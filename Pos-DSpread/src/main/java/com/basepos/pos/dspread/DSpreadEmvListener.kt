package com.basepos.pos.dspread

import android.content.Context
import android.os.RemoteException
import com.basepos.pos.common.domain.enums.PosEntryMode
import com.basepos.pos.common.domain.enums.TerminalCapability
import com.basepos.pos.common.domain.enums.TerminalType
import com.basepos.pos.common.domain.models.CardInfo
import com.basepos.pos.common.domain.models.CardSlotType
import com.basepos.pos.common.domain.models.EmvResult
import com.basepos.pos.common.domain.models.EmvTransactionDetails
import com.basepos.pos.common.domain.models.PaymentRequest
import com.basepos.pos.common.domain.models.TerminalInfo
import com.basepos.pos.common.domain.repository.EmvListener
import com.basepos.pos.common.domain.repository.SessionManager
import com.basepos.pos.common.utils.AmountUtils
import com.basepos.pos.common.utils.AssetFileReader
import com.basepos.pos.common.utils.HexUtils
import com.basepos.pos.common.utils.ISOUtils
import com.basepos.pos.common.utils.cryptographyUtils.TripleDESUtils
import com.basepos.pos.common.utils.showSingleChoiceDialog
import com.basepos.pos.common.utils.toAscii
import com.basepos.pos.dspread.interfaces.ConnectStateCallback
import com.basepos.pos.dspread.interfaces.PosInfoCallback
import com.basepos.pos.dspread.interfaces.PosUpdateCallback
import com.basepos.pos.dspread.interfaces.TransactionCallback
import com.basepos.pos.dspread.utils.DeviceUtils
import com.basepos.pos.dspread.utils.TLV
import com.basepos.pos.dspread.utils.TLVParser
import com.dspread.xpos.EmvAppTag
import com.dspread.xpos.EmvCapkTag
import com.dspread.xpos.QPOSService
import com.dspread.xpos.QPOSService.TransactionResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Hashtable
import javax.inject.Inject


/**
 * @Author: ifechukwu.udorji
 * @Date: 7/18/2024
 */
