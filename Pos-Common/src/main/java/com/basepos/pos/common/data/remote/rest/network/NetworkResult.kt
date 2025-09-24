package com.basepos.pos.common.data.remote.rest.network

/**
 * @Author: ifechukwu.udorji
 * @Date: 7/6/2024
 */
sealed class NetworkResult<T> {
    data class Success<T>(val data: T) : NetworkResult<T>()

    data class Error<T>(val message: String?, val throwable: Throwable?) : NetworkResult<T>()

    companion object {
        fun <T> error(message: String?, throwable: Throwable? = null): NetworkResult<T> =
            Error(message, throwable)

        fun <T> success(data: T): NetworkResult<T> = Success(data)
    }
}
