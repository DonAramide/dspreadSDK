package com.basepos.pos.common.data.repository

import android.content.SharedPreferences
import com.basepos.pos.common.domain.enums.CurrencyCode
import com.basepos.pos.common.domain.models.HostParameters
import com.basepos.pos.common.domain.models.TmsParameterResponse
import com.basepos.pos.common.domain.repository.SessionManager
import com.basepos.pos.common.utils.Constants
import com.google.gson.Gson
import javax.inject.Inject
import androidx.core.content.edit

/**
 * @Author: ifechukwu.udorji
 * @Date: 7/3/2024
 */
class SessionManagerImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences
): SessionManager {
    override fun saveHostParameters(hostParameters: HostParameters) {
        val json = Gson().toJson(hostParameters)
        sharedPreferences.edit() { putString(Constants.PREF_TERMINAL_INFO, json) }
    }

    override fun getHostParameters(): HostParameters? {
        val json = sharedPreferences.getString(Constants.PREF_TERMINAL_INFO, null)
        return Gson().fromJson(json, HostParameters::class.java)
    }

    override fun saveLatitude(latitude: String?) {
        sharedPreferences.edit() { putString(Constants.PREF_LATITUDE, latitude) }
    }

    override fun getLatitude(): String? {
        return sharedPreferences.getString(Constants.PREF_LATITUDE, null)
    }

    override fun saveLongitude(longitude: String?) {
        sharedPreferences.edit() { putString(Constants.PREF_LONGITUDE, longitude) }
    }

    override fun getLongitude(): String? {
        return sharedPreferences.getString(Constants.PREF_LONGITUDE, null)
    }

    override fun getCurrency(): CurrencyCode {
        val terminalParameters = getHostParameters()
        return if (terminalParameters?.currencyCode != null) {
            CurrencyCode.fromCode(terminalParameters.currencyCode!!)
        } else {
            CurrencyCode.NIGERIA
        }
    }

    override fun saveTerminalSerialNo(serialNo: String) {
        sharedPreferences.edit() { putString(Constants.PREF_TERMINAL_SERIAL_NUMBER, serialNo) }
    }

    override fun getTerminalSerialNo(): String {
        return sharedPreferences.getString(Constants.PREF_TERMINAL_SERIAL_NUMBER, null) ?: "N/A"
    }

    override fun saveTmsParameter(tmsParameterResponse: TmsParameterResponse) {
        val json = Gson().toJson(tmsParameterResponse)
        sharedPreferences.edit() { putString(Constants.PREF_TMS_PARAMETER, json) }
    }

    override fun getTmsParameter(): TmsParameterResponse? {
        val json = sharedPreferences.getString(Constants.PREF_TMS_PARAMETER, null)
        return Gson().fromJson(json, TmsParameterResponse::class.java)
    }

    override fun saveCTMK(cTMK: String?) {
        sharedPreferences.edit() { putString(Constants.PREF_CTMK, cTMK) }
    }

    override fun getCTMK(): String? {
        return sharedPreferences.getString(Constants.PREF_CTMK, null)
    }
}