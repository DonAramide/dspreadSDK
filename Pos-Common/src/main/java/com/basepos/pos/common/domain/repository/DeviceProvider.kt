package com.basepos.pos.common.domain.repository

import com.basepos.pos.common.domain.enums.DeviceType

/**
 * @Author: ifechukwu.udorji
 * @Date: 7/16/2024
 */
interface DeviceProvider {
    fun getDeviceType(): DeviceType
}