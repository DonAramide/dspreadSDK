package com.basepos.pos.dspread.printer

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import com.action.printerservice.PrintStyle
import com.basepos.pos.common.domain.models.ReceiptFields
import com.basepos.pos.common.domain.printer.PrinterEngine
import com.basepos.pos.common.domain.printer.PrinterState
import com.basepos.pos.common.utils.FileDownloadUtils
import com.basepos.pos.dspread.utils.DeviceUtils
import com.dspread.print.device.PrintListener
import com.dspread.print.device.PrinterDevice
import com.dspread.print.device.PrinterInitListener
import com.dspread.print.device.PrinterManager
import com.dspread.print.device.bean.PrintLineStyle
import com.dspread.print.widget.PrintLine
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject

/**
 * @Author: ifechukwu.udorji
 * @Date: 7/22/2024
 */
class DSpreadPrinterEngine @Inject constructor(
    private val context: Context,
    private val dispatcher: CoroutineDispatcher,
    private val fileDownloadUtils: FileDownloadUtils
) : PrinterEngine {
    override suspend fun printReceipt(receipt: ReceiptFields, isEod: Boolean) = callbackFlow {
        try {
            trySend(PrinterState.Printing)

            // Add delay to prevent DEVICE_BUSY errors after POS operations (like working project)
            kotlinx.coroutines.delay(150)

            val printer = PrinterManager.getInstance().printer
            if (printer == null) {
                trySend(PrinterState.Error("Printer is not initialized"))
                return@callbackFlow
            }

            // Improved device-specific initialization logic based on working project
            if ("D30".equals(Build.MODEL, ignoreCase = true) || DeviceUtils.isAppInstalled(
                    context,
                    DeviceUtils.UART_AIDL_SERVICE_APP_PACKAGE_NAME
                )
            ) {
                // Use callback-based initialization for D30 devices (like working project)
                printer.initPrinter(context, object : PrinterInitListener {
                    override fun connected() {
                        Timber.d("Printer connected successfully")
                        printer.setPrinterTerminatedState(PrinterDevice.PrintTerminationState.PRINT_STOP)
                    }

                    override fun disconnected() {
                        Timber.w("Printer disconnected")
                    }
                })
                // Add additional delay for callback-based initialization
                kotlinx.coroutines.delay(100)
            } else {
                // Simple initialization for non-D30 devices
                printer.initPrinter(context)
                kotlinx.coroutines.delay(50)
            }

            var bitmap: Bitmap? = null
            if (receipt.logoPath.isNotBlank()) {
                bitmap = fileDownloadUtils.getLogoBitmap()
            }

            if (bitmap != null) {
                printer.addBitmap(bitmap)
            }

            for (field in receipt.stringFields) {
                if (field.isMultiline) {
                    printer.addPrintLintStyle(
                        PrintLineStyle(
                            if (field.header.isBold == true) PrintStyle.FontStyle.BOLD else PrintStyle.FontStyle.NORMAL,
                            PrintStyle.Alignment.CENTER,
                            16
                        )
                    )
                    printer.addText(field.header.text)

                    if (field.body != null && field.body!!.text.isNotEmpty()) {
                        printer.addPrintLintStyle(
                            PrintLineStyle(
                                if (field.header.isBold == true) PrintStyle.FontStyle.BOLD else PrintStyle.FontStyle.NORMAL,
                                PrintStyle.Alignment.CENTER,
                                12
                            )
                        )
                        printer.addText(field.body!!.text)
                    }
                } else {
                    if (field.header.text.isNotBlank() || (field.body != null && field.body!!.text.isNotBlank())) {
                        val width = 35

                        val formattedText = if (isEod) {
                            java.lang.String.format(
                                "%-" + (width / 2).toString() + "s %-" + (width / 2).toString() + "s",
                                "${field.header.text.uppercase()}",
                                "${field.body?.text}"
                            )
                        } else {
                            java.lang.String.format(
                                "%-" + (width / 2).toString() + "s %-" + (width / 2).toString() + "s",
                                "${field.header.text.uppercase()}:",
                                "${field.body?.text}"
                            )
                        }

                        printer.addPrintLintStyle(
                            PrintLineStyle(
                                if (field.header.isBold == true) PrintStyle.FontStyle.BOLD else PrintStyle.FontStyle.NORMAL,
                                PrintStyle.Alignment.NORMAL,
                                12
                            )
                        )
                        printer.addText(formattedText)
                    }
                }
            }

            printer.print(context)
            printer.setPrintListener(object : PrintListener {
                override fun printResult(p0: Boolean, p1: String?, p2: PrinterDevice.ResultType?) {
                    Timber.d("printResult: $p0, $p1, $p2")
                    if (p0) {
                        trySend(PrinterState.Done(receipt.stringFields.size > 1))
                    } else {
                        trySend(PrinterState.Error("Print failed: $p1"))
                    }
                }
            })
        } catch (exception: Exception) {
            Timber.e(exception, "Printer operation failed")

            // Improved error messages based on common printer issues
            val errorMessage = when {
                exception.message?.contains("DEVICE_BUSY", ignoreCase = true) == true ->
                    "Printer is busy. Please wait and try again."
                exception.message?.contains("connection", ignoreCase = true) == true ->
                    "Printer connection failed. Please check printer connection."
                else -> "Print error: ${exception.message}"
            }

            trySend(PrinterState.Error(errorMessage))

            // Try to cleanup printer state on error
            try {
                val printer = PrinterManager.getInstance().printer
                printer?.setPrinterTerminatedState(PrinterDevice.PrintTerminationState.PRINT_STOP)
            } catch (e: Exception) {
                Timber.w(e, "Failed to cleanup printer state")
            }
        } finally {
            awaitClose {
                // Proper cleanup
                try {
                    val printer = PrinterManager.getInstance().printer
                    printer?.setPrinterTerminatedState(PrinterDevice.PrintTerminationState.PRINT_STOP)
                } catch (e: Exception) {
                    Timber.w(e, "Failed to cleanup printer in finally block")
                }
                channel.close()
            }
        }
    }.flowOn(dispatcher)
}