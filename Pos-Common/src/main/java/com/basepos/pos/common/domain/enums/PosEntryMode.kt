package com.basepos.pos.common.domain.enums

/**
 * @Author: ifechukwu.udorji
 * @Date: 12/24/2024
 */
enum class PosEntryMode(val data: String) {
    ICC("051"),
    CONTACTLESS_ICC("071"),
    MAG("901"),
    CONTACTLESS_MAG("911")
}