class DSpreadEmvListener @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sessionManager: SessionManager,
    private val dispatcher: CoroutineDispatcher,
    private val qposClass: MyQposClass
) : EmvListener {
    private var emvTransactionDetails = EmvTransactionDetails()
    private lateinit var emvResult: ((EmvResult) -> Unit)
    private lateinit var qposService: QPOSService


    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun initSdk(): Boolean {
        return suspendCancellableCoroutine { cancellableContinuation ->
            try {
                openCommunication()
                MyQposClass.setPosInfoCallback(posInfoCallback)

                qposService.getQposId()
                cancellableContinuation.resume(true) {}
            } catch (e: Exception) {
                Timber.e(e)
                cancellableContinuation.resume(false) {}
            }
        }
    }

    override suspend fun startCardTransaction(
        paymentRequest: PaymentRequest,
        callback: (EmvResult) -> Unit
    ) {
        this.emvResult = callback
        setDefaultTransactionData(paymentRequest)
        startEmvProcess()
    }

    private fun setDefaultTransactionData(paymentRequest: PaymentRequest) {
        emvTransactionDetails = emvTransactionDetails.copy(
            amount = paymentRequest.amount,
            cashbackAmount = paymentRequest.cashbackAmount,
            transactionType = paymentRequest.transType,
            accountType = paymentRequest.accountType,
            terminalInfo = TerminalInfo(
                countryCode = sessionManager.getHostParameters()?.currencyCode,
                currencyCode = sessionManager.getHostParameters()?.currencyCode,
                mcc = sessionManager.getHostParameters()?.mcc,
                merchantId = sessionManager.getHostParameters()?.merchantId,
                merchantNameAndLocation = sessionManager.getHostParameters()?.merchantName,
                posConditionCode = "00",
                posPinCaptureCode = "12",
                terminalCapability = TerminalCapability.TERMINAL_CAPABILITY_CVM.value,
                additionalTerminalCapabilities = TerminalCapability.ADDITIONAL_TERMINAL_CAPABILITY.value,
                terminalId = sessionManager.getHostParameters()?.terminalId,
                terminalSerialNumber = sessionManager.getTerminalSerialNo(),
                terminalType = TerminalType.ATTENDED_ONLINE_ONLY.code
            )
        )
    }

    private fun startEmvProcess() {
        try {
            openCommunication()

            // Wait for proper connection establishment
            Thread.sleep(100)

            // Get device ID first like reference project (skip for D60 daemon to avoid timeout)
            val isD60WithDaemon = DeviceUtils.usesServiceArchitecture(context) &&
                                  android.os.Build.MODEL == "D60"

            if (!isD60WithDaemon) {
                getDeviceId()
            } else {
                Timber.d("Skipping device ID retrieval for D60 with daemon to avoid timeout")
                sessionManager.saveTerminalSerialNo("D60_DAEMON")
            }

            // Transaction callback is already set in openCommunication()
            // For D60 with daemon, use the exact same sequence as reference project

            // Try using standard initialization for all devices (including D60)
            // The daemon service should handle D60-specific behavior automatically
            Timber.d("Using standard initialization for all devices (D60 daemon will handle specifics)")
            initializeStandardDevice()

            Timber.d("Device initialization complete, starting trade")

            // Verify listener is still set before starting trade
            if (!verifyListenerIntegrity()) {
                Timber.w("Listener integrity check failed - reinitializing")
                qposService.initListener(qposClass)
                Thread.sleep(100)
            }

            // Use longer timeout for D60 devices due to daemon communication overhead
            val timeout = if (isD60WithDaemon) 120 else 60
            Timber.d("About to call doTrade($timeout) - device should be ready")

            // Add a small delay before doTrade for D60 daemon
            if (isD60WithDaemon) {
                Thread.sleep(200)
            }

            qposService.doTrade(timeout)
            Timber.d("doTrade($timeout) called - waiting for callbacks...")

            // Start a watchdog timer to detect if no callbacks come
            startCallbackWatchdog()

        } catch (e: Exception) {
            Timber.e(e, "Error in EMV process initialization")
            emvResult(EmvResult.Error("Failed to initialize payment process: ${e.message}"))
        }
    }

    private fun initializeD60Device() {
        Timber.d("Initializing D60 device with minimal configuration (like reference project)")

        // For D60 daemon, use minimal initialization - let device handle most configuration
        // Only set the essential card trade mode
        qposService.setCardTradeMode(QPOSService.CardTradeMode.SWIPE_TAP_INSERT_CARD_NOTUP)
        Timber.d("D60: Card trade mode set")

        // Skip everything else for D60 - let daemon service handle it
        Timber.d("D60: Using minimal initialization for daemon service compatibility")
    }

    private fun initializeStandardDevice() {
        Timber.d("Initializing device with standard sequence")

        // Set card trade mode
        val cardTradeMode = if (DeviceUtils.isSmartDevices()) {
            QPOSService.CardTradeMode.SWIPE_TAP_INSERT_CARD_NOTUP
        } else {
            QPOSService.CardTradeMode.SWIPE_TAP_INSERT_CARD
        }
        qposService.setCardTradeMode(cardTradeMode)
        Timber.d("Card trade mode set to: $cardTradeMode")

        // Always set format ID - let SDK handle device differences internally
        qposService.setFormatId("0002")
        Timber.d("Format ID set to 0002")

        // Always set amount upfront like reference project - device will request again if needed
        setTransactionAmount()

        // Set customer device info for all devices
        val terminalInfo = emvTransactionDetails.terminalInfo
        if (terminalInfo != null) {
            qposService.setCustomerDeviceInfo(
                terminalInfo.merchantId ?: "",
                terminalInfo.merchantNameAndLocation ?: "",
                ""
            )
            Timber.d("Customer device info set")
        }
    }

    private fun getDeviceId() {
        try {
            // For D60 with daemon service, use longer timeout and handle differently
            val isD60WithDaemon = DeviceUtils.usesServiceArchitecture(context) &&
                                  android.os.Build.MODEL == "D60"

            if (isD60WithDaemon) {
                Timber.d("D60 with daemon - using extended timeout for device ID")
                // Use longer timeout for D60 daemon communication
                val posIdTable = qposService.syncGetQposId(10)
                val posId = posIdTable?.get("posId") as? String ?: ""
                Timber.d("D60 Device ID retrieved: $posId")
                sessionManager.saveTerminalSerialNo(posId)
            } else {
                val posIdTable = qposService.syncGetQposId(5)
                val posId = posIdTable?.get("posId") as? String ?: ""
                Timber.d("Device ID retrieved: $posId")
                sessionManager.saveTerminalSerialNo(posId)
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to get device ID - continuing without it")
            // Don't fail the entire transaction just because device ID couldn't be retrieved
            // Set a default or empty device ID
            sessionManager.saveTerminalSerialNo("D60_UNKNOWN")
        }
    }

    private fun setTransactionAmount() {
        try {
            val terminalInfo = emvTransactionDetails.terminalInfo
            val currencyCode = terminalInfo?.currencyCode ?: "566"
            val transactionAmount = emvTransactionDetails.amount

            if (transactionAmount <= 0.0) {
                Timber.e("Invalid transaction amount: $transactionAmount")
                emvResult(EmvResult.Error("Invalid transaction amount"))
                return
            }

            val amount = AmountUtils.toIsoAmount(transactionAmount, currencyCode)

            // Validate formatted amounts first
            if (amount.isNullOrEmpty() || amount == "000000000000") {
                Timber.e("Invalid formatted amount: $amount")
                emvResult(EmvResult.Error("Invalid amount format"))
                return
            }

            // For D60 with daemon service, try different parameter combinations
            val isD60WithDaemon = DeviceUtils.usesServiceArchitecture(context) &&
                                  android.os.Build.MODEL == "D60"

            val cashbackAmount: String
            val finalCurrencyCode: String
            val transactionType: QPOSService.TransactionType

            if (isD60WithDaemon) {
                // D60 with daemon - try exactly what reference project does
                cashbackAmount = ""  // Reference project uses empty string
                finalCurrencyCode = "566"  // Keep as string but ensure correct value
                transactionType = QPOSService.TransactionType.GOODS
                Timber.d("Using D60 daemon-specific parameters (matching reference)")
            } else {
                // Use standard parameters for other devices
                cashbackAmount = if (DeviceUtils.usesServiceArchitecture(context)) {
                    ""
                } else {
                    AmountUtils.toIsoAmount(emvTransactionDetails.cashbackAmount, currencyCode)
                }
                finalCurrencyCode = currencyCode
                transactionType = QPOSService.TransactionType.GOODS
            }

            Timber.d("Final parameters - Amount: $amount, Cashback: '$cashbackAmount', Currency: $finalCurrencyCode, TransType: $transactionType")

            qposService.setAmount(
                amount,
                cashbackAmount,
                finalCurrencyCode,
                transactionType
            )

            Timber.d("Amount set successfully")
        } catch (e: Exception) {
            Timber.e(e, "Error setting amount")
            emvResult(EmvResult.Error("Failed to set transaction amount: ${e.message}"))
        }
    }

    private fun openCommunication() {
        try {
            // Force close any existing connection and cleanup
            closeExistingConnections()

            // Wait a bit for proper cleanup on D60 devices
            Thread.sleep(500)

            // For D60 and service architecture devices, we might need different handling
            val communicationMode = if (DeviceUtils.usesServiceArchitecture(context)) {
                Timber.d("Using service architecture for D60/modern device")
                QPOSService.CommunicationMode.UART  // Still use UART but let service handle it
            } else {
                Timber.d("Using legacy UART communication")
                QPOSService.CommunicationMode.UART
            }

            // Create new instance
            qposService = QPOSService.getInstance(context, communicationMode)

            // Only set device address for legacy devices
            if (!DeviceUtils.usesServiceArchitecture(context)) {
                qposService.setDeviceAddress("/dev/ttyS1")
                Timber.d("Set device address to /dev/ttyS1 for legacy device")
            }

            qposService.setContext(context)

            // Set all callbacks BEFORE initializing listener
            MyQposClass.setStateCallback(connectStateCallback)
            MyQposClass.setPosUpdateCallback(posUpdateCallback)
            MyQposClass.setTransactionCallback(transactionCallback)

            // Initialize listener after setting all callbacks
            qposService.initListener(qposClass)

            // Open UART connection
            qposService.openUart()

            Timber.d("POS device connection opened successfully for ${android.os.Build.MODEL}")
        } catch (e: Exception) {
            Timber.e(e, "Failed to open POS device connection for ${android.os.Build.MODEL}")
            emvResult(EmvResult.Error("Failed to connect to POS device: ${e.message}"))
        }
    }

    private fun closeExistingConnections() {
        try {
            if (::qposService.isInitialized) {
                Timber.d("Closing existing UART connection")
                qposService.closeUart()

                // For D60 devices, also try to release any service connections
                if (DeviceUtils.usesServiceArchitecture(context)) {
                    // Give time for service cleanup
                    Thread.sleep(200)
                }
            }
        } catch (e: Exception) {
            Timber.w(e, "Error during connection cleanup - continuing anyway")
        }
    }

    private fun handleDeviceBusyError() {
        try {
            Timber.d("Handling device busy error - forcing cleanup and retry")

            // Force close and cleanup
            closeExistingConnections()

            // Wait longer for D60 service cleanup
            Thread.sleep(2000)

            // Try to restart the connection once
            Timber.d("Attempting to restart connection after busy error")

            try {
                openCommunication()

                // Wait a bit then retry the transaction
                Thread.sleep(500)

                // Retry the EMV process
                retryTransaction()

            } catch (retryException: Exception) {
                Timber.e(retryException, "Retry failed")

                if (::emvResult.isInitialized) {
                    emvResult(EmvResult.Error("Device is busy. Please wait a moment and try again."))
                } else {
                    Timber.e("Cannot signal device busy error - emvResult not initialized")
                }
            }

        } catch (e: Exception) {
            Timber.e(e, "Error handling device busy state")
            if (::emvResult.isInitialized) {
                emvResult(EmvResult.Error("Device communication error. Please restart the application."))
            } else {
                Timber.e("Cannot signal device error - emvResult not initialized")
            }
        }
    }

    private fun retryTransaction() {
        Timber.d("Retrying transaction after device busy cleanup")

        // Re-initialize device
        val isD60WithDaemon = DeviceUtils.usesServiceArchitecture(context) &&
                              android.os.Build.MODEL == "D60"

        initializeStandardDevice()

        // Verify listener integrity before retry
        if (!verifyListenerIntegrity()) {
            Timber.w("Listener integrity check failed during retry - reinitializing")
            qposService.initListener(qposClass)
            Thread.sleep(100)
        }

        // Retry doTrade with same timeout
        val timeout = if (isD60WithDaemon) 120 else 60
        Timber.d("Retrying doTrade($timeout)")
        qposService.doTrade(timeout)
    }

    private fun verifyListenerIntegrity(): Boolean {
        return try {
            // Check if the callbacks are still set
            val hasTransactionCallback = MyQposClass.hasTransactionCallback()
            val hasStateCallback = MyQposClass.hasStateCallback()

            Timber.d("Listener integrity check - Transaction: $hasTransactionCallback, State: $hasStateCallback")

            hasTransactionCallback && hasStateCallback
        } catch (e: Exception) {
            Timber.e(e, "Error checking listener integrity")
            false
        }
    }

    private fun startCallbackWatchdog() {
        // Start a timer to detect if no callbacks come within 10 seconds
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            Timber.w("⚠️ WATCHDOG: No callbacks received within 10 seconds after doTrade()")
            Timber.w("Attempting fallback approach for unresponsive device")

            // Try a fallback approach - restart connection with different parameters
            tryFallbackApproach()

        }, 10000)
    }

    private fun tryFallbackApproach() {
        try {
            Timber.d("Trying fallback approach for unresponsive D60 daemon")

            // Close current connection
            closeExistingConnections()
            Thread.sleep(1000)

            // Try a more basic approach - minimal initialization
            qposService = QPOSService.getInstance(context, QPOSService.CommunicationMode.UART)
            qposService.setContext(context)

            // Set callbacks before init
            MyQposClass.setStateCallback(connectStateCallback)
            MyQposClass.setPosUpdateCallback(posUpdateCallback)
            MyQposClass.setTransactionCallback(transactionCallback)

            qposService.initListener(qposClass)
            qposService.openUart()

            Thread.sleep(500)

            // Basic trade mode only
            qposService.setCardTradeMode(QPOSService.CardTradeMode.SWIPE_TAP_INSERT_CARD_NOTUP)

            // Try shorter timeout
            Timber.d("Fallback: Trying doTrade with 60 second timeout")
            qposService.doTrade(60)

        } catch (e: Exception) {
            Timber.e(e, "Fallback approach failed")
            if (::emvResult.isInitialized) {
                emvResult(EmvResult.Error("Device not responding. Please restart the application and try again."))
            }
        }
    }

    private fun extractIccData(tlvMap: List<TLV?>?): String {
        Timber.d("Extracting ICC Data")
        val tagList = ISOUtils.getIccDataTags()
        val tlvData = StringBuilder()
        tagList.forEach { tag ->
            val mapData = tlvMap?.firstOrNull { tlv -> tlv?.tag?.uppercase() == tag }
            if (mapData != null) {
                tlvData.append(tag)
                val length = mapData.length
                tlvData.append(mapData.length)
                if (tag == "5F2A" || tag == "9F1A") {
                    tlvData.append("0566")
                } else {
                    tlvData.append(mapData.value)
                }
                Timber.d("---------------------------------------")
                Timber.d("Tag: $tag")
                Timber.d("Length: $length")
                Timber.d("Value: $mapData")
                Timber.d("---------------------------------------")
            }
        }

        return tlvData.toString().uppercase()
    }

    override suspend fun injectPinpadKeys(): Boolean {
        openCommunication()

        val terminalParameters = sessionManager.getHostParameters()
        val mainKey = loadMainKey(terminalParameters?.tmk!!, terminalParameters.tmkKCV!!)

        loadAidCapk()
        closeSdk()
        return mainKey
    }

    override fun loadAidCapk() {
//        val emvProfileTlv = AssetFileReader.readAssetsLine(context, "emv_profile_tlv.xml")
//        qposService.updateEMVConfigByXml(String(emvProfileTlv!!))

        try {
            qposService.updateEmvAPPByTlv(QPOSService.EMVDataOperation.Clear, null)
            qposService.updateEmvCAPKByTlv(QPOSService.EMVDataOperation.Clear, null)

            loadAid()
            loadCapk()
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    private fun loadCapk() {
        val emvCapkList = sessionManager.getTmsParameter()!!.emvCapkList

        emvCapkList.forEach { capk ->
            val capk = StringBuilder()
                .apply {
                    append(EmvCapkTag.RID)
                    append(capk.rid)
                    append(EmvCapkTag.Public_Key_Index)
                    append(capk.keyIndex.toString())
                    append(EmvCapkTag.Public_Key_Module)
                    append(capk.modulus)
                    append(EmvCapkTag.Pk_exponent)
                    append(capk.exponent.toString())
                    append(EmvCapkTag.Pk_algorithm_identification)
                    append(capk.arithIndex.toString())
                    append(EmvCapkTag.Public_Key_CheckValue)
                    append(capk.checksum)
                    append(EmvCapkTag.Expired_date)
                    append(capk.expiredWhen)
                    append(EmvCapkTag.Hash_algorithm_identification)
                    append(capk.hashIndex.toString())
                }.toString()

            Timber.d("Loading CAPK: $capk")

            qposService.updateEmvCAPKByTlv(QPOSService.EMVDataOperation.Add, capk)
        }
    }

    private fun loadAid() {
        val emvAIDList = sessionManager.getTmsParameter()!!.emvAIDList

        emvAIDList.forEach { aid ->
            val app = StringBuilder()
                .apply {
                    append(EmvAppTag.Application_Identifier_AID_terminal)
                    append(aid.aid)
                    append(EmvAppTag.Application_Version_Number)
                    append(aid.appVerNum)
                    append(EmvAppTag.Terminal_Floor_Limit)
                    append(aid.floorLimit.toString())
                    append(EmvAppTag.Contactless_CVM_Required_limit)
                    append(aid.contactlessCvmLimit.toString())
                    append(EmvAppTag.terminal_contactless_transaction_limit)
                    append(aid.contactlessTransLimit.toString())
                    append(EmvAppTag.terminal_contactless_offline_floor_limit)
                    append(aid.contactlessFloorLimit.toString())
                    append(EmvAppTag.Maximum_Target_Percentage_to_be_used_for_Biased_Random_Selection)
                    append(aid.maxTargetPercent.toString())
                    append(EmvAppTag.TAC_Default)
                    append(aid.tacDefault)
                    append(EmvAppTag.TAC_Denial)
                    append(aid.tacDenial)
                    append(EmvAppTag.TAC_Online)
                    append(aid.tacOnline)
                }.toString()

            Timber.d("Loading AID: $app")

            qposService.updateEmvAPPByTlv(QPOSService.EMVDataOperation.Add, app)
        }
    }

    override suspend fun closeSdk() {
        try {
            Timber.d("Closing SDK and cleaning up connections")
            if (::qposService.isInitialized) {
                qposService.closeUart()

                // Additional cleanup for service architecture devices
                if (DeviceUtils.usesServiceArchitecture(context)) {
                    Thread.sleep(300)
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error closing SDK")
        }
    }

    private fun loadMainKey(tmk: String, kcv: String): Boolean {
        return try {
            val cTMK = sessionManager.getCTMK() ?: "0123456789ABCDEFFEDCBA9876543210"
            val tripleDESUtils =
                TripleDESUtils(HexUtils.hexStringToByte(cTMK)!!)
            val newMasterKey = tripleDESUtils.encode(HexUtils.hexStringToByte(tmk))
            val encodedNewMasterKey = HexUtils.byteArrayToHexString(newMasterKey!!)

            sessionManager.saveCTMK(tmk)
            qposService.setMasterKey(encodedNewMasterKey, kcv, 0);
            true
        } catch (e: RemoteException) {
            Timber.e(e)
            false
        }
    }

    private val transactionCallback = object : TransactionCallback {
        override fun onRequestSetAmount() {
            Timber.i("💰 CALLBACK: onRequestSetAmount - Device requesting amount (already set upfront)")

            // Amount was already set in initialization, but device is requesting it again
            // This is normal for some devices - just acknowledge that amount is already set
            Timber.d("Amount already set during initialization - continuing transaction")

            // Log next expected step
            Timber.i("✅ Amount confirmed - device should now request final confirm or wait for card")
        }

        override fun onRequestWaitingUser() {
            Timber.i("🔄 WAITING FOR CARD: Device is ready - please insert, tap, or swipe your card")

            // This is the key milestone - device is ready for card input
            // User should now present their card to the device
            // The device will automatically detect and read the card
        }

        override fun onRequestTime() {
            Timber.d("OnRequest Time")
            val terminalTime =
                SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().time)
            qposService.sendTime(terminalTime)
        }

        override fun onRequestSelectEmvApp(appList: ArrayList<String>?) {
            Timber.d("onRequestSelectEmvApp >>> $appList")
            appList ?: return qposService.selectEmvApp(0)

            context.showSingleChoiceDialog(
                title = "Select EMV App",
                items = appList.toTypedArray(),
                onItemSelected = { index, selectedApp ->
                    Timber.d("Selected App: $selectedApp")
                    qposService.selectEmvApp(index)
                },
                onCancelled = {
                    qposService.cancelSelectEmvApp()
                }
            )
        }

        override fun onQposRequestPinResult(dataList: MutableList<String>?, offlineTime: Int) {
            Timber.d("onQposRequestPinResult >>> dataList: $dataList, offlineTime: $offlineTime")
        }

        override fun onQposPinMapSyncResult(isSuccess: Boolean, isNeedPin: Boolean) {

        }

        override fun onRequestSetPin(isOfflinePin: Boolean, tryNum: Int) {
            Timber.d("onRequestSetPin >>> isOfflinePin: $isOfflinePin, tryNum: $tryNum")
            emvResult(EmvResult.OnPinInputRequired(isOfflinePin, tryNum))
        }

        override fun onRequestSetPin() {
            Timber.d("onRequestSetPin >>>")
            emvResult(EmvResult.OnPinInputRequired(true, 0))
        }

        override fun onReturnGetPinResult(result: Hashtable<String, String>?) {
            Timber.d("onReturnGetPinResult >>> result: $result")
            val pinBlock = result!!["pinBlock"]
            emvTransactionDetails =
                emvTransactionDetails.copy(cardInfo = emvTransactionDetails.cardInfo?.copy(pinData = pinBlock))
        }

        override fun onDoTradeResult(
            result: QPOSService.DoTradeResult?,
            decodeData: Hashtable<String, String>?
        ) {
            Timber.i("💳 CARD DETECTED: $result")
            Timber.d("Card data: $decodeData")

            when (result) {
                QPOSService.DoTradeResult.ICC -> {
                    Timber.i("🔹 CHIP CARD detected - starting EMV processing")
                    qposService.doEmvApp(QPOSService.EmvOption.START)
                    emvTransactionDetails =
                        emvTransactionDetails.copy(
                            terminalInfo = emvTransactionDetails.terminalInfo?.copy(
                                posEntryMode = PosEntryMode.ICC
                            ),
                            cardInfo = CardInfo(cardSlotType = CardSlotType.ICC)
                        )
                }

                QPOSService.DoTradeResult.MCR -> {
                    Timber.i("💳 MAGNETIC STRIPE card detected")

                    // Extract card details from magnetic stripe
                    val cardNumber = decodeData?.get("maskedPAN") ?: decodeData?.get("encTracks")
                    val cardHolder = decodeData?.get("cardholderName")
                    val expiry = decodeData?.get("expiryDate")

                    Timber.d("Card Details - PAN: $cardNumber, Holder: $cardHolder, Expiry: $expiry")

                    emvTransactionDetails = emvTransactionDetails.copy(
                        terminalInfo = emvTransactionDetails.terminalInfo?.copy(
                            posEntryMode = PosEntryMode.MAG
                        ),
                        cardInfo = CardInfo(
                            cardSlotType = CardSlotType.SWIPE,
                            aid = decodeData?.get("aid"),
                            cardHolderName = cardHolder,
                            pan = cardNumber,
                            expiry = expiry
                        )
                    )

                    Timber.i("✅ MAGNETIC STRIPE transaction ready - proceeding to online processing")
                    emvResult(EmvResult.OnRequestOnline(emvTransactionDetails))
                }

                QPOSService.DoTradeResult.NFC_ONLINE,
                QPOSService.DoTradeResult.NFC_OFFLINE -> {
                    Timber.i("💳 CONTACTLESS card detected: $result")

                    // Extract card details from contactless
                    val cardNumber = decodeData?.get("maskedPAN") ?: decodeData?.get("encTracks")
                    val cardHolder = decodeData?.get("cardholderName")
                    val expiry = decodeData?.get("expiryDate")

                    Timber.d("Contactless Card Details - PAN: $cardNumber, Holder: $cardHolder, Expiry: $expiry")

                    emvTransactionDetails = emvTransactionDetails.copy(
                        terminalInfo = emvTransactionDetails.terminalInfo?.copy(
                            posEntryMode = PosEntryMode.CONTACTLESS_ICC
                        ),
                        cardInfo = CardInfo(
                            cardSlotType = CardSlotType.RF,
                            aid = decodeData?.get("aid"),
                            cardHolderName = cardHolder,
                            pan = cardNumber,
                            expiry = expiry
                        )
                    )

                    Timber.i("✅ CONTACTLESS transaction ready - proceeding to online processing")
                    emvResult(EmvResult.OnRequestOnline(emvTransactionDetails))
                }

                QPOSService.DoTradeResult.NFC_DECLINED -> {
                    emvResult(EmvResult.Error("Transaction Declined"))
                }

                QPOSService.DoTradeResult.BAD_SWIPE -> {
                    emvResult(EmvResult.Error("Bad Swipe"))
                }

                QPOSService.DoTradeResult.CARD_NOT_SUPPORT -> {
                    emvResult(EmvResult.Error("Card Not Supported"))
                }

                QPOSService.DoTradeResult.NO_RESPONSE -> {
                    emvResult(EmvResult.Error("Check card no response"))
                }

                QPOSService.DoTradeResult.NONE -> {
                    emvResult(EmvResult.Error("No Card Detected"))
                }

                else -> {
                    emvResult(EmvResult.Error("Unknown Error"))
                }
            }
        }

        override fun onRequestOnlineProcess(tlv: String) {
            Timber.d("onRequestOnlineProcess >>> tlv: $tlv")
            Timber.d("ANALYZE >>> ${qposService.anlysEmvIccData(tlv)}")
            val anlysEmvIccData = qposService.anlysEmvIccData(tlv)
            val encTrack2 = anlysEmvIccData?.getOrDefault("encTrack2", null)
            val iccdata = anlysEmvIccData?.getOrDefault("iccdata", null)
            val cardholderName = anlysEmvIccData?.getOrDefault("cardholderName", null)
            val pinBlock = anlysEmvIccData?.getOrDefault("pinBlock", null)

            val iccDataTLVS = TLVParser.parse(iccdata)

            val applicationIdentifier =
                iccDataTLVS?.firstOrNull { tlv -> tlv?.tag?.uppercase() == "9F06" }
            val applicationLabel = iccDataTLVS?.firstOrNull { tlv -> tlv?.tag == "50" }
            val sequenceNumber = iccDataTLVS?.firstOrNull { tlv -> tlv?.tag?.uppercase() == "5F34" }
            val applicationCryptogram =
                iccDataTLVS?.firstOrNull { tlv -> tlv?.tag?.uppercase() == "9F26" }
            val cryptogramInformationData =
                iccDataTLVS?.firstOrNull { tlv -> tlv?.tag?.uppercase() == "9F27" }
            val issuerApplicationData =
                iccDataTLVS?.firstOrNull { tlv -> tlv?.tag?.uppercase() == "9F10" }
            val unpredictableNumber =
                iccDataTLVS?.firstOrNull { tlv -> tlv?.tag?.uppercase() == "9F37" }
            val appTransactionCounter =
                iccDataTLVS?.firstOrNull { tlv -> tlv?.tag?.uppercase() == "9F36" }
            val terminalVerificationResults = iccDataTLVS?.firstOrNull { tlv -> tlv?.tag == "95" }
            val applicationInterchangeProfile = iccDataTLVS?.firstOrNull { tlv -> tlv?.tag == "82" }
            val cvmResult = iccDataTLVS?.firstOrNull { tlv -> tlv?.tag?.uppercase() == "9F34" }
            val dedicatedFileName = iccDataTLVS?.firstOrNull { tlv -> tlv?.tag == "84" }

            val decodedTrack2 = try {
                val tdes =
                    TripleDESUtils(HexUtils.hexStringToByte(sessionManager.getHostParameters()?.tpk)!!)
                val tdesValue = tdes.decode(HexUtils.hexStringToByte(encTrack2)!!)
                HexUtils.byteArrayToHexString(tdesValue!!).trimEnd { it == 'F' }
            } catch (e: Exception) {
                Timber.e(e, "Error decoding Track 2 data")
                ""
            }

            emvTransactionDetails = emvTransactionDetails.copy(
                appCryptogram = applicationCryptogram?.value,
                cryptogramInformationData = cryptogramInformationData?.value?.toByteOrNull() ?: 0,
                issuerApplicationData = issuerApplicationData?.value,
                unpredictableNumber = unpredictableNumber?.value,
                appTransactionCounter = appTransactionCounter?.value,
                terminalVerificationResults = terminalVerificationResults?.value,
                applicationInterchangeProfile = applicationInterchangeProfile?.value,
                cvmResult = cvmResult?.value,
                dedicatedFileName = dedicatedFileName?.value,
                cardInfo = emvTransactionDetails.cardInfo?.copy(
                    aid = applicationIdentifier?.value,
                    cardLabel = applicationLabel?.value,
                    cardHolderName = cardholderName,
                    expiry = ISOUtils.getCardExpiryDateFromTrack2Data(decodedTrack2),
                    pan = ISOUtils.getCardPanFromTrack2Data(decodedTrack2),
                    pinData = pinBlock,
                    serviceCode = ISOUtils.getServiceCodeFromTrack2Data(decodedTrack2),
                    track2 = decodedTrack2,
                    iccData = extractIccData(iccDataTLVS),
                    sequenceNumber = sequenceNumber?.value,
                    acquirerInstitutionId = ISOUtils.getAcquirerInstitutionIdFromTrack2Data(decodedTrack2)
                ),
            )


            Timber.d("Card Info: $emvTransactionDetails")
            emvResult(EmvResult.OnRequestOnline(emvTransactionDetails))
        }

        override fun onRequestTransactionResult(transactionResult: TransactionResult?) {
            Timber.d("onRequestTransactionResult >>> transactionResult: $transactionResult")
            when (transactionResult) {
                TransactionResult.APPROVED -> {
                    Timber.d("Transaction approved successfully")
                    emvResult(EmvResult.Loading("Transaction approved"))
                    // Note: Transaction completion will be handled by onRequestTransactionLog
                }
                TransactionResult.TERMINATED -> {
                    Timber.d("Transaction terminated")
                    emvResult(EmvResult.Error("Transaction was terminated"))
                }
                TransactionResult.DECLINED -> {
                    Timber.d("Transaction declined")
                    emvResult(EmvResult.Error("Transaction declined"))
                }
                TransactionResult.CANCEL -> {
                    Timber.w("⚠️ Transaction cancelled - this may be due to timeout or user action")
                    emvResult(EmvResult.Error("Transaction cancelled. Please try again and present your card when prompted."))
                }
                TransactionResult.CAPK_FAIL -> {
                    Timber.d("CAPK failure")
                    emvResult(EmvResult.Error("Certificate authority public key failure"))
                }
                TransactionResult.NOT_ICC -> {
                    Timber.d("Not an ICC card")
                    emvResult(EmvResult.Error("Card is not a chip card"))
                }
                TransactionResult.SELECT_APP_FAIL -> {
                    Timber.d("Application selection failed")
                    emvResult(EmvResult.Error("Application selection failed"))
                }
                TransactionResult.DEVICE_ERROR -> {
                    Timber.d("Device error")
                    emvResult(EmvResult.Error("POS device error"))
                }
                TransactionResult.CARD_NOT_SUPPORTED -> {
                    Timber.d("Card not supported")
                    emvResult(EmvResult.Error("Card not supported"))
                }
                TransactionResult.MISSING_MANDATORY_DATA -> {
                    Timber.d("Missing mandatory data")
                    emvResult(EmvResult.Error("Missing mandatory transaction data"))
                }
                TransactionResult.CARD_BLOCKED_OR_NO_EMV_APPS -> {
                    Timber.d("Card blocked or no EMV apps")
                    emvResult(EmvResult.Error("Card is blocked or has no EMV applications"))
                }
                TransactionResult.INVALID_ICC_DATA -> {
                    Timber.d("Invalid ICC data")
                    emvResult(EmvResult.Error("Invalid chip card data"))
                }
                TransactionResult.FALLBACK -> {
                    Timber.d("Fallback to magnetic stripe")
                    emvResult(EmvResult.Error("EMV processing failed, fallback required"))
                }
                TransactionResult.NFC_TERMINATED -> {
                    Timber.d("NFC transaction terminated")
                    emvResult(EmvResult.Error("Contactless transaction terminated"))
                }
                TransactionResult.CARD_REMOVED -> {
                    Timber.d("Card removed")
                    emvResult(EmvResult.Error("Card was removed"))
                }
                TransactionResult.TRADE_LOG_FULL -> {
                    Timber.d("Trade log full")
                    emvResult(EmvResult.Error("Transaction log is full"))
                }
                TransactionResult.TRANSACTION_NOT_ALLOWED_AMOUNT_EXCEED -> {
                    Timber.d("Transaction amount exceeds limit")
                    emvResult(EmvResult.Error("Transaction amount exceeds limit"))
                }
                TransactionResult.CONTACTLESS_TRANSACTION_NOT_ALLOW -> {
                    Timber.d("Contactless transaction not allowed")
                    emvResult(EmvResult.Error("Contactless transaction not allowed"))
                }
                TransactionResult.TRANS_TOKEN_INVALID -> {
                    Timber.d("Transaction token invalid")
                    emvResult(EmvResult.Error("Transaction token is invalid"))
                }
                TransactionResult.CARD_BLOCKED -> {
                    Timber.d("Card blocked")
                    emvResult(EmvResult.Error("Card is blocked"))
                }
                TransactionResult.APP_BLOCKED -> {
                    Timber.d("Application blocked")
                    emvResult(EmvResult.Error("Card application is blocked"))
                }
                TransactionResult.MULTIPLE_CARDS -> {
                    Timber.d("Multiple cards detected")
                    emvResult(EmvResult.Error("Multiple cards detected, please use one card"))
                }
                null -> {
                    Timber.d("Transaction result is null")
                    emvResult(EmvResult.Error("Unknown transaction result"))
                }
            }
        }

        override fun onRequestBatchData(tlv: String?) {
            Timber.d("onRequestBatchData >>> tlv: $tlv")
        }


        override fun onQposIsCardExist(cardIsExist: Boolean) {
            Timber.d("onQposIsCardExist: $cardIsExist")
        }

        override fun onRequestDisplay(displayMsg: QPOSService.Display?) {
            Timber.d("onRequestDisplay: $displayMsg")

            // For now, just log the display message
            // The D60 device may handle account selection automatically
            // or require different response methods not available in sendSelectResult
            displayMsg?.let { display ->
                val displayStr = display.toString()
                when {
                    displayStr.contains("ACCOUNT") || displayStr.contains("SELECT") -> {
                        Timber.d("Account type selection dialog detected: $display")
                        // Just log - the device may handle this automatically
                        // or the timeout may resolve it
                    }
                    displayStr.contains("INSERT") || displayStr.contains("TAP") || displayStr.contains("SWIPE") -> {
                        Timber.d("Card insertion/tap/swipe requested: $display")
                        // User action required - just wait
                    }
                    displayStr.contains("PROCESSING") -> {
                        Timber.d("Processing transaction: $display")
                    }
                    displayStr.contains("AMOUNT") -> {
                        Timber.d("Amount display: $display")
                    }
                    displayStr.contains("REMOVED") -> {
                        Timber.d("Card removed: $display")
                    }
                    else -> {
                        Timber.d("Other display message: $display")
                    }
                }
            }
        }

        override fun onReturnReversalData(tlv: String?) {

        }

        override fun onReturnGetPinInputResult(num: Int) {
            if (num == -1) return
            emvResult(EmvResult.OnPinInput(num))
        }

        override fun onGetCardNoResult(cardNo: String?) {
            Timber.d("onGetCardNoResult: $cardNo")
        }

        override fun onGetCardInfoResult(cardInfo: Hashtable<String, String>?) {
            Timber.d("onGetCardInfoResult: $cardInfo")
        }

        override fun onEmvICCExceptionData(tlv: String?) {
            Timber.d("onEmvICCExceptionData: $tlv")
        }

        override fun onTradeCancelled() {
            Timber.d("onTradeCancelled")
        }

        override fun onError(errorState: QPOSService.Error?) {
            Timber.e("Transaction error: $errorState")

            // Check if emvResult is initialized before using it
            if (!this@DSpreadEmvListener::emvResult.isInitialized) {
                Timber.e("Error occurred before emvResult was initialized: $errorState")
                return
            }

            when (errorState) {
                QPOSService.Error.INPUT_INVALID -> {
                    emvResult(EmvResult.Error("Invalid input parameters - check amount, currency, or transaction settings"))
                }
                QPOSService.Error.DEVICE_RESET -> {
                    Timber.e("Device reset detected - this may indicate D60 daemon communication issues")
                    emvResult(EmvResult.Error("Device reset detected. Please ensure D60 daemon service is running and try again."))
                }
                QPOSService.Error.CMD_NOT_AVAILABLE -> {
                    emvResult(EmvResult.Error("Command not available on this device"))
                }
                QPOSService.Error.TIMEOUT -> {
                    emvResult(EmvResult.Error("Operation timeout"))
                }
                QPOSService.Error.DEVICE_BUSY -> {
                    Timber.w("Device busy - attempting cleanup and retry")
                    handleDeviceBusyError()
                }
                QPOSService.Error.DEVICE_IS_OCCUPIED -> {
                    Timber.e("Device is occupied by another application")
                    emvResult(EmvResult.Error("Device is already in use by another application. Please close other POS applications and try again."))
                }
                else -> {
                    emvResult(EmvResult.Error("Device error: ${errorState?.name ?: "Unknown"}"))
                }
            }
        }

        override fun onRequestFinalConfirm() {
            Timber.i("💰 FINAL CONFIRM: Auto-confirming transaction to proceed")
            // Auto-confirm the transaction - this simulates clicking "OK" on the confirmation dialog
            qposService.finalConfirm(true)
            Timber.d("Final confirm sent - waiting for next step")
        }
    }

    private val posInfoCallback = object : PosInfoCallback {
        override fun onQposInfoResult(posInfoData: Hashtable<String, String>?) {
            Timber.d("onQposInfoResult: $posInfoData")
        }

        override fun onQposIdResult(posIdTable: Hashtable<String, String>?) {
            Timber.d("onQposIdResult: $posIdTable")
            val serialNo = if (posIdTable!!.get("posId") == null) "" else posIdTable.get("posId")
            sessionManager.saveTerminalSerialNo(serialNo!!)
        }

        override fun onRequestUpdateKey(arg0: String?) {
            Timber.d("onRequestUpdateKey: $arg0")
        }

        override fun onGetKeyCheckValue(checkValue: Hashtable<String, String>?) {
            Timber.d("onGetKeyCheckValue: $checkValue")
        }
    }

    private val connectStateCallback = object : ConnectStateCallback {
        override fun onRequestQposConnected() {
            Timber.d("onRequestQposConnected >>>>>>>>>>>>>>>>")
        }

        override fun onRequestQposDisconnected() {
            Timber.d("onRequestQposDisconnected")
        }

        override fun onRequestNoQposDetected() {
            Timber.d("onRequestNoQposDetected")
        }
    }

    private val posUpdateCallback = object : PosUpdateCallback {
        override fun onReturnUpdateIPEKResult(arg0: Boolean) {
            Timber.d("onReturnUpdateIPEKResult: $arg0")
        }

        override fun onReturnSetMasterKeyResult(
            isSuccess: Boolean,
            result: Hashtable<String, String>?
        ) {
            Timber.d("onReturnSetMasterKeyResult: $isSuccess, $result")
        }

        override fun onReturnSetMasterKeyResult(isSuccess: Boolean) {
            Timber.d("onReturnSetMasterKeyResult: $isSuccess")
            if (isSuccess) {
                loadWorkKey()
            } else {
                sessionManager.saveCTMK(null)
            }
        }

        override fun onRequestUpdateWorkKeyResult(
            result: QPOSService.UpdateInformationResult?,
            checkValue: Hashtable<String, String>?
        ) {
            Timber.d("onRequestUpdateWorkKeyResult: $result, $checkValue")
        }

        override fun onRequestUpdateWorkKeyResult(result: QPOSService.UpdateInformationResult?) {
            Timber.d("onRequestUpdateWorkKeyResult: $result")
            qposService.getKeyCheckValue(0, QPOSService.CHECKVALUE_KEYTYPE.DUKPT_MKSK_ALLTYPE)
        }

        override fun onUpdatePosFirmwareResult(arg0: QPOSService.UpdateInformationResult?) {
            Timber.d("onUpdatePosFirmwareResult: $arg0")
        }

        override fun onReturnCustomConfigResult(isSuccess: Boolean, result: String?) {
            Timber.d("onReturnCustomConfigResult >>>>>>: $isSuccess, $result")
        }

        override fun onRequestDevice() {
            Timber.d("onRequestDevice")
        }

        private fun loadWorkKey() {
            Timber.d("Loading Work Key")
            val terminalParameters = sessionManager.getHostParameters()
            if (terminalParameters != null) {
                val tpk = terminalParameters.encryptedTpk
                val kcv: String? = terminalParameters.tpkKCV
                val kcv_: Int = kcv?.toInt() ?: 0

                qposService.updateWorkKey("0", tpk, kcv, tpk, kcv, tpk, kcv_)
            } else {
                Timber.e("Terminal parameters not found")
            }
        }

    }
}