package com.basepos.pos.host.iso.transaction

import android.content.Context
import com.basepos.pos.common.domain.models.HostParameters
import com.basepos.pos.common.domain.repository.SessionManager
import com.basepos.pos.common.utils.Constants
import com.basepos.pos.common.utils.HexUtils
import com.basepos.pos.common.utils.ISOUtils
import com.basepos.pos.common.utils.cryptographyUtils.TripleDESUtils
import com.basepos.pos.host.R
import com.basepos.pos.host.iso.data.remote.rest.api.PosHostApiClient
import com.basepos.pos.host.iso.data.remote.socket.SocketChannel
import com.basepos.pos.host.iso.domain.enums.ISOProcCode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.jpos.iso.ISOException
import timber.log.Timber
import java.io.EOFException
import java.io.IOException
import javax.inject.Inject

/**
 * @Author: ifechukwu.udorji
 * @Date: 7/3/2024
 */

sealed class KeyExchangeResult {
    data class Error(val message: String): KeyExchangeResult()
    data object Loading: KeyExchangeResult()
    data class OnSuccess(val message: String): KeyExchangeResult()
}

class KeyExchangeHandler @Inject constructor(
    private val context: Context,
    private val sessionManager: SessionManager,
    private val socketChannel: SocketChannel,
    private val isoMessageBuilder: IsoMessageBuilder,
    private val posHostApiClient: PosHostApiClient
) {
    private val keyExchangeResult = MutableStateFlow<KeyExchangeResult>(KeyExchangeResult.Loading)
    val keyExchangeResultFlow = keyExchangeResult.asStateFlow()

    private var hostParameters: HostParameters? = null

    suspend fun startKeyExchangeTransaction() {
        hostParameters = sessionManager.getHostParameters()
        if (hostParameters == null) {
            keyExchangeResult.update {
                KeyExchangeResult.Error("Terminal Parameters not found")
            }
            return
        }


        doTMKTransaction()
    }

    private suspend fun doTMKTransaction() {
        try {
            val channel = socketChannel.setup()
            channel.connect()
            val tmkRequest =
                isoMessageBuilder.buildKeyExchangeMessage(ISOProcCode.TMK_DOWNLOAD_ISO_PROC_CODE)

            channel.send(tmkRequest)
            val response = channel.receive()
            channel.disconnect()

            val responseCode = response.getString(39)
            if (responseCode != Constants.ISO_SUCCESS) {
                val message = ISOUtils.getNibssMessage(responseCode)
                Timber.i("Message :::::::::::::: $message")
                keyExchangeResult.update {
                    KeyExchangeResult.Error("TMK Failed. Response code: $responseCode")
                }
                return
            }

            val field53 = response.getString(53)
            val masterKey =
                ISOUtils.getDecryptedTMKFromHost(field53, hostParameters?.componentKey!!).toString()

            val eTmk = field53.substring(0, 32)
            val kcv = field53.substring(32, 38)
            hostParameters?.tmkKCV = TripleDESUtils(HexUtils.hexStringToByte(masterKey)!!).getKCV()
            hostParameters?.encryptedTmk = eTmk
            hostParameters?.tmk = masterKey
            sessionManager.saveHostParameters(hostParameters!!)

            doTSKTransaction()
        } catch (e: ISOException) {
            Timber.e(e)
            keyExchangeResult.update {
                KeyExchangeResult.Error("TMK Failed\n${context.getString(R.string.error_packing_message)}")
            }
        } catch (e: EOFException) {
            Timber.e(e)
            keyExchangeResult.update {
                KeyExchangeResult.Error("TMK Failed\n${context.getString(R.string.host_disconnect)}")
            }
        } catch (e: IOException) {
            Timber.e(e)
            keyExchangeResult.update {
                KeyExchangeResult.Error("TMK Failed\n${context.getString(R.string.network_error_please_check_your_connection_and_try_again)}")
            }
        } catch (e: Exception) {
            Timber.e(e)
            keyExchangeResult.update {
                KeyExchangeResult.Error("TMK Failed\n${context.getString(R.string.an_error_occurred)}")
            }
        }
    }

    private suspend fun doTSKTransaction() {
        try {
            val channel = socketChannel.setup()
            channel.connect()
            val tskRequest =
                isoMessageBuilder.buildKeyExchangeMessage(ISOProcCode.TSK_DOWNLOAD_ISO_PROC_CODE)

            channel.send(tskRequest)
            val response = channel.receive()
            channel.disconnect()

            val responseCode = response.getString(39)
            if (responseCode != Constants.ISO_SUCCESS) {
                val message: String = ISOUtils.getNibssMessage(responseCode)
                Timber.i("TSK Failed ::::: $message")
                keyExchangeResult.update {
                    KeyExchangeResult.Error("TSK Failed. Response code: $responseCode")
                }
                return
            }


            val sessionKey =
                ISOUtils.getDecryptedKeyFromHost(response.getString(53), hostParameters?.tmk!!)
                    .toString()

            val field53 = response.getString(53)
            val eTSK = field53.substring(0, 32)
            val kcv = field53.substring(32, 38)
            hostParameters?.tskKCV = TripleDESUtils(HexUtils.hexStringToByte(sessionKey)!!).getKCV()
            hostParameters?.encryptedTsk = eTSK
            hostParameters?.tsk = sessionKey
            sessionManager.saveHostParameters(hostParameters!!)

            doTPKTransaction()
        } catch (e: ISOException) {
            Timber.e(e)
            keyExchangeResult.update {
                KeyExchangeResult.Error("TSK Failed\n${context.getString(R.string.error_packing_message)}")
            }
        } catch (e: EOFException) {
            Timber.e(e)
            keyExchangeResult.update {
                KeyExchangeResult.Error("TSK Failed\n${context.getString(R.string.host_disconnect)}")
            }
        } catch (e: IOException) {
            Timber.e(e)
            keyExchangeResult.update {
                KeyExchangeResult.Error("TSK Failed\n${context.getString(R.string.network_error_please_check_your_connection_and_try_again)}")
            }
        } catch (e: Exception) {
            Timber.e(e)
            keyExchangeResult.update {
                KeyExchangeResult.Error("TSK Failed\n${context.getString(R.string.an_error_occurred)}")
            }
        }
    }

    private suspend fun doTPKTransaction() {
        try {
            val channel = socketChannel.setup()
            channel.connect()
            val tpkRequest =
                isoMessageBuilder.buildKeyExchangeMessage(ISOProcCode.TPK_DOWNLOAD_ISO_PROC_CODE)

            channel.send(tpkRequest)
            val response = channel.receive()
            channel.disconnect()

            val responseCode = response.getString(39)
            if (responseCode != Constants.ISO_SUCCESS) {
                val message: String = ISOUtils.getNibssMessage(responseCode)
                Timber.i("TPK Failed ::::: $message")
                keyExchangeResult.update {
                    KeyExchangeResult.Error("TPK Failed. Response code: $responseCode")
                }
                return
            }

            val pinKey =
                ISOUtils.getDecryptedKeyFromHost(response.getString(53), hostParameters?.tmk!!)
                    .toString()
            val field53 = response.getString(53)
            val encryptedTerminalPinKey = field53.substring(0, 32)
            val kcv = field53.substring(32, 38)

            hostParameters?.tpkKCV = TripleDESUtils(HexUtils.hexStringToByte(pinKey)!!).getKCV()
            hostParameters?.encryptedTpk = encryptedTerminalPinKey
            hostParameters?.tpk = pinKey
            sessionManager.saveHostParameters(hostParameters!!)

            doParameterDownloadTransaction()
        } catch (e: ISOException) {
            Timber.e(e)
            keyExchangeResult.update {
                KeyExchangeResult.Error("TPK Failed\n${context.getString(R.string.error_packing_message)}")
            }
        } catch (e: EOFException) {
            Timber.e(e)
            keyExchangeResult.update {
                KeyExchangeResult.Error("TPK Failed\n${context.getString(R.string.host_disconnect)}")
            }
        } catch (e: IOException) {
            Timber.e(e)
            keyExchangeResult.update {
                KeyExchangeResult.Error("TPK Failed\n${context.getString(R.string.network_error_please_check_your_connection_and_try_again)}")
            }
        } catch (e: Exception) {
            Timber.e(e)
            keyExchangeResult.update {
                KeyExchangeResult.Error("TPK Failed\n${context.getString(R.string.an_error_occurred)}")
            }
        }
    }

    private suspend fun doParameterDownloadTransaction() {
        try {
            val channel = socketChannel.setup()
            channel.connect()

            val parameterDownloadRequest =
                isoMessageBuilder.buildKeyExchangeMessage(ISOProcCode.TERM_PARAM_DOWNLOAD_ISO_PROC_CODE)

            channel.send(parameterDownloadRequest)
            val response = channel.receive()
            channel.disconnect()

            val responseCode = response.getString(39)
            if (responseCode != Constants.ISO_SUCCESS) {
                val message: String = ISOUtils.getNibssMessage(responseCode)
                Timber.i("Terminal Param Failed ::::: $message")
                keyExchangeResult.update {
                    KeyExchangeResult.Error("Terminal Param Failed. Response code: $responseCode")
                }
                return
            }


            val field62 = response.getString(62)

            val merchantId = ISOUtils.parseTLV(field62, "03").toString()
            val merchantCategoryCode = ISOUtils.parseTLV(field62, "08").toString()
            val merchantLocation = ISOUtils.parseTLV(field62, "52").toString()
            val currencyCode = ISOUtils.parseTLV(field62, "05").toString()
            val countryCode = ISOUtils.parseTLV(field62, "06").toString()
            val ctmsTimeDate = ISOUtils.parseTLV(field62, "02").toString()

            hostParameters?.merchantId = merchantId
            hostParameters?.merchantName = merchantLocation
            hostParameters?.currencyCode = currencyCode
            hostParameters?.mcc = merchantCategoryCode
            hostParameters?.cardAcceptorLocation = merchantLocation
            hostParameters?.cardAcceptorId = merchantId


            if (hostParameters != null && !hostParameters?.tmk.isNullOrEmpty() && !hostParameters?.tpk.isNullOrEmpty()) {
                keyExchangeResult.update {
                    KeyExchangeResult.OnSuccess("Key Exchange Successful")
                }

                sessionManager.saveHostParameters(hostParameters!!)
            } else {
                //Restart Key Exchange
                startKeyExchangeTransaction()
            }

        } catch (e: ISOException) {
            Timber.e(e)
            keyExchangeResult.update {
                KeyExchangeResult.Error("Parameter Download Failed\n${context.getString(R.string.error_packing_message)}")
            }
        } catch (e: EOFException) {
            Timber.e(e)
            keyExchangeResult.update {
                KeyExchangeResult.Error("Parameter Download Failed\n${context.getString(R.string.host_disconnect)}")
            }
        } catch (e: IOException) {
            Timber.e(e)
            keyExchangeResult.update {
                KeyExchangeResult.Error("Parameter Download Failed\n${context.getString(R.string.network_error_please_check_your_connection_and_try_again)}")

            }
        } catch (e: Exception) {
            Timber.e(e)
            keyExchangeResult.update {
                KeyExchangeResult.Error("Parameter Download Failed\n${context.getString(R.string.an_error_occurred)}")
            }
        }
    }
}