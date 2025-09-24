package com.basepos.pos.common.domain.models

import androidx.annotation.Keep

/**
 * @Author: ifechukwu.udorji
 * @Date: 7/3/2024
 */

@Keep
data class HostParameters(
    var terminalId: String? = null,
    var tmk: String? = null,
    var tsk: String? = null,
    var tpk: String? = null,
    var componentKey: String? = null,
    var zmk: String? = null,
    var merchantId: String? = null,
    var merchantName: String? = null,
    var ptsp: String? = null,
    var lastUpdateDate: Long? = null,
    var mcc: String? = null,
    var serverIP: String? = null,
    var port: Int = 0,
    var cardAcceptorId: String? = null,
    var cardAcceptorLocation: String? = null,
    var currencyCode: String? = null,
    var encryptedTmk: String? = null,
    var encryptedTsk: String? = null,
    var encryptedTpk: String? = null,
    var encryptedZmk: String? = null,
    var tmkKCV: String? = null,
    var tskKCV: String? = null,
    var tpkKCV: String? = null,
    var zmkKCV: String? = null,
    var enableSSL: Boolean = false,
)
