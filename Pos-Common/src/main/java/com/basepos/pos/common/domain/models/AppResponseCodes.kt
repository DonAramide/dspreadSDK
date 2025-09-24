package com.basepos.pos.common.domain.models

enum class AppResponseCodes(val code: String) {
    SUCCESS("00"),
    FAILED("02"),
    CANCEL("03"),
    INVALID_FORMAT("04"),
    WRONG_PARAMETER("05"),
    TIMEOUT("06"),
}