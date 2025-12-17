package com.basepos.pos.dspread

import com.basepos.pos.dspread.interfaces.ConnectStateCallback
import com.basepos.pos.dspread.interfaces.PosInfoCallback
import com.basepos.pos.dspread.interfaces.PosUpdateCallback
import com.basepos.pos.dspread.interfaces.TransactionCallback
import com.dspread.xpos.QPOSService
import com.dspread.xpos.CQPOSService
import timber.log.Timber
import java.util.Hashtable

/**
 * Bridge class that extends CQPOSService and delegates to specific callbacks
 * This allows modular handling of different callback types
 */
class MyQposClass : CQPOSService() {

    private var transactionCallback: TransactionCallback? = null
    private var connectStateCallback: ConnectStateCallback? = null
    private var posInfoCallback: PosInfoCallback? = null
    private var posUpdateCallback: PosUpdateCallback? = null

    fun setTransactionCallback(callback: TransactionCallback) {
        transactionCallback = callback
    }

    fun setStateCallback(callback: ConnectStateCallback) {
        connectStateCallback = callback
    }

    fun setPosInfoCallback(callback: PosInfoCallback) {
        posInfoCallback = callback
    }

    fun setPosUpdateCallback(callback: PosUpdateCallback) {
        posUpdateCallback = callback
    }

    fun hasTransactionCallback(): Boolean = transactionCallback != null

    fun hasStateCallback(): Boolean = connectStateCallback != null

    private fun getTransactionCallback(): TransactionCallback? = transactionCallback

    private fun getConnectStateCallback(): ConnectStateCallback? = connectStateCallback

    private fun getPosInfoCallback(): PosInfoCallback? = posInfoCallback

    private fun getPosUpdateCallback(): PosUpdateCallback? = posUpdateCallback

    // Transaction callbacks
    override fun onRequestSetAmount() {
        Timber.d("MyQposClass: onRequestSetAmount")
        getTransactionCallback()?.onRequestSetAmount()
    }

    override fun onRequestWaitingUser() {
        Timber.d("MyQposClass: onRequestWaitingUser")
        getTransactionCallback()?.onRequestWaitingUser()
    }

    override fun onDoTradeResult(result: QPOSService.DoTradeResult?, decodeData: Hashtable<String, String>?) {
        Timber.d("MyQposClass: onDoTradeResult - $result")
        getTransactionCallback()?.onDoTradeResult(result, decodeData)
    }

    override fun onRequestTime() {
        Timber.d("MyQposClass: onRequestTime")
        getTransactionCallback()?.onRequestTime()
    }

    override fun onRequestSelectEmvApp(appList: ArrayList<String>?) {
        Timber.d("MyQposClass: onRequestSelectEmvApp")
        getTransactionCallback()?.onRequestSelectEmvApp(appList)
    }

    override fun onQposRequestPinResult(dataList: MutableList<String>?, offlineTime: Int) {
        Timber.d("MyQposClass: onQposRequestPinResult")
        getTransactionCallback()?.onQposRequestPinResult(dataList, offlineTime)
    }

    override fun onQposPinMapSyncResult(isSuccess: Boolean, isNeedPin: Boolean) {
        Timber.d("MyQposClass: onQposPinMapSyncResult")
        getTransactionCallback()?.onQposPinMapSyncResult(isSuccess, isNeedPin)
    }

    override fun onRequestSetPin(isOfflinePin: Boolean, tryNum: Int) {
        Timber.d("MyQposClass: 🔐 PIN REQUESTED - isOfflinePin: $isOfflinePin, tryNum: $tryNum")
        getTransactionCallback()?.onRequestSetPin(isOfflinePin, tryNum)
    }

    override fun onRequestSetPin() {
        Timber.d("MyQposClass: 🔐 PIN REQUESTED (no parameters)")
        getTransactionCallback()?.onRequestSetPin()
    }

    override fun onReturnGetPinResult(result: Hashtable<String, String>?) {
        Timber.d("MyQposClass: onReturnGetPinResult")
        getTransactionCallback()?.onReturnGetPinResult(result)
    }

    override fun onRequestOnlineProcess(tlv: String?) {
        Timber.d("MyQposClass: onRequestOnlineProcess")
        getTransactionCallback()?.onRequestOnlineProcess(tlv)
    }

    override fun onRequestTransactionResult(transactionResult: QPOSService.TransactionResult?) {
        Timber.d("MyQposClass: onRequestTransactionResult - $transactionResult")
        getTransactionCallback()?.onRequestTransactionResult(transactionResult)
    }

