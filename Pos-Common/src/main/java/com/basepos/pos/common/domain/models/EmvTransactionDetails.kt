package com.basepos.pos.common.domain.models

import android.os.Parcelable
import com.basepos.pos.common.domain.enums.AccountType
import com.basepos.pos.common.domain.enums.PosEntryMode
import com.basepos.pos.common.domain.enums.TransactionType
import kotlinx.parcelize.Parcelize
import java.util.Date

/**
 * @Author: ifechukwu.udorji
 * @Date: 6/3/2024
 */
@Parcelize
data class EmvTransactionDetails(
    val appCryptogram: String? = null,
    val cryptogramInformationData: Byte = 0,
    val issuerApplicationData: String? = null,
    val unpredictableNumber: String? = null,
    val appTransactionCounter: String? = null,
    val terminalVerificationResults: String? = null,
    val transactionDate: String? = Date().toString(),
    val transactionType: TransactionType? = null,
    val applicationInterchangeProfile: String? = null,
    val cvmResult: String? = null,
    val dedicatedFileName: String? = null,
    val chipSerialNo: String? = null,
    val amount: Double = 0.00,
    val cashbackAmount: Double = 0.00,
    val transactionTime: String? = null,
    val ksn: String? = null,
    val offlinePwdCount: Int = 0,
    val accountType: AccountType? = null,
    val cardInfo: CardInfo? = null,
    val terminalInfo: TerminalInfo? = null
) : Parcelable

@Parcelize
data class CardInfo(
    val aid: String? = null,
    val cardHolderName: String? = null,
    val cardLabel: String? = null,
    val cardSlotType: CardSlotType? = null,
    val expiry: String? = null,
    val iccData: String? = null,
    val isOnlinePin: Boolean = false,
    val pan: String? = null,
    val pinData: String? = null,
    val sequenceNumber: String? = null,
    val track2: String? = null,
    val serviceCode: String? = null,
    val acquirerInstitutionId: String? = null,
    val pinVerificationValue: String? = null,
    val cardVerificationValue: String? = null,
): Parcelable

enum class CardSlotType {
    ICC,
    RF,
    SWIPE
}

@Parcelize
data class TerminalInfo(
    val countryCode: String? = null,
    val currencyCode: String? = null,
    val mcc: String? = null,
    val merchantId: String? = null,
    val merchantNameAndLocation: String? = null,
    val posEntryMode: PosEntryMode? = null,
    val posConditionCode: String? = null,
    val posPinCaptureCode: String? = null,
    val terminalCapability: String? = null,
    val additionalTerminalCapabilities: String? = null,
    val terminalId: String? = null,
    val terminalSerialNumber: String? = null,
    val terminalType: String? = null
): Parcelable

