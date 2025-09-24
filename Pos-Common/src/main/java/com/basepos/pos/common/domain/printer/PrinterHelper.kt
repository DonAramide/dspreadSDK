package com.basepos.pos.common.domain.printer

import android.content.Context
import com.basepos.pos.common.domain.enums.TransactionType
import com.basepos.pos.common.domain.models.FieldAlign
import com.basepos.pos.common.domain.models.StringFields
import com.basepos.pos.common.domain.models.TextField
import com.basepos.pos.common.domain.models.TransactionResponse
import com.basepos.pos.common.domain.models.line
import com.basepos.pos.common.domain.repository.SessionManager
import com.basepos.pos.common.utils.AmountUtils
import com.basepos.pos.common.utils.UtilMethods
import java.util.Date
import javax.inject.Inject

/**
 * @Author: ifechukwu.udorji
 * @Date: 7/3/2024
 */
class PrinterHelper @Inject constructor(
    private val context: Context,
    private val sessionManager: SessionManager
) {

    fun setupTransactionPrintFields(
        receiptType: String,
        paymentResponse: TransactionResponse
    ): MutableList<StringFields> {
        val tmsParameter = sessionManager.getTmsParameter()!!
        val isReprint = receiptType == "***** REPRINT *****"

        val printFields = mutableListOf<StringFields>()
        printFields.add(
            StringFields(
                isMultiline = true,
                header = TextField(
                    text = receiptType,
                    isBold = true,
                    align = FieldAlign.center,
                ),
            ),
        )
        printFields.add(
            StringFields(
                isMultiline = true,
                header = TextField(
                    text = tmsParameter.merchantName ?: "N/A",
                    isBold = true,
                    align = FieldAlign.center
                ),
            ),
        )
        printFields.add(
            StringFields(
                isMultiline = true,
                header = TextField(
                    text = tmsParameter.location ?: "N/A",
                    isBold = true,
                    align = FieldAlign.center
                ),
            ),
        )

        printFields.add(
            StringFields(
                isMultiline = true,
                header = TextField(text = line),
            ),
        )

        if (paymentResponse.transactionType != null && paymentResponse.transactionType != TransactionType.AUTOREVERSAL) {
            printFields.add(
                StringFields(
                    isMultiline = true,
                    header = TextField(
                        text = paymentResponse.transactionType!!.value,
                        isBold = true,
                        align = FieldAlign.center
                    )
                )
            )
        }

        printFields.add(
            StringFields(
                header = TextField(text = "Terminal ID"),
                body = TextField(
                    text = paymentResponse.terminalID
                )
            )
        )

        printFields.add(
            StringFields(
                header = TextField(text = "Merchant ID"),
                body = TextField(
                    text = tmsParameter.merchantID ?: "N/A"
                )
            )
        )

        printFields.add(
            StringFields(
                header = TextField(text = "AID"),
                body = TextField(text = paymentResponse.aid ?: "N/A")
            )
        )

        printFields.add(
            StringFields(
                header = TextField(text = "RRN"),
                body = TextField(text = paymentResponse.rrn)
            )
        )

        printFields.add(
            StringFields(
                header = TextField(text = "Stan"),
                body = TextField(text = paymentResponse.stan)
            )
        )

        if (paymentResponse.transactionType == TransactionType.PURCHASEWITHCB) {
            printFields.add(
                StringFields(
                    isMultiline = true,
                    header = TextField(text = line),
                )
            )

            printFields.add(
                StringFields(
                    header = TextField(text = "Cashback Amount"),
                    body = TextField(
                        text = "NGN ${AmountUtils.formatToTwoDecimalPlaces(paymentResponse.cashbackAmount)}"
                    )
                )
            )
            printFields.add(
                StringFields(
                    header = TextField(text = "Total Amount"),
                    body = TextField(
                        text = "NGN ${
                            AmountUtils.formatToTwoDecimalPlaces(
                                paymentResponse.calculateTotalAmount().toString()
                            )
                        }"
                    )
                )
            )
        }

        printFields.add(
            StringFields(
                isMultiline = true,
                header = TextField(text = line)
            )
        )

        printFields.add(
            StringFields(
                isMultiline = true,
                header = TextField(
                    text = "NGN ${AmountUtils.formatToTwoDecimalPlaces(paymentResponse.amount)}",
                    align = FieldAlign.center
                )
            )
        )

        val message = when {
            paymentResponse.isApproved() -> "Transaction Approved"
            paymentResponse.isTimeOut() -> "TRANSACTION TIME OUT"
            paymentResponse.isAutoReversal() -> "AUTO-REVERSED"
            else -> "Transaction Declined"
        }

        printFields.add(
            StringFields(
                isMultiline = true,
                header = TextField(text = message, align = FieldAlign.center)
            )
        )

        printFields.add(
            StringFields(
                isMultiline = true,
                header = TextField(text = line)
            )
        )

        printFields.add(
            StringFields(
                header = TextField(text = "App Label"),
                body = TextField(text = paymentResponse.appLabel ?: "N/A")
            )
        )

        if (paymentResponse.authCode != null) {
            printFields.add(
                StringFields(
                    header = TextField(text = "Auth Code"),
                    body = TextField(text = paymentResponse.authCode!!)
                )
            )
        }

        printFields.add(
            StringFields(
                header = TextField(text = "Card Exp"),
                body = TextField(text = paymentResponse.cardExpireDate)
            )
        )

        printFields.add(
            StringFields(
                header = TextField(text = "Card Name"),
                body = TextField(text = paymentResponse.cardHolderName ?: "N/A")
            )
        )

        printFields.add(
            StringFields(
                header = TextField(text = "Masked PAN"),
                body = TextField(text = paymentResponse.maskedPan)
            )
        )

        printFields.add(
            StringFields(
                header = TextField(text = "Date/Time"),
                body = TextField(text = paymentResponse.dateTime)
            )
        )

        if (paymentResponse.statusCode.isNotEmpty()) {
            printFields.add(
                StringFields(
                    header = TextField(text = "Status Code"),
                    body = TextField(text = paymentResponse.statusCode)
                )
            )

            printFields.add(
                StringFields(
                    header = TextField(text = "Status Message"),
                    body = TextField(text = paymentResponse.message)
                )
            )
        }

        printFields.add(
            StringFields(
                isMultiline = true,
                header = TextField(text = line)
            )
        )

        printFields.addAll(footerFields())

        return printFields
    }

    fun setupEodPrintFields(eodTransactions: List<TransactionResponse>, selectedDate: Long): MutableList<StringFields> {
        val terminalParameter = sessionManager.getHostParameters()
        val tmsParameter = sessionManager.getTmsParameter()!!

        val printFields = mutableListOf<StringFields>()
        printFields.add(
            StringFields(
                isMultiline = true,
                header = TextField(
                    text = "***** EOD *****",
                    isBold = true
                ),
            ),
        )
        printFields.add(
            StringFields(
                isMultiline = true,
                header = TextField(
                    text = tmsParameter.merchantName ?: "N/A",
                    isBold = true
                ),
            ),
        )
        printFields.add(
            StringFields(
                header = TextField(text = "Terminal ID"),
                body = TextField(
                    text = tmsParameter.terminalID ?: "N/A"
                )
            )
        )
        printFields.add(
            StringFields(
                header = TextField(text = "Merchant ID"),
                body = TextField(
                    text = tmsParameter.merchantID ?: "N/A"
                )
            )
        )
        printFields.add(
            StringFields(
                header = TextField(text = "Date"),
                body = TextField(
                    text = UtilMethods.formatDateToCustomFormat(selectedDate)
                )
            )
        )
        printFields.add(line())

        val totalApproved =
            eodTransactions.filter { it.statusCode == "00" }
                .sumOf { it.calculateTotalAmount() }
        val totalDeclined =
            eodTransactions.filter { it.statusCode != "00" }
                .sumOf { it.calculateTotalAmount() }
        val totalApprovedCount = eodTransactions.filter { it.statusCode == "00" }.size
        val totalDeclinedCount = eodTransactions.filter { it.statusCode != "00" }.size

        printFields.add(
            StringFields(
                header = TextField(text = "Total Count"),
                body = TextField(text = eodTransactions.size.toString())
            )
        )
        printFields.add(
            StringFields(
                header = TextField(text = "Approved Amount"),
                body = TextField(text = "₦${AmountUtils.formatToTwoDecimalPlaces(totalApproved.toString())}")
            )
        )
        printFields.add(
            StringFields(
                header = TextField(text = "Approved Count"),
                body = TextField(text = totalApprovedCount.toString())
            )
        )
        printFields.add(
            StringFields(
                header = TextField(text = "Declined Amount"),
                body = TextField(text = "₦${AmountUtils.formatToTwoDecimalPlaces(totalDeclined.toString())}")
            )
        )
        printFields.add(
            StringFields(
                header = TextField(text = "Declined Count"),
                body = TextField(text = totalDeclinedCount.toString())
            )
        )

        printFields.add(line())

        printFields.add(
            StringFields(
                header = TextField(text = "Time    Type    Amount    RRN"),
                body = TextField(text = "")
            )
        )

        printFields.add(
            StringFields(
                header = TextField(text = "PAN    Status"),
                body = TextField(text = "")
            )
        )

        printFields.add(line())

        for (transaction in eodTransactions) {
            printFields.add(
                StringFields(
                    header = TextField(
                        text = "${transaction.dateTime.takeLast(11)}  ${transaction.transactionType}  ₦${
                            AmountUtils.formatToTwoDecimalPlaces(
                                transaction.calculateTotalAmount().toString()
                            )
                        }"
                    ),
                    body = TextField(text = "")
                )
            )

            printFields.add(
                StringFields(
                    header = TextField(
                        text = "${transaction.rrn}  ${transaction.maskedPan}"
                    ),
                    body = TextField(text = "")
                )
            )

            printFields.add(
                StringFields(
                    header = TextField(
                        text = mapTransactionStatusCode(transaction.statusCode)
                    ),
                    body = TextField(text = "")
                )
            )

            printFields.add(line())
        }

        printFields.addAll(footerFields())

        printFields.add(
            StringFields(
                isMultiline = true,
                header = TextField(text = ""),
                body = TextField(text = "")
            ),
        )

        return printFields
    }

    fun setupParametersPrintFields(): MutableList<StringFields> {
        val hostParameters = sessionManager.getHostParameters()!!
        val tmsParameter = sessionManager.getTmsParameter()!!

        return mutableListOf(
            StringFields(
                isMultiline = true,
                header = TextField(
                    text = "TERMINAL PARAMETERS",
                    isBold = true
                ),
                body = TextField(
                    text = "",
                )
            ),
            StringFields(
                isMultiline = false,
                header = TextField(
                    text = "Terminal ID"
                ),
                body = TextField(
                    text = tmsParameter.terminalID ?: "N/A",
                )
            ),
            StringFields(
                isMultiline = false,
                header = TextField(
                    text = "Device ID",
                ),
                body = TextField(
                    text = sessionManager.getTerminalSerialNo(),
                )
            ),
            StringFields(
                isMultiline = false,
                header = TextField(
                    text = "Merchant ID",
                ),
                body = TextField(
                    text = tmsParameter.merchantID ?: "N/A",
                )
            ),
            StringFields(
                isMultiline = false,
                header = TextField(
                    text = "Merchant Name",
                ),
                body = TextField(
                    text = tmsParameter.merchantName ?: "N/A",
                )
            ),
            StringFields(
                isMultiline = false,
                header = TextField(
                    text = "Merchant Location",
                ),
                body = TextField(
                    text = tmsParameter.location ?: "N/A",
                )
            ),
            StringFields(
                isMultiline = false,
                header = TextField(
                    text = "Server IP",
                ),
                body = TextField(
                    text = hostParameters.serverIP ?: "N/A",
                )
            ),
            StringFields(
                isMultiline = false,
                header = TextField(
                    text = "Server PORT",
                ),
                body = TextField(
                    text = hostParameters.port.toString() ?: "N/A",
                )
            ),
            StringFields(
                isMultiline = false,
                header = TextField(
                    text = "SSL",
                ),
                body = TextField(
                    text = hostParameters.enableSSL.toString() ?: "N/A",
                )
            ),
            StringFields(
                isMultiline = false,
                header = TextField(
                    text = "Date/Time",
                ),
                body = TextField(
                    text = Date().toString() ?: "N/A",
                )
            ),
            StringFields(
                isMultiline = false,
                header = TextField(
                    text = "Application Version",
                ),
                body = TextField(
                    text = UtilMethods.getPackageInfo(context)?.versionName ?: "N/A",
                )
            ),
        )
    }

    fun setupTestPrintFields(): MutableList<StringFields> {
        return mutableListOf(
            StringFields(
                isMultiline = true,
                header = TextField(
                    text = "******",
                    isBold = true
                ),
                body = TextField(
                    text = "",
                )
            ),
        )
    }

    private fun mapTransactionStatusCode(statusCode: String): String {
        return when (statusCode) {
            "00" -> "Approved"
            else -> "Declined"
        }
    }

    private fun footerFields(): List<StringFields> {
        return listOf(
            line(),

            StringFields(
                isMultiline = true,
                header = TextField(
                    text = "THANKS FOR USING BASEPOS AGENT",
                ),
            ),

            StringFields(
                isMultiline = true,
                header = TextField(
                    text = "VERSION: ${UtilMethods.getPackageInfo(context)?.versionName ?: "N/A"}",
                ),
            ),

            line()
        )
    }
}