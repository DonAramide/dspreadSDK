package com.basepos.pos.host.iso.data.local.converter

import androidx.room.TypeConverter
import com.basepos.pos.common.domain.models.CardInfo
import com.basepos.pos.common.domain.models.TerminalInfo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * @Author: ifechukwu.udorji
 * @Date: 4/26/2025
 */
class Converter {
    @TypeConverter
    fun fromJsonToCardInfo(json: String): CardInfo {
        return Gson().fromJson(json, CardInfo::class.java)
    }

    @TypeConverter
    fun fromCardInfoToJson(cardInfo: CardInfo): String {
        return Gson().toJson(cardInfo)
    }

    @TypeConverter
    fun fromJsonToTerminalInfo(json: String): TerminalInfo {
        return Gson().fromJson(json, TerminalInfo::class.java)
    }

    @TypeConverter
    fun fromTerminalInfoToJson(terminalInfo: TerminalInfo): String {
        return Gson().toJson(terminalInfo)
    }
}