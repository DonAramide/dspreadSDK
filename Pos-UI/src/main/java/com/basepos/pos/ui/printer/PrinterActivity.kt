package com.basepos.pos.ui.printer

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.basepos.pos.common.domain.models.AppResponseCodes
import com.basepos.pos.common.domain.models.Receipt
import com.basepos.pos.common.domain.models.ReceiptFields
import com.basepos.pos.common.domain.printer.PrinterEngine
import com.basepos.pos.common.domain.printer.PrinterState
import com.basepos.pos.common.utils.FileDownloadUtils
import com.basepos.pos.common.utils.showAlertDialog
import com.basepos.pos.ui.R
import com.basepos.pos.ui.databinding.ActivityPrinterBinding
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class PrinterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPrinterBinding
    // Retrieve the incoming intent
    private var incomingIntent = lazy { intent }

    @Inject
    lateinit var printerEngine: PrinterEngine

    @Inject
    lateinit var fileDownloadUtils: FileDownloadUtils

    private var receipt: Receipt? = null
    private var printCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrinterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        incomingIntent.value?.let { intent ->
            val printJson = intent.getStringExtra("jsonData")
            if (printJson != null) {
                receipt = Gson().fromJson(printJson, Receipt::class.java)
                if (receipt != null && receipt!!.receipt.isNotEmpty()) {
                    printReceipt(receipt!!.receipt[0])
                } else {
                    showAlertDialog(
                        title = getString(R.string.printer),
                        message = getString(R.string.invalid_receipt_object),
                        positiveTitle = getString(R.string.close),
                        showNegativeButton = false,
                    ) {
                        closePrinter(AppResponseCodes.INVALID_FORMAT.code)
                    }
                }
            } else {
                showAlertDialog(
                    title = getString(R.string.printer),
                    message = getString(R.string.invalid_receipt_object),
                    positiveTitle = getString(R.string.close),
                    showNegativeButton = false
                ) {
                    closePrinter(AppResponseCodes.INVALID_FORMAT.code)
                }
            }
        }
    }

    private fun printReceipt(receiptFields: ReceiptFields) {
        lifecycleScope.launch {
            printerEngine.printReceipt(receiptFields)
                .collectLatest { state ->
                    when (state) {
                        is PrinterState.Done -> {
                            withContext(Dispatchers.Main) {
                                printCount++;
                                Snackbar.make(binding.root, "Printing done", Snackbar.LENGTH_SHORT)
                                    .show()

                                if (receipt!!.receipt.size == 1 || printCount == receipt!!.receipt.size) {
                                    closePrinter(AppResponseCodes.SUCCESS.code)
                                } else {
                                    showAlertDialog(
                                        title = "Printer",
                                        message = "Continue printing",
                                        positiveTitle = "Continue",
                                        negativeTitle = "Finish",
                                        onPositiveButtonPressed = {
                                            if (it) {
                                                printReceipt(receipt!!.receipt[1])
                                            } else {
                                                closePrinter(AppResponseCodes.SUCCESS.code)
                                            }
                                        }
                                    )
                                }
                            }
                        }

                        is PrinterState.Error -> {
                            Timber.e("onCreate: ERROR ::::::::::::::::: ${state.message}")

                            closePrinter(AppResponseCodes.FAILED.code)
                        }

                        is PrinterState.Printing -> {
                            Timber.d("onCreate: PRINTING :::::::::::::::::")
                        }
                    }
                }
        }
    }

    private fun closePrinter(status: String) {
        // Create an intent to send the response back to calling app
        val responseIntent = Intent()
        responseIntent.putExtra("status", status)

        // Set the result to be sent back to the calling app
        setResult(RESULT_OK, responseIntent)
        finish()
    }
}