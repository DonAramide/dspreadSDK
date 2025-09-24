package com.basepos.pos.host.iso.transaction

import android.content.Context
import com.basepos.pos.common.domain.enums.TransactionType
import com.basepos.pos.common.domain.models.EmvTransactionDetails
import com.basepos.pos.common.domain.models.PaymentRequest
import com.basepos.pos.common.domain.models.TransactionResponse
import com.basepos.pos.common.domain.repository.SessionManager
import com.basepos.pos.common.utils.AmountUtils
import com.basepos.pos.common.utils.Constants
import com.basepos.pos.common.utils.ISOUtils
import com.basepos.pos.common.utils.UtilMethods
import com.basepos.pos.host.R
import com.basepos.pos.host.iso.data.local.ProcessedTransactionDao
import com.basepos.pos.host.iso.data.local.entities.ProcessedTransactionEntity
import com.basepos.pos.host.iso.data.local.entities.toTransactionResponse
import com.basepos.pos.host.iso.data.remote.socket.SocketChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.jpos.iso.ISOException
import org.jpos.iso.ISOMsg
import timber.log.Timber
import java.io.EOFException
import java.io.IOException
import java.net.SocketException
import javax.inject.Inject

/**
 * @Author: ifechukwu.udorji
 * @Date: 7/4/2024
 */

sealed class TransactionResult {
    data object Idle: TransactionResult()
    data class Error(val message: String): TransactionResult()
    data object Loading: TransactionResult()
    data class AutoReversal(val emvTransactionDetails: EmvTransactionDetails, val paymentRequest: PaymentRequest): TransactionResult()
    data class OnSuccess(val transactionResponse: TransactionResponse): TransactionResult()
    data class Timeout(val paymentResponse: TransactionResponse) : TransactionResult()
    data class ShowAccountTypeDialog(val paymentRequest: PaymentRequest) : TransactionResult()
    data class ShowAdminPinDialog(
        val paymentRequest: PaymentRequest,
        val adminPin: String?
    ) : TransactionResult()
}

