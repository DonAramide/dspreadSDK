package com.basepos.pos.common.domain.models

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

/**
 * @Author: ifechukwu.udorji
 * @Date: 7/3/2024
 */

@Keep
@Parcelize
data class ParameterResponse(
    var merchantId: String?,
    var terminalId: String?,
    var serialNumber: String?,
    var ptsp: String?,
    var footerMessage: String?,
    var merchantName: String?,
    var bankName: String?,
    var bankLogo: String?,
    var baseAppVersion: String?,
    var location: String?,
    var merchantCategoryCode: String?,
    var currency: String?
): Parcelable

