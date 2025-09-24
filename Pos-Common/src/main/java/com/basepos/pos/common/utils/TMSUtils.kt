package com.basepos.pos.common.utils

import com.basepos.pos.common.domain.models.HostParameters
import com.basepos.pos.common.domain.models.TmsParameterResponse
import com.basepos.pos.common.utils.cryptographyUtils.AESUtils

/**
 * @Author: ifechukwu.udorji
 * @Date: 12/5/2024
 */
object TMSUtils {
    fun mapTmsParamToHostParam(tmsParameterResponse: TmsParameterResponse): HostParameters {
        return HostParameters(
            terminalId = tmsParameterResponse.terminalID,
            serverIP = tmsParameterResponse.ipAddress,
            port = tmsParameterResponse.portNumber?.toInt() ?: 0,
            componentKey =  "66D4AF3321D8564E9F6F35411755E730",
            enableSSL = tmsParameterResponse.enableSsl,
        )
    }
}
//componentKey = AESUtils.decryptHexFormat(tmsParameterResponse.key1 + tmsParameterResponse.key2),
//componentKey =  "66D4AF3321D8564E9F6F35411755E730",