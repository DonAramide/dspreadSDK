package com.basepos.pos.common.data.remote.rest.dto.response

import androidx.annotation.Keep

/**
 * @Author: ifechukwu.udorji
 * @Date: 7/6/2024
 */
@Keep
data class ErrorResponse(
    val responseMessage: String? = null,
    val responseCode: String? = null
)
