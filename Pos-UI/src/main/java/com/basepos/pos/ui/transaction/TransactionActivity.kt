package com.basepos.pos.ui.transaction

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.basepos.pos.ui.keyexchange.KeyExchangeViewModel
import com.basepos.pos.common.domain.models.AppResponseCodes
import com.basepos.pos.common.data.repository.LocationProvider
import com.basepos.pos.common.domain.enums.AccountType
import com.basepos.pos.common.domain.enums.TransactionType
import com.basepos.pos.common.domain.models.EmvResult
import com.basepos.pos.common.domain.models.EmvTransactionDetails
import com.basepos.pos.common.domain.models.LocationResult
import com.basepos.pos.common.domain.models.PaymentRequest
import com.basepos.pos.common.domain.models.TransactionResponse
import com.basepos.pos.common.utils.AmountUtils
import com.basepos.pos.common.utils.Constants
import com.basepos.pos.common.utils.PermissionHelper
import com.basepos.pos.common.utils.hideKeyboard
import com.basepos.pos.common.utils.showAlertDialog
import com.basepos.pos.common.utils.showSingleChoiceDialog
import com.basepos.pos.host.iso.transaction.TransactionResult
import com.basepos.pos.ui.R
import com.basepos.pos.ui.databinding.ActivityTransactionBinding
import com.basepos.pos.ui.databinding.DialogAccountTypeBinding
import com.basepos.pos.ui.databinding.DialogAdminPinBinding
import com.basepos.pos.ui.keyexchange.KeyExchangeScreenResult
import com.basepos.pos.ui.printer.PrintState
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TransactionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTransactionBinding
    private val viewModel: TransactionViewModel by viewModels()
    private val keyExchangeViewModel: KeyExchangeViewModel by viewModels()
    private var canGoBack = true


    @Inject
    lateinit var locationProvider: LocationProvider

    // Retrieve the incoming intent
    private var incomingIntent = lazy { intent }

    private lateinit var paymentRequest: PaymentRequest

    private fun processIntentData() {
        incomingIntent.value?.let { intent ->
            val transactionJson = intent.getStringExtra("requestData")
            paymentRequest = Gson().fromJson(transactionJson, PaymentRequest::class.java)
            viewModel.processIntentSteps(paymentRequest)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        launchPermissionRequest()
        observeEmvProcess()
        observeTransactionProcess()
        observePrintState()
    }

    private fun launchPermissionRequest() {
        val permissionHelper = PermissionHelper(this)
        permissionHelper.requestPermissions(
            permissionHelper.permissions(),
            object : PermissionHelper.PermissionCallback {
                override fun onPermissionsGranted() {
                    if (viewModel.shouldDoKeyExchange()) {
                        keyExchangeViewModel.fetchTerminalParameters()
                        observeKeyExchangeResult()
                    } else {
                        fetchLocation()
                    }
                }
            })
    }

    private fun fetchLocation() {
        locationProvider.getLocation { result ->
            when (result) {
                is LocationResult.NoPermission -> {
                    showAlertDialog(
                        title = getString(R.string.permission),
                        message = getString(R.string.location_permission_is_required_to_continue),
                        showNegativeButton = false,
                        onPositiveButtonPressed = {
                            launchPermissionRequest()
                        }
                    )
                }

                is LocationResult.NotEnabled -> {
                    showAlertDialog(
                        title = getString(R.string.permission),
                        message = getString(R.string.enable_location_message),
                        showNegativeButton = false,
                        onPositiveButtonPressed = {
                            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                            startActivity(intent)

                            startIntent(
                                status = AppResponseCodes.CANCEL.code,
                                statusMessage = getString(R.string.enable_location_message)
                            )
                        }
                    )
                }

                LocationResult.Success -> processIntentData()
            }
        }
    }

    private fun observeKeyExchangeResult() {
        keyExchangeViewModel.keyExchangeResultState.observe(this) { state ->
            when (state) {
                is KeyExchangeScreenResult.Success -> {
                    fetchLocation()
                }

                is KeyExchangeScreenResult.ExchangeError -> {
                    showAlertDialog(
                        title = getString(R.string.key_exchange),
                        message = state.message,
                        positiveTitle = getString(R.string.retry)
                    ) { retry ->
                        if (retry) {
                            fetchLocation()
                        } else {
                            startIntent(
                                status = AppResponseCodes.CANCEL.code,
                                statusMessage = getString(R.string.key_exchange_failed)
                            )
                        }
                    }
                }

                is KeyExchangeScreenResult.PinPadError -> {
                    showAlertDialog(
                        title = getString(R.string.key_exchange),
                        message = state.message,
                        positiveTitle = getString(R.string.retry),
                    ) { retry ->
                        if (retry) {
                            fetchLocation()
                        } else {
                            startIntent(
                                status = AppResponseCodes.CANCEL.code,
                                statusMessage = getString(R.string.key_exchange_failed)
                            )
                        }
                    }
                }

                is KeyExchangeScreenResult.Loading -> {
                    binding.gifCheckCard.isVisible = false
                    binding.tvProcessMessage.text = getString(R.string.please_wait_downloading_keys)
                }

                else -> Unit
            }
        }
    }

    private fun observeTransactionProcess() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.transactionResultState.collectLatest { state ->
                    when (state) {
                        is TransactionResult.Error -> {
                            startIntent(
                                status = AppResponseCodes.INVALID_FORMAT.code,
                                statusMessage = state.message
                            )
                        }

                        is TransactionResult.Loading -> {
                            //binding.insertLogo.isVisible = false
                        }

                        is TransactionResult.OnSuccess -> {
                            if (paymentRequest.transType == TransactionType.BALANCE) {
                                showCardBalanceDialog(state.transactionResponse)
                            } else {
                                if (paymentRequest.print == true) {
                                    viewModel.printReceipt(
                                        state.transactionResponse,
                                        "***** CUSTOMER COPY *****"
                                    )
                                } else {
                                    handleTransactionResponse(state.transactionResponse)
                                }
                            }
                        }

                        is TransactionResult.AutoReversal -> {
                            binding.tvProcessMessage.text =
                                getString(R.string.auto_reversal_in_progress)
                            viewModel.doIsoTransaction(state.emvTransactionDetails, state.paymentRequest)
                        }

                        is TransactionResult.Timeout -> {
                            viewModel.printReceipt(
                                state.paymentResponse,
                                "***** CUSTOMER COPY *****"
                            )
                        }

                        is TransactionResult.Idle -> {}

                        is TransactionResult.ShowAdminPinDialog -> {
                            showAdminPinDialog(state.paymentRequest, state.adminPin)
                        }

                        is TransactionResult.ShowAccountTypeDialog -> {
                            showAccountTypeDialog(state.paymentRequest)
                        }
                    }
                }
            }
        }
    }

    private fun observePrintState() {
        viewModel.printState.observe(this) { printResult ->
            when (printResult) {
                is PrintState.Idle -> Unit
                is PrintState.PrintDone -> {
                    handleTransactionResponse(printResult.transactionResponse)
                }

                is PrintState.Printing -> {
                    binding.tvProcessMessage.text = "Printing..."
                }

                is PrintState.PrintError -> {
                    showAlertDialog(
                        title = "Printer",
                        message = printResult.message,
                        positiveTitle = "Retry",
                        onPositiveButtonPressed = {
                            if (it) {
                                viewModel.printReceipt(
                                    paymentResponse = printResult.transactionResponse,
                                    receiptType = printResult.receiptType
                                )
                            } else {
                                handleTransactionResponse(printResult.transactionResponse)
                            }
                        }
                    )
                }

                is PrintState.PrintMerchantCopy -> {
                    showAlertDialog(
                        title = "Printer",
                        message = "Print Merchant Copy",
                        onPositiveButtonPressed = {
                            if (it) {
                                viewModel.printReceipt(
                                    paymentResponse = printResult.transactionResponse,
                                    receiptType = "***** MERCHANT COPY *****"
                                )

                                viewModel.setMerchantCopyPrinted(true)
                            } else {
                                handleTransactionResponse(printResult.transactionResponse)
                            }
                        }
                    )
                }
            }
        }
    }

    private fun observeEmvProcess() {
        viewModel.emvResultState.observe(this) { emvResult ->
            emvResult ?: return@observe
            when (emvResult) {
                is EmvResult.Loading -> {
                    binding.tvProcessMessage.text = emvResult.message
                }

                is EmvResult.Error -> {
                    startIntent(
                        status = AppResponseCodes.CANCEL.code,
                        statusMessage = emvResult.message
                    )

                    viewModel.cleanUp()
                }

                is EmvResult.OnRequestOnline -> {
                    handleOnOnlineRequest(emvResult.emvTransactionDetails)
                }

                is EmvResult.OnPinInput -> {
                    handleOnInputPin(emvResult.pinLength)
                }

                is EmvResult.OnPinInputRequired -> {
                    handleOnPinInputRequired(emvResult.isOnlinePin, emvResult.offlinePinTrialCount)
                }

                else -> Unit
            }
        }
    }

    private fun handleOnPinInputRequired(isOnlinePin: Boolean, offlinePinTrialCount: Int) {
        binding.insertCardLayout.isVisible = false
        binding.pinEntryLayout.isVisible = true
        resetPinView(0)

        val amount = paymentRequest.amount
        binding.tvAmount.text =
            "Amount: NGN${AmountUtils.formatToTwoDecimalPlaces(amount)}"
        binding.pinType.text = if (isOnlinePin) "Online PIN" else "Offline PIN"
        binding.pinTrialCounter.isVisible = isOnlinePin.not()
        binding.pinTrialCounter.text = "PIN Trials Remaining: ${offlinePinTrialCount}"

        if (paymentRequest.transType == TransactionType.BALANCE) {
            binding.tvAmount.isVisible = false
        }
    }

    private fun handleOnOnlineRequest(emvTransactionDetails: EmvTransactionDetails) {
        binding.insertCardLayout.isVisible = true
        binding.pinEntryLayout.isVisible = false
        resetPinView(0)

        binding.gifCheckCard.isVisible = false

        binding.tvProcessMessage.text = getString(R.string.processing_transaction)
        Toast.makeText(
            this@TransactionActivity,
            getString(R.string.pin_accepted),
            Toast.LENGTH_SHORT
        ).show()

        viewModel.doIsoTransaction(emvTransactionDetails, paymentRequest)
    }

    private fun handleOnInputPin(pinLength: Int) {
        canGoBack = false
        resetPinView(pinLength)
    }

    private fun resetPinView(pinLength: Int) {
        binding.pinContainer.removeAllViews()

        val asteriskTextView = createAsteriskTextView(pinLength)
        binding.pinContainer.addView(asteriskTextView)
    }

    private fun createAsteriskTextView(length: Int): TextView {
        val textView = TextView(this)
        textView.textSize = 30f
        textView.setTextColor(getColor(com.basepos.pos.common.R.color.black))
        textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        textView.text = "*".repeat(length)
        return textView
    }

    private fun showAccountTypeDialog(paymentRequest: PaymentRequest) {
        showSingleChoiceDialog(
            title = "Select Account Type",
            items = listOf("Credit", "Default", "Savings", "Current").toTypedArray(),
            onItemSelected = { _, selectedItem ->
                when (selectedItem) {
                    "Credit" -> paymentRequest.accountType = AccountType.CREDIT
                    "Default" -> paymentRequest.accountType = AccountType.DEFAULT
                    "Savings" -> paymentRequest.accountType = AccountType.SAVINGS
                    "Current" -> paymentRequest.accountType = AccountType.CURRENT
                }
                viewModel.processIncomingIntentNextStep(paymentRequest)
                },
            onCancelled = {
                startIntent(
                    status = AppResponseCodes.CANCEL.code,
                    statusMessage = "Account type selection cancelled"
                )
            }
        )
    }

    private fun showCardBalanceDialog(paymentResponse: TransactionResponse) {
        showAlertDialog(
            title = "Card Balance",
            message = "Card balance is ${paymentResponse.currency} ${
                AmountUtils.formatToTwoDecimalPlaces(
                    paymentResponse.amount
                )
            }\n\nDo you want to print receipt?",
            onPositiveButtonPressed = {
                if (it) {
                    viewModel.printReceipt(
                        paymentResponse = paymentResponse,
                        receiptType = "***** CUSTOMER COPY *****"
                    )
                } else {
                    handleTransactionResponse(paymentResponse)
                }
            }
        )
    }

    private fun showAdminPinDialog(paymentRequest: PaymentRequest, pin: String?) {
        val dialogBinding = DialogAdminPinBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()
        dialog.setCanceledOnTouchOutside(false)
        dialogBinding.buttonConfirmationAccept.setOnClickListener {
            it.hideKeyboard()

            val adminPIN = dialogBinding.edtAdminPin.text.toString()

            if (adminPIN.isBlank() || adminPIN.length < 6) {
                startIntent(
                    status = AppResponseCodes.CANCEL.code,
                    statusMessage = getString(R.string.invalid_admin_pin)
                )
                return@setOnClickListener
            }

            if (adminPIN != pin) {
                startIntent(
                    status = AppResponseCodes.CANCEL.code,
                    statusMessage = getString(R.string.wrong_admin_pin)
                )
                return@setOnClickListener
            }

            dialog.dismiss()
            viewModel.selectAccountType(paymentRequest)
        }
        dialogBinding.buttonConfirmationCancel.setOnClickListener {
            dialog.dismiss()
            startIntent(
                status = AppResponseCodes.CANCEL.code,
                statusMessage = getString(R.string.admin_pin_cancelled)
            )
        }
        dialog.show()
    }

    private fun handleTransactionResponse(paymentResponse: TransactionResponse) {
        when (paymentResponse.statusCode) {
            Constants.ISO_SUCCESS -> {
                startIntent(
                    status = AppResponseCodes.SUCCESS.code,
                    data = Gson().toJson(paymentResponse)
                )
            }

            else -> {
                startIntent(
                    status = if (paymentResponse.message.contains("TimeOut")) AppResponseCodes.TIMEOUT.code else AppResponseCodes.FAILED.code,
                    data = Gson().toJson(paymentResponse)
                )
            }
        }
    }

    /**
     * This is implemented so that users do not mistakenly
     * press the back button when the transaction is already ongoing.
     */
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        if (!canGoBack) {
            return;
        }

        super.onBackPressed()
        viewModel.cleanUp()
        startIntent(AppResponseCodes.CANCEL.code, "")
    }

    private fun startIntent(
        status: String,
        statusMessage: String? = null,
        data: String? = null
    ) {
        val responseIntent = Intent()
        responseIntent.putExtra("status", status)
        responseIntent.putExtra("data", data)
        responseIntent.putExtra("statusMessage", statusMessage)

        setResult(RESULT_OK, responseIntent)
        finish()
    }
}