    override fun onRequestBatchData(tlv: String?) {
        Timber.d("MyQposClass: onRequestBatchData")
        getTransactionCallback()?.onRequestBatchData(tlv)
    }

    override fun onQposIsCardExist(cardIsExist: Boolean) {
        Timber.d("MyQposClass: onQposIsCardExist - $cardIsExist")
        getTransactionCallback()?.onQposIsCardExist(cardIsExist)
    }

    override fun onRequestDisplay(displayMsg: QPOSService.Display?) {
        Timber.d("MyQposClass: onRequestDisplay - $displayMsg")
        getTransactionCallback()?.onRequestDisplay(displayMsg)
    }

    override fun onReturnReversalData(tlv: String?) {
        Timber.d("MyQposClass: onReturnReversalData")
        getTransactionCallback()?.onReturnReversalData(tlv)
    }

    override fun onReturnGetPinInputResult(num: Int) {
        Timber.d("MyQposClass: onReturnGetPinInputResult - $num")
        getTransactionCallback()?.onReturnGetPinInputResult(num)
    }

    override fun onGetCardNoResult(cardNo: String?) {
        Timber.d("MyQposClass: onGetCardNoResult")
        getTransactionCallback()?.onGetCardNoResult(cardNo)
    }

    override fun onGetCardInfoResult(cardInfo: Hashtable<String, String>?) {
        Timber.d("MyQposClass: onGetCardInfoResult")
        getTransactionCallback()?.onGetCardInfoResult(cardInfo)
    }

    override fun onEmvICCExceptionData(tlv: String?) {
        Timber.d("MyQposClass: onEmvICCExceptionData")
        getTransactionCallback()?.onEmvICCExceptionData(tlv)
    }

    override fun onTradeCancelled() {
        Timber.d("MyQposClass: onTradeCancelled")
        getTransactionCallback()?.onTradeCancelled()
    }

    override fun onError(errorState: QPOSService.Error?) {
        Timber.e("MyQposClass: onError - $errorState")
        getTransactionCallback()?.onError(errorState)
    }

    override fun onRequestFinalConfirm() {
        Timber.d("MyQposClass: onRequestFinalConfirm")
        getTransactionCallback()?.onRequestFinalConfirm()
    }

    // Connect State callbacks
    override fun onRequestQposConnected() {
        Timber.d("MyQposClass: onRequestQposConnected")
        getConnectStateCallback()?.onRequestQposConnected()
    }

    override fun onRequestQposDisconnected() {
        Timber.d("MyQposClass: onRequestQposDisconnected")
        getConnectStateCallback()?.onRequestQposDisconnected()
    }

    override fun onRequestNoQposDetected() {
        Timber.d("MyQposClass: onRequestNoQposDetected")
        getConnectStateCallback()?.onRequestNoQposDetected()
    }

    // POS Info callbacks
    override fun onQposInfoResult(posInfoData: Hashtable<String, String>?) {
        Timber.d("MyQposClass: onQposInfoResult")
        getPosInfoCallback()?.onQposInfoResult(posInfoData)
    }

    override fun onQposIdResult(posIdTable: Hashtable<String, String>?) {
        Timber.d("MyQposClass: onQposIdResult")
        getPosInfoCallback()?.onQposIdResult(posIdTable)
    }

    override fun onRequestUpdateKey(arg0: String?) {
        Timber.d("MyQposClass: onRequestUpdateKey")
        getPosInfoCallback()?.onRequestUpdateKey(arg0)
    }

    override fun onGetKeyCheckValue(checkValue: Hashtable<String, String>?) {
        Timber.d("MyQposClass: onGetKeyCheckValue")
        getPosInfoCallback()?.onGetKeyCheckValue(checkValue)
    }

    // POS Update callbacks
    override fun onReturnUpdateIPEKResult(arg0: Boolean) {
        Timber.d("MyQposClass: onReturnUpdateIPEKResult")
        getPosUpdateCallback()?.onReturnUpdateIPEKResult(arg0)
    }

    override fun onReturnSetMasterKeyResult(isSuccess: Boolean, result: Hashtable<String, String>?) {
        Timber.d("MyQposClass: onReturnSetMasterKeyResult")
        getPosUpdateCallback()?.onReturnSetMasterKeyResult(isSuccess, result)
    }

    override fun onReturnSetMasterKeyResult(isSuccess: Boolean) {
        Timber.d("MyQposClass: onReturnSetMasterKeyResult")
        getPosUpdateCallback()?.onReturnSetMasterKeyResult(isSuccess)
    }

