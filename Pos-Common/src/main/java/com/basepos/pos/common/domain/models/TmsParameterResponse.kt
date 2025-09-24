package com.basepos.pos.common.domain.models

import androidx.annotation.Keep

@Keep
data class TmsParameterResponse(
    val terminalID: String? = null,
    val key1: String? = null,
    val key2: String?,
    val ipAddress: String? = null,
    val portNumber: String? = null,
    val bankLogo: String? = null,
    val bankName: String? = null,
    val footerMessage: String? = null,
    val location: String? = null,
    val merchantID: String? = null,
    val merchantName: String? = null,
    val enableSsl: Boolean = false,
    val emvAIDList:List<Aid> = emptyList(),
    val emvCapkList: List<CapK> = emptyList()
)

@Keep
data class Aid(
    val aid: String,
    val appVerNum: String,
    val asi: Int,
    val contactlessCvmLimit: Int,
    val contactlessFloorLimit: Int,
    val contactlessTransLimit: Int,
    val ddol: String,
    val floorLimit: Int,
    val id: Int,
    val maxTargetPercent: Int,
    val name: String,
    val onlinePinCap: Int,
    val tacDefault: String,
    val tacDenial: String,
    val tacOnline: String,
    val targetPercent: Int,
    val threshold: Int,
    val transLimit: Int
)

@Keep
data class CapK(
    val arithIndex: Int,
    val checksum: String,
    val expiredWhen: String,
    val exponent: Int,
    val hashIndex: Int,
    val id: Int,
    val keyIndex: Int,
    val modulus: String,
    val name: String,
    val rid: String
)