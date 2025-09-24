package com.basepos.pos.ui.transaction

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.basepos.pos.common.domain.enums.TransactionType
import com.basepos.pos.common.domain.models.EmvResult
import com.basepos.pos.common.domain.models.EmvTransactionDetails
import com.basepos.pos.common.domain.models.PaymentRequest
import com.basepos.pos.common.domain.models.Receipt
import com.basepos.pos.common.domain.models.ReceiptFields
import com.basepos.pos.common.domain.models.TransactionResponse
import com.basepos.pos.common.domain.printer.PrinterEngine
import com.basepos.pos.common.domain.printer.PrinterHelper
import com.basepos.pos.common.domain.printer.PrinterState
import com.basepos.pos.common.domain.repository.EmvListener
import com.basepos.pos.common.domain.repository.SessionManager
import com.basepos.pos.common.utils.AmountUtils
import com.basepos.pos.common.utils.FileDownloadUtils
import com.basepos.pos.common.utils.ISOUtils
import com.basepos.pos.host.iso.data.local.ProcessedTransactionDao
import com.basepos.pos.host.iso.transaction.TransactionHandler
import com.basepos.pos.host.iso.transaction.TransactionResult
import com.basepos.pos.ui.R
import com.basepos.pos.ui.printer.PrintState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val emvListener: EmvListener,
    private val printerEngine: PrinterEngine,
    private val printerHelper: PrinterHelper,
    private val fileDownloadUtils: FileDownloadUtils,
    private val transactionDao: ProcessedTransactionDao,
    private val transactionHandler: TransactionHandler,
    private val sessionManager: SessionManager
) : ViewModel() {
    var emvResultState = MutableLiveData<EmvResult>(EmvResult.Idle)
        private set
    private var _transactionResultState: MutableStateFlow<TransactionResult> = transactionHandler.transactionResult
    val transactionResultState = _transactionResultState.asStateFlow()

    private val _printState = MutableLiveData<PrintState>(PrintState.Idle)
    val printState: LiveData<PrintState> = _printState

    private var merchantCopyPrinted = false

    companion object {
        private const val MIN_AMOUNT = 1.00
        private const val MIN_CASHBACK = 1.00
        private const val MIN_RRN_LENGTH = 12
        private const val MIN_STAN_LENGTH = 6
    }

    sealed class ValidationResult {
        data object Success : ValidationResult()
        data class Error(val message: String) : ValidationResult()
    }

    fun shouldDoKeyExchange(): Boolean = with(sessionManager) {
        when {
            getTmsParameter() == null || getHostParameters() == null -> true
            else -> false
        }
    }

    fun processIntentSteps(paymentRequest: PaymentRequest) {
        when (val result = validatePaymentRequest(paymentRequest)) {
            is ValidationResult.Success -> handleValidPaymentRequest(paymentRequest)
            is ValidationResult.Error -> _transactionResultState.update {
                TransactionResult.Error(
                    result.message
                )
            }
        }
    }

    private fun validatePaymentRequest(paymentRequest: PaymentRequest): ValidationResult {
        if (!TransactionType.entries.contains(paymentRequest.transType)) {
            return ValidationResult.Error("Transaction type (${paymentRequest.transType} not supported")
        }

        if (!checkTransactionTypeEnabled(paymentRequest.transType)) {
            return ValidationResult.Error("Transaction type (${paymentRequest.transType} not enabled")
        }

        return ValidationResult.Success
    }

    private fun handleValidPaymentRequest(paymentRequest: PaymentRequest) {
        when (paymentRequest.transType) {
            TransactionType.REVERSAL,
            TransactionType.REFUND,
            TransactionType.PREAUTHCOMPLETE -> showAdminPinDialog(paymentRequest)

            else -> selectAccountType(paymentRequest)
        }
    }

    private fun showAdminPinDialog(paymentRequest: PaymentRequest) {
        _transactionResultState.update {
            TransactionResult.ShowAdminPinDialog(paymentRequest, "000000")
        }
    }

    fun selectAccountType(paymentRequest: PaymentRequest) {
        _transactionResultState.update {
            TransactionResult.ShowAccountTypeDialog(paymentRequest)
        }
    }

    fun processIncomingIntentNextStep(paymentRequest: PaymentRequest) {
        when (val result = validateAmounts(paymentRequest)) {
            is ValidationResult.Success -> processValidatedTransaction(paymentRequest)
            is ValidationResult.Error -> _transactionResultState.update {
                TransactionResult.Error(
                    result.message
                )
            }
        }
    }

    private fun validateAmounts(paymentRequest: PaymentRequest): ValidationResult {
        val amount = paymentRequest.amount
        val cashbackAmount = paymentRequest.cashbackAmount

        if (amount < MIN_AMOUNT) {
            return ValidationResult.Error(context.getString(R.string.transaction_amount_cannot_be_less_than_1))
        }

        if (paymentRequest.transType == TransactionType.PURCHASEWITHCB) {
            if (cashbackAmount < MIN_CASHBACK) {
                return ValidationResult.Error(context.getString(R.string.cashback_amount_cannot_be_less_than_1))
            }
            if (cashbackAmount > amount) {
                return ValidationResult.Error(context.getString(R.string.cashback_amount_greater_than_transaction_amount))
            }
        }

        return ValidationResult.Success
    }

    private fun processValidatedTransaction(paymentRequest: PaymentRequest) {
        ensureTransactionReferences(paymentRequest)

        when (paymentRequest.transType) {
            TransactionType.REVERSAL,
            TransactionType.REFUND,
            TransactionType.PREAUTHCOMPLETE -> validateOldTransaction(paymentRequest)

            else -> {
                val totalAmount = AmountUtils.addCashbackToAmount(
                    amount = paymentRequest.amount,
                    cashbackAmount = paymentRequest.cashbackAmount
                )
                paymentRequest.amount = totalAmount
                startCardEMV(paymentRequest)
            }
        }
    }

    private fun ensureTransactionReferences(paymentRequest: PaymentRequest) {
        when (paymentRequest.transType) {
            TransactionType.REVERSAL,
            TransactionType.REFUND,
            TransactionType.PREAUTHCOMPLETE -> {
                if (!validateRrnAndStan(paymentRequest)) {
                    return
                }
            }

            else -> Unit
        }


        if (paymentRequest.stan.isNullOrEmpty()) {
            paymentRequest.stan = ISOUtils.getStan()
        }
        if (paymentRequest.rrn.isNullOrEmpty()) {
            paymentRequest.rrn = ISOUtils.generateRetrievalReferenceNumber(paymentRequest.stan!!)
        }
    }

    private fun validateRrnAndStan(paymentRequest: PaymentRequest): Boolean {
        val rrn = paymentRequest.rrn
        val stan = paymentRequest.stan

        // Check if both RRN and STAN are null or empty
        if (rrn.isNullOrEmpty() && stan.isNullOrEmpty()) {
            _transactionResultState.update {
                TransactionResult.Error(context.getString(R.string.rrn_stan_cannot_be_null))
            }
            return false
        }

        // Validate RRN length
        if (rrn != null && rrn.length < MIN_RRN_LENGTH) {
            _transactionResultState.update {
                TransactionResult.Error(context.getString(R.string.rrn_cannot_be_less_than_12))
            }
            return false
        }

        // Validate STAN length
        if (stan != null && stan.length < MIN_STAN_LENGTH) {
            _transactionResultState.update {
                TransactionResult.Error(context.getString(R.string.stan_cannot_be_less_than_six))
            }
            return false
        }

        return true
    }

    private fun validateOldTransaction(paymentRequest: PaymentRequest) =
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val transactionType = paymentRequest.transType

                val originalTransaction =
                    transactionDao.fetchValidTransactionByRrnOrStan(
                        rrn = paymentRequest.rrn,
                        stan = paymentRequest.stan
                    )

                if (originalTransaction == null) {
                    _transactionResultState.update {
                        TransactionResult.Error(context.getString(R.string.transaction_with_rrn_stan_not_found))
                    }
                    return@launch
                }

                if (transactionType == TransactionType.PREAUTHCOMPLETE) {
                    if (originalTransaction.transactionType != TransactionType.PREAUTH) {
                        _transactionResultState.update {
                            TransactionResult.Error(context.getString(R.string.you_cannot_do_completion_on_this_transaction))
                        }
                        return@launch
                    }
                }

                val cashbackAmount =
                    if (originalTransaction.transactionType == TransactionType.PURCHASEWITHCB) originalTransaction.cashbackAmount
                    else 0.00

                val amount =
                    when (transactionType) {
                        TransactionType.REVERSAL,
                        TransactionType.REFUND -> originalTransaction.amount

                        else -> paymentRequest.amount
                    }


                val totalAmount = AmountUtils.addCashbackToAmount(amount, cashbackAmount)

                if (transactionType == TransactionType.REFUND) {
                    if (paymentRequest.amount > totalAmount) {
                        _transactionResultState.update {
                            TransactionResult.Error(
                                context.getString(R.string.refund_amount_cannot_be_greater_than_original_amount)
                            )
                        }
                        return@launch
                    }
                }

                /**
                 * REFUND needs to use the amount passed by the user
                 * from the [PaymentRequest]
                 */
                when (transactionType) {
                    TransactionType.REFUND -> {
                        startCardEMV(paymentRequest)
                    }

                    else -> {
                        paymentRequest.amount = totalAmount
                        startCardEMV(paymentRequest)
                    }
                }
            } catch (e: Exception) {
                Timber.e(e)
                _transactionResultState.update {
                    TransactionResult.Error(
                        e.message ?: context.getString(R.string.an_error_occurred)
                    )
                }
            }
        }

    private fun startCardEMV(paymentRequest: PaymentRequest) {
        viewModelScope.launch {
            try {
                emvListener.startCardTransaction(paymentRequest, ::updateEmvResult)
            } catch (exception: Exception) {
                exception.printStackTrace()
                emvResultState.postValue(
                    EmvResult.Error(
                        exception.message ?: context.getString(R.string.an_error_occurred)
                    )
                )
                cleanUp()
            }
        }
    }

    private fun updateEmvResult(emvResult: EmvResult) {
        emvResultState.postValue(emvResult)
    }

    fun doIsoTransaction(
        emvTransactionDetails: EmvTransactionDetails,
        paymentRequest: PaymentRequest
    ) = viewModelScope.launch(Dispatchers.IO) {
        transactionHandler.doIsoTransaction(emvTransactionDetails, paymentRequest)
    }

    fun printReceipt(paymentResponse: TransactionResponse, receiptType: String) =
        viewModelScope.launch {
            val receipt = createReceipt(paymentResponse, receiptType)
            handlePrinting(receipt, paymentResponse)
        }

    private fun createReceipt(paymentResponse: TransactionResponse, receiptType: String): Receipt {
        return Receipt(
            receipt = listOf(
                ReceiptFields(
                    logoPath = fileDownloadUtils.getLogoPath() ?: "",
                    stringFields = printerHelper.setupTransactionPrintFields(
                        receiptType,
                        paymentResponse
                    )
                )
            )
        )
    }

    private suspend fun handlePrinting(receipt: Receipt, paymentResponse: TransactionResponse) {
        printerEngine.printReceipt(receipt.receipt[0]).collectLatest { state ->
            when (state) {
                is PrinterState.Done -> handlePrintDone(paymentResponse)
                is PrinterState.Error -> handlePrintError(state.message, paymentResponse)
                is PrinterState.Printing -> Timber.d("Printing in progress")
            }
        }
    }

    private fun handlePrintDone(paymentResponse: TransactionResponse) {
        //Only print merchant copy if not already printed and
        //transaction type is not balance and the transaction is approved
        if (paymentResponse.transactionType == TransactionType.BALANCE) {
            _printState.postValue(PrintState.PrintDone(paymentResponse))
            return
        }

        if (merchantCopyPrinted || !paymentResponse.isApproved()) {
            _printState.postValue(PrintState.PrintDone(paymentResponse))
            return
        }

        _printState.postValue(PrintState.PrintMerchantCopy(paymentResponse))
    }

    private fun handlePrintError(errorMessage: String, paymentResponse: TransactionResponse) {
        val type =
            if (merchantCopyPrinted) "***** MERCHANT COPY *****" else "***** CUSTOMER COPY *****"
        _printState.postValue(PrintState.PrintError(errorMessage, type, paymentResponse))
    }

    private fun checkTransactionTypeEnabled(transactionType: TransactionType): Boolean {
        return when (transactionType) {
            TransactionType.PURCHASE,
            TransactionType.BALANCE,
            TransactionType.REVERSAL,
            TransactionType.AUTOREVERSAL,
            TransactionType.PURCHASEWITHCB,
            TransactionType.CASHADVANCE,
            TransactionType.REFUND,
            TransactionType.PREAUTH,
            TransactionType.PREAUTHCOMPLETE -> true

            else -> false
        }
    }

    fun setMerchantCopyPrinted(isPrinted: Boolean) {
        //We assume that the user has gone ahead to print the merchant copy
        merchantCopyPrinted = isPrinted
    }

    fun cleanUp() = viewModelScope.launch {
        emvListener.closeSdk()
    }
}