    override fun onRequestUpdateWorkKeyResult(result: QPOSService.UpdateInformationResult?, checkValue: Hashtable<String, String>?) {
        Timber.d("MyQposClass: onRequestUpdateWorkKeyResult")
        getPosUpdateCallback()?.onRequestUpdateWorkKeyResult(result, checkValue)
    }

    override fun onRequestUpdateWorkKeyResult(result: QPOSService.UpdateInformationResult?) {
        Timber.d("MyQposClass: onRequestUpdateWorkKeyResult")
        getPosUpdateCallback()?.onRequestUpdateWorkKeyResult(result)
    }

    override fun onUpdatePosFirmwareResult(arg0: QPOSService.UpdateInformationResult?) {
        Timber.d("MyQposClass: onUpdatePosFirmwareResult")
        getPosUpdateCallback()?.onUpdatePosFirmwareResult(arg0)
    }

    override fun onReturnCustomConfigResult(isSuccess: Boolean, result: String?) {
        Timber.d("MyQposClass: onReturnCustomConfigResult")
        getPosUpdateCallback()?.onReturnCustomConfigResult(isSuccess, result)
    }

    override fun onRequestDevice() {
        Timber.d("MyQposClass: onRequestDevice")
        getPosUpdateCallback()?.onRequestDevice()
    }

    // Additional callbacks that might be needed
    override fun onRequestTransactionLog(tlv: String?) {
        Timber.d("MyQposClass: onRequestTransactionLog")
        // Handle transaction log if needed
    }

    override fun onReturnBatchSendAPDUResult(batchAPDUResult: LinkedHashMap<Int, String>?) {
        Timber.d("MyQposClass: onReturnBatchSendAPDUResult")
        // Handle batch APDU results if needed
    }

    override fun onBluetoothBoardStateResult(result: Boolean) {
        Timber.d("MyQposClass: onBluetoothBoardStateResult - $result")
        // Handle Bluetooth state if needed
    }

    override fun onReturnPowerOnIccResult(isSuccess: Boolean, ksn: String?, atr: String?, atrLen: Int) {
        Timber.d("MyQposClass: onReturnPowerOnIccResult")
        // Handle power on ICC result if needed
    }

    override fun onReturnPowerOffIccResult(isSuccess: Boolean) {
        Timber.d("MyQposClass: onReturnPowerOffIccResult")
        // Handle power off ICC result if needed
    }

    override fun onReturnApduResult(isSuccess: Boolean, apdu: String?, apduLen: Int) {
        Timber.d("MyQposClass: onReturnApduResult")
        // Handle APDU result if needed
    }

    fun onReturnPowerOnNfcResult(isSuccess: Boolean, ksn: String?, atr: String?, atrLen: Int) {
        Timber.d("MyQposClass: onReturnPowerOnNfcResult")
        // Handle NFC power on if needed
    }

    fun onReturnNfcApduResult(isSuccess: Boolean, apdu: String?, apduLen: Int) {
        Timber.d("MyQposClass: onReturnNfcApduResult")
        // Handle NFC APDU result if needed
    }

    fun onReturnPowerOffNfcResult(isSuccess: Boolean) {
        Timber.d("MyQposClass: onReturnPowerOffNfcResult")
        // Handle NFC power off if needed
    }

    override fun onCbcMacResult(result: String?) {
        Timber.d("MyQposClass: onCbcMacResult")
        // Handle CBC MAC result if needed
    }

    override fun onReadBusinessCardResult(isSuccess: Boolean, result: String?) {
        Timber.d("MyQposClass: onReadBusinessCardResult")
        // Handle business card reading if needed
    }

    override fun onWriteBusinessCardResult(isSuccess: Boolean) {
        Timber.d("MyQposClass: onWriteBusinessCardResult")
        // Handle business card writing if needed
    }

    override fun onConfirmAmountResult(isSuccess: Boolean) {
        Timber.d("MyQposClass: onConfirmAmountResult - $isSuccess")
        // Handle amount confirmation if needed
    }

    fun onSetManualKeyInResult(isSuccess: Boolean, result: String?) {
        Timber.d("MyQposClass: onSetManualKeyInResult")
        // Handle manual key input if needed
    }

    fun onSetParamsResult(isSuccess: Boolean, params: String?) {
        Timber.d("MyQposClass: onSetParamsResult")
        // Handle params setting if needed
    }

    fun onSetSleepTimeResult(isSuccess: Boolean) {
        Timber.d("MyQposClass: onSetSleepTimeResult")
        // Handle sleep time setting if needed
    }

    override fun onGetInputAmountResult(isSuccess: Boolean, amount: String?) {
        Timber.d("MyQposClass: onGetInputAmountResult")
        // Handle input amount if needed
    }
}