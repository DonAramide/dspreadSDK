package com.basepos.pos.host.iso.transaction

import com.basepos.pos.common.domain.enums.PosEntryMode
import com.basepos.pos.common.domain.enums.TransactionType
import com.basepos.pos.common.domain.models.EmvTransactionDetails
import com.basepos.pos.common.domain.models.PaymentRequest
import com.basepos.pos.common.domain.repository.SessionManager
import com.basepos.pos.common.utils.AmountUtils
import com.basepos.pos.common.utils.Constants
import com.basepos.pos.common.utils.ISOUtils
import com.basepos.pos.common.utils.cryptographyUtils.Sha256Utils
import com.basepos.pos.host.iso.data.local.entities.ProcessedTransactionEntity
import com.basepos.pos.host.iso.domain.enums.ISOMessageType
import com.basepos.pos.host.iso.domain.enums.ISOProcCode
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils
import org.jpos.iso.ISODate
import org.jpos.iso.ISOMsg
import org.jpos.iso.ISOUtil
import java.util.Date
import javax.inject.Inject

/**
 * @Author: ifechukwu.udorji
 * @Date: 6/3/2024
 */
class IsoMessageBuilder @Inject constructor(
    private val sessionManager: SessionManager,
    private val isoPackager: IsoPackager
) {
    private val date = Date()
    private val transactionDate = ISODate.getDate(date)
    private val transactionTime = ISODate.getTime(date)
    private val transactionDateTime = ISODate.getDateTime(date)

    fun buildKeyExchangeMessage(processingCode: ISOProcCode): ISOMsg {
        val terminalParameters = sessionManager.getHostParameters()!!

        val isoMsg = ISOMsg()
        isoMsg.packager = isoPackager
        isoMsg.mti = ISOMessageType._0800.value
        isoMsg.set(3, processingCode.value)
        isoMsg.set(7, transactionDateTime)
        isoMsg.set(11, ISOUtils.getStan())
        isoMsg.set(12, transactionTime)
        isoMsg.set(13, transactionDate)
        isoMsg.set(41, terminalParameters.terminalId)

        if (processingCode == ISOProcCode.TERM_PARAM_DOWNLOAD_ISO_PROC_CODE) {
            isoMsg.set(62, "01008".plus(terminalParameters.terminalId))
            isoMsg.set(64, ISOUtil.hex2byte(Constants.SIXTY_FOUR_ZEROS))
            isoMsg.recalcBitMap()

            val prePack = isoMsg.pack()
            isoMsg.set(
                64,
                Sha256Utils.performSha256Hash(
                    ISOUtil.trim(prePack, prePack.size - 64),
                    ISOUtil.hex2byte(terminalParameters.tsk)
                )
            )
        }

        return isoMsg
    }

    fun buildIsoMessage(
        emvTransactionDetails: EmvTransactionDetails,
        originalTransaction: ProcessedTransactionEntity?,
        paymentRequest: PaymentRequest
    ): ISOMsg {
        val terminalParameters = sessionManager.getHostParameters()!!
        val transactionType = paymentRequest.transType

        val mti = when (transactionType) {
            TransactionType.PURCHASE,
            TransactionType.PURCHASEWITHCB,
            TransactionType.REFUND,
            TransactionType.CASHADVANCE -> ISOMessageType._0200.value

            TransactionType.PREAUTH,
            TransactionType.BALANCE -> ISOMessageType._0100.value

            TransactionType.PREAUTHCOMPLETE -> ISOMessageType._0220.value
            TransactionType.REVERSAL,
            TransactionType.AUTOREVERSAL -> ISOMessageType._0420.value
        }
        val processingCode = getProcessingCode(
            transactionType,
            emvTransactionDetails.accountType?.code ?: "00",
            originalTransaction?.transactionProcessCode
        )
        val stan = when (transactionType) {
            TransactionType.REFUND,
            TransactionType.PREAUTHCOMPLETE,
            TransactionType.REVERSAL,
            TransactionType.AUTOREVERSAL -> originalTransaction?.stan
            else -> paymentRequest.stan
        }
        val rrn = when (transactionType) {
            TransactionType.REFUND,
            TransactionType.PREAUTHCOMPLETE,
            TransactionType.REVERSAL,
            TransactionType.AUTOREVERSAL-> originalTransaction?.rrn
            else -> paymentRequest.rrn
        }
        val transactionDateTime = when (transactionType) {
            TransactionType.REVERSAL,
            TransactionType.AUTOREVERSAL -> originalTransaction?.hostDateTime
            else -> transactionDateTime
        }
        val transactionTime = when (transactionType) {
            TransactionType.REVERSAL -> originalTransaction?.transactionTime
            else -> transactionTime
        }
        val transactionDate = when (transactionType) {
            TransactionType.REVERSAL,
            TransactionType.AUTOREVERSAL -> originalTransaction?.transactionDate
            else -> transactionDate
        }

        val cardInfo = emvTransactionDetails.cardInfo
        val terminalInfo = emvTransactionDetails.terminalInfo

        val isoMessageRequest = ISOMsg()
        isoMessageRequest.packager = isoPackager
        isoMessageRequest.mti = mti
        isoMessageRequest.set(2, cardInfo?.pan)
        isoMessageRequest.set(3, processingCode)
        if (emvTransactionDetails.transactionType == TransactionType.BALANCE) {
            isoMessageRequest.set(4, "000000000000")
        } else {
            isoMessageRequest.set(
                4, AmountUtils.toIsoAmount(
                    amount = emvTransactionDetails.amount,
                    currencyCode = terminalInfo?.currencyCode ?: "566"
                )
            )
        }
        isoMessageRequest.set(7, transactionDateTime)
        isoMessageRequest.set(11, stan)
        isoMessageRequest.set(12, transactionTime)
        isoMessageRequest.set(13, transactionDate)
        isoMessageRequest.set(14, cardInfo?.expiry)
        isoMessageRequest.set(18, terminalInfo?.mcc)

        val posEntryMode = emvTransactionDetails.terminalInfo?.posEntryMode
        isoMessageRequest.set(22, posEntryMode?.data)

        if (posEntryMode == PosEntryMode.ICC || posEntryMode == PosEntryMode.CONTACTLESS_ICC) {
            if (cardInfo?.sequenceNumber?.isNotEmpty() == true) {
                isoMessageRequest.set(23, ISOUtil.zeropad(cardInfo.sequenceNumber, 3))
            }
            isoMessageRequest.set(55, cardInfo?.iccData)
        }

        isoMessageRequest.set(25, terminalInfo?.posConditionCode)
        isoMessageRequest.set(26, terminalInfo?.posPinCaptureCode)
        isoMessageRequest.set(28, "D00000000")
        isoMessageRequest.set(32, cardInfo?.acquirerInstitutionId)
        isoMessageRequest.set(35, cardInfo?.track2)
        isoMessageRequest.set(37, rrn)
        isoMessageRequest.set(40, cardInfo?.serviceCode)
        isoMessageRequest.set(41, terminalInfo?.terminalId)
        isoMessageRequest.set(42, terminalInfo?.merchantId)
        isoMessageRequest.set(43, terminalInfo?.merchantNameAndLocation)
        isoMessageRequest.set(49, terminalInfo?.currencyCode)
        cardInfo?.pinData?.let { block ->
            if (block.length == 16) {
                isoMessageRequest.set(52, block) //Pin Data
            }
        }

        if (transactionType == TransactionType.PURCHASEWITHCB) {
            val field54 =
                emvTransactionDetails.accountType?.code + "40" + terminalInfo?.currencyCode +
                        "D" + AmountUtils.toIsoAmount(emvTransactionDetails.cashbackAmount, terminalInfo!!.currencyCode!!)

            isoMessageRequest.set(54, field54)
        }

        if (transactionType == TransactionType.REVERSAL || transactionType == TransactionType.AUTOREVERSAL) {
            isoMessageRequest.set(56, Constants.REASON_TIMEOUT)
        }

        if (transactionType == TransactionType.REFUND) {
            isoMessageRequest.set(56, Constants.REASON_CUSTOMER_CANCELLATION)
        }

        isoMessageRequest.set(59, "${terminalInfo?.terminalId}-${terminalInfo?.terminalSerialNumber}-${rrn}")

        if (transactionType == TransactionType.PREAUTHCOMPLETE || transactionType == TransactionType.REVERSAL || transactionType == TransactionType.AUTOREVERSAL) {
            isoMessageRequest.set(
                90,
                originalDataElement(
                    originalMessageType = originalTransaction?.transactionMti ?: "",
                    originalSTAN = originalTransaction?.stan ?: "",
                    originalTransmissionDateTime = originalTransaction?.dateTime ?: "",
                    originalAcquirerInstitutionId = originalTransaction?.cardInfo?.acquirerInstitutionId
                        ?: "",
                    originalForwardingInstitutionId = originalTransaction?.forwardingInstitutionId
                        ?: ""
                )
            )

            isoMessageRequest.set(95, replacementAmountElement(isoMessageRequest.getString(4)))
        }

        if (posEntryMode == PosEntryMode.CONTACTLESS_ICC) {
            isoMessageRequest.set(123, "A11101713344101")
        } else {
            isoMessageRequest.set(123, "A11101513344101")
        }

        isoMessageRequest.set(128, ISOUtil.hex2byte(Constants.SIXTY_FOUR_ZEROS))
        isoMessageRequest.recalcBitMap()

        val prePack = isoMessageRequest.pack()
        isoMessageRequest.set(
            128,
            Sha256Utils.performSha256Hash(
                ISOUtil.trim(prePack, prePack.size - 64),
                ISOUtil.hex2byte(terminalParameters.tsk)
            )
        )
        return isoMessageRequest
    }

    private fun originalDataElement(
        originalMessageType: String,
        originalSTAN: String,
        originalTransmissionDateTime: String,
        originalAcquirerInstitutionId: String,
        originalForwardingInstitutionId: String
    ): String {
        return StringBuilder()
            .append(originalMessageType)
            .append(originalSTAN)
            .append(originalTransmissionDateTime)
            .append(originalAcquirerInstitutionId.padStart(11, '0'))
            .append(originalForwardingInstitutionId.padStart(11, '0'))
            .toString()
    }

    private fun additionalAmountElement(
        currencyCode: String,
        amount: String
    ): String {
        return StringBuilder()
            .append("00")
            .append("40")//Todo -> Investigate why this is 40
            .append(currencyCode)
            .append("D")
            .append(amount)
            .toString()
    }

    private fun replacementAmountElement(
        amount: String
    ): String {
        return StringBuilder()
            .append(amount)
            .append("000000000000")
            .append("D00000000")
            .append("D00000000")
            .toString()
    }

    private fun getProcessingCode(transactionType: TransactionType, accountType: String, reversalProcessingCode: String?): String? {
        return when (transactionType) {
            TransactionType.PURCHASE -> "00" + accountType + "00"
            TransactionType.PURCHASEWITHCB -> "09" + accountType + "00"
            TransactionType.BALANCE -> "31" + accountType + "00"
            TransactionType.REFUND -> "20" + accountType + "00"
            TransactionType.CASHADVANCE -> "01" + accountType + "00"
            TransactionType.PREAUTH -> "60" + accountType + "00"
            TransactionType.PREAUTHCOMPLETE -> "61" + accountType + "00"
            TransactionType.REVERSAL,
            TransactionType.AUTOREVERSAL -> reversalProcessingCode
        }
    }
}