package com.basepos.pos.common.domain.repository

import com.basepos.pos.common.domain.enums.CurrencyCode
import com.basepos.pos.common.domain.models.HostParameters
import com.basepos.pos.common.domain.models.TmsParameterResponse

/**
 * @Author: ifechukwu.udorji
 * @Date: 7/3/2024
 */

interface SessionManager {
    fun saveHostParameters(hostParameters: HostParameters)
    fun getHostParameters(): HostParameters?
    fun saveLatitude(latitude: String?)
    fun getLatitude(): String?
    fun saveLongitude(longitude: String?)
    fun getLongitude(): String?
    fun getCurrency(): CurrencyCode
    fun saveTerminalSerialNo(serialNo: String)
    fun getTerminalSerialNo(): String
    fun saveTmsParameter(tmsParameterResponse: TmsParameterResponse)
    fun getTmsParameter(): TmsParameterResponse?
    fun saveCTMK(cTMK: String?)
    fun getCTMK(): String?
}