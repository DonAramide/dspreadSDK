package com.basepos.pos.common.domain.models

/**
 * @Author: ifechukwu.udorji
 * @Date: 7/3/2024
 */
sealed class LocationResult {
    data class NoPermission(val message: String): LocationResult()
    data class NotEnabled(val message: String): LocationResult()
    data object Success: LocationResult()
}
