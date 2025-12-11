package com.dspreadbaseapp.baseapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.basepos.pos.dspread.MyQposClass
import com.basepos.pos.dspread.interfaces.TransactionCallback
import com.basepos.pos.dspread.interfaces.ConnectStateCallback
import com.dspread.xpos.QPOSService
import timber.log.Timber
import java.util.*

/**
 * Simple test activity that mimics the reference project's payment flow
 * Based on: /Users/mac/Documents/EMV/android-7.0.3/pos_android_studio_demo/pos_android_app
 */
class TestPurchaseActivity : AppCompatActivity(), TransactionCallback, ConnectStateCallback {

    private lateinit var amountEditText: EditText
    private lateinit var startButton: Button
    private lateinit var statusTextView: TextView

    private lateinit var qposService: QPOSService
    private lateinit var myQposClass: MyQposClass
    private var amount: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_purchase)

        initViews()
        initPOS()
    }

    private fun initViews() {
        amountEditText = findViewById(R.id.amountEditText)
        startButton = findViewById(R.id.startButton)
        statusTextView = findViewById(R.id.statusTextView)

        startButton.setOnClickListener {
            startTransaction()
        }
    }

    private fun initPOS() {
        try {
            // Close any existing connections first
            try {
                if (::qposService.isInitialized) {
                    qposService.closeUart()
                }
            } catch (e: Exception) {
                Timber.d("No existing connection to close")
            }

            // Wait a moment for device to be released
            Thread.sleep(500)

            // Initialize exactly like reference project
            qposService = QPOSService.getInstance(this, QPOSService.CommunicationMode.UART)
            qposService.setContext(this)

            // Create MyQposClass instance
            myQposClass = MyQposClass()

            // Set up callbacks BEFORE initializing listener
            myQposClass.setTransactionCallback(this)
            myQposClass.setStateCallback(this)

            // Initialize listener with the MyQposClass instance
            qposService.initListener(myQposClass)

            updateStatus("🔄 Connecting to device...")
            Timber.d("POS initialization started")

            // Try to open UART connection with retry
            openUartWithRetry()

        } catch (e: Exception) {
            Timber.e(e, "Failed to initialize POS")
            updateStatus("❌ Failed to connect to device")
        }
    }

    private fun openUartWithRetry() {
        try {
            qposService.openUart()
        } catch (e: Exception) {
            Timber.w(e, "First connection attempt failed, retrying...")
            updateStatus("🔄 Retrying connection...")

            // Wait and retry once
            Thread.sleep(1000)
            try {
                qposService.openUart()
            } catch (retryException: Exception) {
                Timber.e(retryException, "Retry connection also failed")
                updateStatus("❌ Failed to connect - Device may be occupied")
            }
        }
    }

    private fun startTransaction() {
        try {
            amount = amountEditText.text.toString()
            if (amount.isEmpty()) {
                updateStatus("⚠️ Please enter amount")
                return
            }

            updateStatus("💰 Starting transaction for $amount...")

            // STEP 1: Get device ID first (CRITICAL - from reference project)
            updateStatus("🔍 Getting device ID...")
            val posIdTable = qposService.syncGetQposId(5)
            val posId = posIdTable["posId"] as? String ?: ""
            Timber.d("Device ID retrieved: $posId")
            updateStatus("✅ Device ID: $posId")

            // STEP 2: Set time (from reference project pattern)
            updateStatus("⏰ Setting time...")
            val currentTime = java.text.SimpleDateFormat("yyyyMMddHHmmss", java.util.Locale.getDefault()).format(java.util.Date())
            qposService.sendTime(currentTime)
            Timber.d("Time set: $currentTime")

            // STEP 3: Set card trade mode
            qposService.setCardTradeMode(QPOSService.CardTradeMode.SWIPE_TAP_INSERT_CARD_NOTUP)

            // STEP 4: Set amount (exactly like reference project)
            val currencyCode = 566 // Nigerian Naira
            val amountDouble = amount.toDouble()
            var amountInCents = (amountDouble * 100).toLong().toString().padStart(12, '0')

            // Remove leading zeros to avoid INPUT_INVALID error (as done in DSpreadEmvListener)
            amountInCents = amountInCents.replace(Regex("^0+"), "").takeIf { it.isNotEmpty() } ?: "0"

            val cashbackAmount = "" // Empty string for D60 compatibility

            Timber.d("Setting amount: $amountInCents, cashback: '$cashbackAmount', currency: $currencyCode")

            // Use GOODS transaction type (same as before but with time setting)
            qposService.setAmount(amountInCents, cashbackAmount, currencyCode.toString(), QPOSService.TransactionType.GOODS)

            // STEP 5: Start trade
            qposService.doTrade(60)

            updateStatus("🔄 Transaction started - waiting for callbacks...")

        } catch (e: Exception) {
            Timber.e(e, "Error starting transaction")
            updateStatus("❌ Error: ${e.message}")
        }
    }

    private fun updateStatus(message: String) {
        runOnUiThread {
            statusTextView.text = message
            Timber.d("Status: $message")
        }
    }

    // Transaction Callbacks - Simple Implementation

    override fun onRequestSetAmount() {
        Timber.d("💰 onRequestSetAmount - amount already set")
        updateStatus("💰 Amount confirmed")
    }

    override fun onRequestWaitingUser() {
        Timber.d("🔄 onRequestWaitingUser - ready for card")
        updateStatus("🔄 Ready - Please insert, tap, or swipe your card")
    }

    override fun onDoTradeResult(result: QPOSService.DoTradeResult?, decodeData: Hashtable<String, String>?) {
        Timber.i("💳 Card detected: $result")
        Timber.d("Card data: $decodeData")

        when (result) {
            QPOSService.DoTradeResult.ICC -> {
                updateStatus("🔹 Chip card detected - processing EMV...")
                // Start EMV processing which will trigger PIN request if needed
                qposService.doEmvApp(QPOSService.EmvOption.START)
            }
            QPOSService.DoTradeResult.MCR -> {
                val cardNumber = decodeData?.get("maskedPAN") ?: "****"
                updateStatus("💳 Magnetic stripe card: $cardNumber")
                // Mag stripe cards may also require PIN - wait for callback
            }
            QPOSService.DoTradeResult.NFC_ONLINE, QPOSService.DoTradeResult.NFC_OFFLINE -> {
                val cardNumber = decodeData?.get("maskedPAN") ?: "****"
                updateStatus("📱 Contactless card: $cardNumber")
                // Contactless may require PIN for high amounts
            }
            QPOSService.DoTradeResult.BAD_SWIPE -> {
                updateStatus("⚠️ Bad swipe - please try again")
            }
            else -> {
                updateStatus("❌ Card read failed: $result")
            }
        }
    }

    override fun onRequestTransactionResult(transactionResult: QPOSService.TransactionResult?) {
        Timber.i("🎯 Transaction result: $transactionResult")

        when (transactionResult) {
            QPOSService.TransactionResult.APPROVED -> {
                updateStatus("✅ TRANSACTION APPROVED!")
            }
            QPOSService.TransactionResult.DECLINED -> {
                updateStatus("❌ Transaction declined")
            }
            QPOSService.TransactionResult.CANCEL -> {
                updateStatus("⚠️ Transaction cancelled")
            }
            else -> {
                updateStatus("❌ Transaction failed: $transactionResult")
            }
        }
    }

    override fun onRequestFinalConfirm() {
        Timber.d("💰 Final confirm requested - auto confirming")
        qposService.finalConfirm(true)
        updateStatus("💰 Transaction confirmed")
    }

    // Other required callbacks (minimal implementation)
    override fun onRequestTime() {
        val time = java.text.SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().time)
        qposService.sendTime(time)
    }

    override fun onRequestSelectEmvApp(appList: ArrayList<String>?) {
        Timber.d("📋 EMV App selection requested: $appList")
        updateStatus("📋 Selecting payment application...")
        // Auto-select first app for simplicity
        qposService.selectEmvApp(0)
    }

    override fun onQposRequestPinResult(dataList: List<String>?, offlineTime: Int) {
        Timber.d("🔐 PIN keyboard mapping requested: dataList=$dataList, offlineTime=$offlineTime")
        updateStatus("🔐 Initializing PIN entry...")
        // This is called when device needs PIN keyboard mapping
        // In production, you'd show a secure PIN pad UI here
    }

    override fun onQposPinMapSyncResult(isSuccess: Boolean, isNeedPin: Boolean) {}
    override fun onRequestSetPin(isOfflinePin: Boolean, tryNum: Int) {
        Timber.d("🔐 PIN REQUIRED: isOfflinePin=$isOfflinePin, tryNum=$tryNum")
        updateStatus("🔐 Please enter PIN (Attempt: $tryNum)")

        // For testing, simulate PIN entry after a delay
        // In production, this would show a PIN pad UI
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            Timber.d("Simulating PIN entry...")
            // Send a test PIN (in production, get from secure PIN pad)
            qposService.sendPin("1234")
        }, 2000)
    }

    override fun onRequestSetPin() {
        Timber.d("🔐 PIN REQUIRED (no parameters)")
        updateStatus("🔐 Please enter PIN")

        // For testing, simulate PIN entry after a delay
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            Timber.d("Simulating PIN entry...")
            // Send a test PIN (in production, get from secure PIN pad)
            qposService.sendPin("1234")
        }, 2000)
    }

    override fun onReturnGetPinResult(result: Hashtable<String, String>?) {
        Timber.d("🔐 PIN Result: $result")
        val pinBlock = result?.get("pinBlock")
        if (pinBlock != null) {
            updateStatus("✅ PIN captured successfully")
        }
    }
    override fun onRequestOnlineProcess(tlv: String?) {
        Timber.d("🌐 Online processing requested with TLV: $tlv")
        updateStatus("🌐 Processing transaction online...")

        // Parse and analyze the EMV data
        val analysisResult = qposService.anlysEmvIccData(tlv)
        Timber.d("EMV Analysis: $analysisResult")

        // For demo, auto-approve after simulating online check
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            updateStatus("✅ Online authorization successful")
            // Send online result (0x00 = approved)
            qposService.sendOnlineProcessResult("8A023030")
        }, 2000)
    }
    override fun onRequestBatchData(tlv: String?) {}
    override fun onQposIsCardExist(cardIsExist: Boolean) {}
    override fun onRequestDisplay(displayMsg: QPOSService.Display?) {}
    override fun onReturnReversalData(tlv: String?) {}
    override fun onReturnGetPinInputResult(num: Int) {
        if (num == -1) {
            updateStatus("❌ PIN entry cancelled")
        } else {
            updateStatus("🔐 PIN digits entered: $num")
        }
    }
    override fun onGetCardNoResult(cardNo: String?) {}
    override fun onGetCardInfoResult(cardInfo: Hashtable<String, String>?) {}
    override fun onEmvICCExceptionData(tlv: String?) {}
    override fun onTradeCancelled() {}
    override fun onError(errorState: QPOSService.Error?) {
        when (errorState) {
            QPOSService.Error.DEVICE_IS_OCCUPIED -> {
                updateStatus("❌ Device is occupied - Please close other POS apps")
                Timber.w("Device is occupied by another application")
                // Try to reconnect after a delay
                retryConnection()
            }
            QPOSService.Error.CMD_NOT_AVAILABLE -> {
                updateStatus("❌ Command not available - Please check device")
                Timber.w("Command not available")
            }
            else -> {
                updateStatus("❌ Error: $errorState")
                Timber.e("POS Error: $errorState")
            }
        }
    }

    private fun retryConnection() {
        updateStatus("🔄 Attempting to reconnect in 3 seconds...")

        // Retry connection after 3 seconds
        startButton.postDelayed({
            try {
                updateStatus("🔄 Retrying connection...")
                qposService.closeUart()
                Thread.sleep(1000)
                qposService.openUart()
            } catch (e: Exception) {
                updateStatus("❌ Reconnection failed - Please restart app")
                Timber.e(e, "Reconnection failed")
            }
        }, 3000)
    }

    // ConnectStateCallback implementation
    override fun onRequestQposConnected() {
        updateStatus("✅ Device connected - D60 ready")
        Timber.d("POS device connected successfully")
    }

    override fun onRequestQposDisconnected() {
        updateStatus("⚠️ Device disconnected")
        Timber.w("POS device disconnected")
    }

    override fun onRequestNoQposDetected() {
        updateStatus("❌ No device detected - Please check connection")
        Timber.e("No POS device detected")
    }

    override fun onResume() {
        super.onResume()
        // Reinitialize POS when activity resumes in case device was released
        if (::qposService.isInitialized) {
            try {
                qposService.openUart()
            } catch (e: Exception) {
                Timber.w(e, "Could not reopen UART on resume")
                updateStatus("⚠️ Device connection lost - Please restart")
            }
        }
    }

    override fun onPause() {
        super.onPause()
        // Don't close UART on pause, just log
        Timber.d("Activity paused - keeping POS connection")
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            if (::qposService.isInitialized) {
                Timber.d("Closing POS connection on destroy")
                qposService.closeUart()
            }
        } catch (e: Exception) {
            Timber.e(e, "Error closing POS")
        }
    }
}