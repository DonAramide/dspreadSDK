package com.dspreadbaseapp.baseapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.basepos.pos.dspread.MyQposClass
import com.basepos.pos.dspread.interfaces.TransactionCallback
import com.dspread.xpos.QPOSService
import timber.log.Timber
import java.util.*

/**
 * Simple test activity that mimics the reference project's payment flow
 * Based on: /Users/mac/Documents/EMV/android-7.0.3/pos_android_studio_demo/pos_android_app
 */
class TestPurchaseActivity : AppCompatActivity(), TransactionCallback {

    private lateinit var amountEditText: EditText
    private lateinit var startButton: Button
    private lateinit var statusTextView: TextView

    private lateinit var qposService: QPOSService
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
            // Initialize exactly like reference project
            qposService = QPOSService.getInstance(this, QPOSService.CommunicationMode.UART)
            qposService.setContext(this)

            // Set up callbacks
            MyQposClass.setTransactionCallback(this)

            qposService.initListener(MyQposClass())
            qposService.openUart()

            updateStatus("✅ Device connected - D60 ready")
            Timber.d("POS initialized successfully")

        } catch (e: Exception) {
            Timber.e(e, "Failed to initialize POS")
            updateStatus("❌ Failed to connect to device")
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
            val amountInCents = (amountDouble * 100).toLong().toString().padStart(12, '0')
            val cashbackAmount = "000000000000" // No cashback

            Timber.d("Setting amount: $amountInCents, cashback: $cashbackAmount, currency: $currencyCode")

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

        when (result) {
            QPOSService.DoTradeResult.ICC -> {
                updateStatus("🔹 Chip card detected - processing...")
                qposService.doEmvApp(QPOSService.EmvOption.START)
            }
            QPOSService.DoTradeResult.MCR -> {
                val cardNumber = decodeData?.get("maskedPAN") ?: "****"
                updateStatus("💳 Magnetic stripe card: $cardNumber")
                // For mag stripe, transaction is complete
            }
            QPOSService.DoTradeResult.NFC_ONLINE, QPOSService.DoTradeResult.NFC_OFFLINE -> {
                val cardNumber = decodeData?.get("maskedPAN") ?: "****"
                updateStatus("📱 Contactless card: $cardNumber")
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
        qposService.selectEmvApp(0) // Select first app
    }

    override fun onQposRequestPinResult(dataList: List<String>?, offlineTime: Int) {
        // Handle PIN input if needed
    }

    override fun onQposPinMapSyncResult(isSuccess: Boolean, isNeedPin: Boolean) {}
    override fun onRequestSetPin(isOfflinePin: Boolean, tryNum: Int) {}
    override fun onRequestSetPin() {}
    override fun onReturnGetPinResult(result: Hashtable<String, String>?) {}
    override fun onRequestOnlineProcess(tlv: String?) {}
    override fun onRequestBatchData(tlv: String?) {}
    override fun onQposIsCardExist(cardIsExist: Boolean) {}
    override fun onRequestDisplay(displayMsg: QPOSService.Display?) {}
    override fun onReturnReversalData(tlv: String?) {}
    override fun onReturnGetPinInputResult(num: Int) {}
    override fun onGetCardNoResult(cardNo: String?) {}
    override fun onGetCardInfoResult(cardInfo: Hashtable<String, String>?) {}
    override fun onEmvICCExceptionData(tlv: String?) {}
    override fun onTradeCancelled() {}
    override fun onError(errorState: QPOSService.Error?) {
        updateStatus("❌ Error: $errorState")
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            qposService.closeUart()
        } catch (e: Exception) {
            Timber.e(e, "Error closing POS")
        }
    }
}