class TransactionHandler @Inject constructor(
    private val context: Context,
    private val sessionManager: SessionManager,
    private val socketChannel: SocketChannel,
    private val isoMessageBuilder: IsoMessageBuilder,
    private val transactionDao: ProcessedTransactionDao
) {
    val transactionResult = MutableStateFlow<TransactionResult>(TransactionResult.Idle)

    private var isoRequest: ISOMsg? = null
    private var processedTransaction: ProcessedTransactionEntity? = null

    suspend fun doIsoTransaction(emvTransactionDetails: EmvTransactionDetails, paymentRequest: PaymentRequest) {
        try {
            val originalTransaction = fetchOriginalTransactionIfNeeded(paymentRequest)
            if (originalTransaction != null && shouldCheckCardMatch(paymentRequest)) {
                if (originalTransaction.cardInfo?.pan != emvTransactionDetails.cardInfo?.pan) {
                    transactionResult.update {
                        TransactionResult.Error(context.getString(R.string.card_not_match_original_transaction))
                    }
                    return
                }
            }

            val channel = socketChannel.setup().apply { connect() }
            val isoMessageRequest =
                isoMessageBuilder.buildIsoMessage(emvTransactionDetails, originalTransaction, paymentRequest)
            this.isoRequest = isoMessageRequest

            channel.send(isoMessageRequest)
            val response = channel.receive()
            channel.disconnect()

            processedTransaction = processTransactionResponse(
                emvTransactionDetails,
                originalTransaction,
                paymentRequest,
                response,
                isoMessageRequest
            )
            enqueueTransactionNotification(processedTransaction!!.rrn)

            transactionResult.update {
                TransactionResult.OnSuccess(
                    processedTransaction!!.toTransactionResponse()
                )
            }
        } catch (e: EOFException) {
            handleNetworkFailure(
                emvTransactionDetails,
                paymentRequest,
                context.getString(R.string.host_disconnect)
            )
        } catch (e: SocketException) {
            handleNetworkFailure(
                emvTransactionDetails,
                paymentRequest,
                context.getString(R.string.host_disconnect)
            )
        } catch (e: IOException) {
            handleNetworkFailure(
                emvTransactionDetails,
                paymentRequest,
                context.getString(R.string.network_error_please_check_your_connection_and_try_again)
            )
        } catch (e: ISOException) {
            handleError(paymentRequest.rrn!!, context.getString(R.string.error_packing_message), e)
        } catch (e: Exception) {
            handleError(paymentRequest.rrn!!, context.getString(R.string.an_error_occurred), e)
        } finally {
            if (processedTransaction == null) {
                processedTransaction = processTransactionResponse(
                    emvTransactionDetails,
                    fetchOriginalTransactionIfNeeded(paymentRequest),
                    paymentRequest,
                    null,
                    this.isoRequest
                )
                enqueueTransactionNotification(processedTransaction!!.rrn)
            }
        }
    }

    private suspend fun fetchOriginalTransactionIfNeeded(paymentRequest: PaymentRequest): ProcessedTransactionEntity? {
        return when (paymentRequest.transType) {
            TransactionType.REFUND, TransactionType.PREAUTHCOMPLETE, TransactionType.REVERSAL, TransactionType.AUTOREVERSAL -> {
                transactionDao.fetchValidTransactionByRrnOrStan(
                    paymentRequest.rrn,
                    paymentRequest.stan
                )
            }

            else -> null
        }
    }

    private fun shouldCheckCardMatch(paymentRequest: PaymentRequest): Boolean {
        return paymentRequest.transType != TransactionType.AUTOREVERSAL
    }

    private suspend fun processTransactionResponse(
        emvTransactionDetails: EmvTransactionDetails,
        originalTransaction: ProcessedTransactionEntity?,
        paymentRequest: PaymentRequest,
        response: ISOMsg?,
        request: ISOMsg?
    ): ProcessedTransactionEntity {
        val responseCode = response?.getString(39) ?: ""
        val authCode = response?.getString(38)
        val hostTransactionDateTime = getHostTransactionDateTime(response, request)
        val additionalAmount = getAdditionalAmount(response)
        val transactionAmount =
            calculateTransactionAmount(emvTransactionDetails, paymentRequest, additionalAmount)

        val processedTransaction = ProcessedTransactionEntity(
            amount = transactionAmount,
            cashbackAmount = paymentRequest.cashbackAmount,
            hostDateTime = hostTransactionDateTime,
            message = ISOUtils.getNibssMessage(responseCode),
            rrn = response?.getString(37) ?: paymentRequest.rrn ?: "",
            stan = response?.getString(11) ?: paymentRequest.stan ?: "",
            statusCode = responseCode,
            authCode = authCode,
            transactionType = paymentRequest.transType,
            forwardingInstitutionId = response?.getString(33),
            createdDate = System.currentTimeMillis(),
            transactionDate = response?.getString(13) ?: request?.getString(13) ?: "",
            transactionTime = response?.getString(12) ?: request?.getString(12) ?: "",
            transactionProcessCode = response?.getString(3) ?: request?.getString(3) ?: "",
            transactionMti = request?.mti ?: "",
            dateTime = UtilMethods.formatDateToCustomFormat(System.currentTimeMillis()),
            accountType = originalTransaction?.accountType ?: emvTransactionDetails.accountType,
            cardInfo = originalTransaction?.cardInfo ?: emvTransactionDetails.cardInfo,
            terminalInfo = originalTransaction?.terminalInfo ?: emvTransactionDetails.terminalInfo
        )

        saveProcessedTransaction(
            paymentRequest,
            processedTransaction,
            originalTransaction,
            responseCode
        )

        return processedTransaction
    }

    private fun enqueueTransactionNotification(rrn: String) {
        //Todo -> Transaction should be posted to a db service
    }

    private fun handleError(rrn: String, message: String, exception: Exception) {
        Timber.e(exception)
        enqueueTransactionNotification(rrn)
        transactionResult.update { TransactionResult.Error(message) }
    }

    private suspend fun handleNetworkFailure(
        emvDetails: EmvTransactionDetails,
        paymentRequest: PaymentRequest,
        message: String
    ) {
        enqueueTransactionNotification(paymentRequest.rrn!!)

        when (paymentRequest.transType) {
            TransactionType.BALANCE, TransactionType.REFUND, TransactionType.REVERSAL -> {
                transactionResult.update { TransactionResult.Error(message) }
            }

            else -> {
                val paymentRequestUpdate =
                    paymentRequest.copy(transType = TransactionType.AUTOREVERSAL)
                val paymentResponse =
                    processTransactionResponse(
                        emvDetails,
                        fetchOriginalTransactionIfNeeded(paymentRequest),
                        paymentRequestUpdate,
                        null,
                        isoRequest
                    )

                if (paymentRequest.transType == TransactionType.AUTOREVERSAL) {
                    transactionResult.update {
                        val transactionResponse = paymentResponse.toTransactionResponse()
                        transactionResponse.transactionType = null
                        transactionResponse.message =
                            context.getString(R.string.transaction_timeout)
                        TransactionResult.Timeout(transactionResponse)
                    }
                } else {
                    transactionResult.update {
                        TransactionResult.AutoReversal(
                            emvDetails,
                            paymentRequestUpdate
                        )
                    }
                }
            }
        }
    }

    private fun getHostTransactionDateTime(response: ISOMsg?, request: ISOMsg?): String {
        return when {
            response != null && response.hasField(7) -> response.getString(7)
            response != null -> "${response.getString(13)}${response.getString(12)}"
            else -> request?.getString(7) ?: ""
        }
    }

    private fun getAdditionalAmount(response: ISOMsg?): Double {
        return if (response?.hasField(54) == true) {
            ISOUtils.extractAmountFromField54(response.getString(54))
        } else {
            0.00
        }
    }

    private fun calculateTransactionAmount(
        emvDetails: EmvTransactionDetails,
        paymentRequest: PaymentRequest,
        additionalAmount: Double
    ): Double {
        return when (paymentRequest.transType) {
            TransactionType.BALANCE -> additionalAmount
            TransactionType.REVERSAL, TransactionType.AUTOREVERSAL -> emvDetails.amount
            else -> subtractCashbackAmount(
                emvDetails.amount,
                paymentRequest.cashbackAmount
            )
        }
    }

    private fun subtractCashbackAmount(amount: Double, cashbackAmount: Double): Double {
        return amount - cashbackAmount
    }

    private suspend fun saveProcessedTransaction(
        paymentRequest: PaymentRequest,
        processedTransaction: ProcessedTransactionEntity,
        originalTransaction: ProcessedTransactionEntity?,
        responseCode: String
    ) {
        if (paymentRequest.transType != TransactionType.BALANCE) {
            if (paymentRequest.transType == TransactionType.REFUND) {
                processedTransaction.isValid = false
            }
            transactionDao.insertTransaction(processedTransaction)
        }

        if (responseCode == Constants.ISO_SUCCESS) {
            when (paymentRequest.transType) {
                TransactionType.REVERSAL, TransactionType.AUTOREVERSAL -> {
                    transactionDao.deleteProcessedTransactionByRrnAndType(
                        originalTransaction!!.rrn,
                        paymentRequest.transType
                    )
                }

                TransactionType.REFUND, TransactionType.PREAUTHCOMPLETE -> {
                    originalTransaction?.isValid = false
                    transactionDao.insertTransaction(originalTransaction!!)
                }

                else -> {}
            }
        }
    }
}