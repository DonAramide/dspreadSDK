package com.basepos.pos.common.data.remote.rest.utils

import com.basepos.pos.common.data.remote.rest.network.exceptions.ServerException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * @Author: ifechukwu.udorji
 * @Date: 7/6/2024
 */

fun Throwable.resolveErrors(): String = when (this) {
    is UnknownHostException -> "Network Error! Check your connection and try again"
    is SocketTimeoutException -> "Timeout waiting for response!"
    is ConnectException -> "Connection refused!\n\nTry again or contact support!"
    is IOException -> "Connection failed! $message"
    else -> "Request failed! $message"
}

fun Throwable.isIoException(): Boolean = this is IOException

fun Throwable.isServerOrNetworkError(): Boolean =
    this is ServerException || this is IOException || cause is ServerException || cause is IOException
