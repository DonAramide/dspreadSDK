package com.basepos.pos.ui.keyexchange

import android.content.Context
import android.content.pm.PackageManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.basepos.pos.common.domain.models.Receipt
import com.basepos.pos.common.domain.models.ReceiptFields
import com.basepos.pos.common.domain.models.TmsParameterResponse
import com.basepos.pos.common.domain.printer.PrinterEngine
import com.basepos.pos.common.domain.printer.PrinterHelper
import com.basepos.pos.common.domain.printer.PrinterState
import com.basepos.pos.common.domain.repository.EmvListener
import com.basepos.pos.common.domain.repository.SessionManager
import com.basepos.pos.common.utils.AssetFileReader
import com.basepos.pos.common.utils.FileDownloadUtils
import com.basepos.pos.common.utils.TMSUtils
import com.basepos.pos.host.iso.data.local.ProcessedTransactionDao
import com.basepos.pos.host.iso.transaction.KeyExchangeHandler
import com.basepos.pos.host.iso.transaction.KeyExchangeResult
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


/**
 * @Author: ifechukwu.udorji
 * @Date: 7/22/2024
 */
sealed class KeyExchangeScreenResult {
    data class Error(val message: String): KeyExchangeScreenResult()
    data class ExchangeError(val message: String): KeyExchangeScreenResult()
    data class PinPadError(val message: String): KeyExchangeScreenResult()
    data object Loading: KeyExchangeScreenResult()
    data object Printing: KeyExchangeScreenResult()
    data class PrintError(val message: String): KeyExchangeScreenResult()
    data class PrintSuccess(val message: String): KeyExchangeScreenResult()
    data class Success(val message: String): KeyExchangeScreenResult()
}

@HiltViewModel
class KeyExchangeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val emvListener: EmvListener,
    private val printerEngine: PrinterEngine,
    private val printerHelper: PrinterHelper,
    private val sessionManager: SessionManager,
    private val keyExchangeHandler: KeyExchangeHandler,
    private val transactionDao: ProcessedTransactionDao,
    private val fileDownloadUtils: FileDownloadUtils,
) : ViewModel() {

    var keyExchangeResultState = MutableLiveData<KeyExchangeScreenResult>(KeyExchangeScreenResult.Loading)
        private set

    fun fetchTerminalParameters() = viewModelScope.launch(Dispatchers.IO) {
        try {
            val tmsParameterResponse = AssetFileReader.readParameterJsonFile(context)
            val currentTID = sessionManager.getHostParameters()?.terminalId
            val newTID = tmsParameterResponse.terminalID
            if (currentTID != newTID) {
                transactionDao.deleteAllTransactions()
            }

            sessionManager.saveTmsParameter(tmsParameterResponse)
            sessionManager.saveHostParameters(TMSUtils.mapTmsParamToHostParam(tmsParameterResponse))

            val hostTerminalParameters = sessionManager.getHostParameters()
            if (hostTerminalParameters == null) {
                keyExchangeResultState.postValue(KeyExchangeScreenResult.Error("Terminal settings error, go to settings and try again"))
                return@launch
            }

            val logoUrl = sessionManager.getTmsParameter()!!.bankLogo ?: ""
            fileDownloadUtils.downloadImageFromUrl(logoUrl) {
                startKeyExchangeTransaction()
            }
        } catch (e: Exception) {
            Timber.e("onCreate: ERROR ::::::::::::::::: ${e.message}")

            keyExchangeResultState.postValue(KeyExchangeScreenResult.Error("Terminal settings error"))
        }
    }

    fun printParameters() = viewModelScope.launch(Dispatchers.IO) {
        val receipt = Receipt(
            receipt = listOf(
                ReceiptFields(
                    logoPath = "",
                    stringFields = printerHelper.setupParametersPrintFields(),
                )
            )
        )

        printerEngine.printReceipt(receipt.receipt[0]).collectLatest { state ->
            when (state) {
                is PrinterState.Done -> {
                    keyExchangeResultState.postValue(KeyExchangeScreenResult.PrintSuccess("Print Successful"))
                }

                is PrinterState.Error -> {
                    Timber.e("onCreate: ERROR ::::::::::::::::: ${state.message}")

                    keyExchangeResultState.postValue(KeyExchangeScreenResult.PrintError(state.message))
                }

                is PrinterState.Printing -> {
                    keyExchangeResultState.postValue(KeyExchangeScreenResult.Printing)
                }
            }
        }
    }

    private fun startKeyExchangeTransaction() = viewModelScope.launch(Dispatchers.IO) {
        keyExchangeHandler.startKeyExchangeTransaction()
        observeKeyExchangeResult()
    }

    private fun observeKeyExchangeResult() = viewModelScope.launch(Dispatchers.IO) {
        keyExchangeHandler.keyExchangeResultFlow.collectLatest { result ->
            when (result) {
                is KeyExchangeResult.Loading -> {
                    Unit
                }

                is KeyExchangeResult.Error -> {
                    keyExchangeResultState.postValue(KeyExchangeScreenResult.ExchangeError(result.message))
                }

                is KeyExchangeResult.OnSuccess -> {
                    if (!emvListener.injectPinpadKeys()) {
                        keyExchangeResultState.postValue(KeyExchangeScreenResult.PinPadError("Pin Injection failed, please retry again"))
                    } else {
                        keyExchangeResultState.postValue(KeyExchangeScreenResult.Success("TMK Successful\nTSK Successful\nTPK Successful\nDownload Parameter Successful"))
                    }
                }
            }
        }
    }
}