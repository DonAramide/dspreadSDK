package com.basepos.pos.common.data.remote.rest.network

import com.google.gson.Gson
import com.basepos.pos.common.data.remote.rest.dto.response.ErrorResponse
import com.basepos.pos.common.data.remote.rest.network.exceptions.ClientException
import com.basepos.pos.common.data.remote.rest.network.exceptions.ServerException
import retrofit2.Response
import java.net.HttpURLConnection

/**
 * @Author: ifechukwu.udorji
 * @Date: 7/6/2024
 */

inline fun <T, R> Response<T>.networkResult(
    mapper: (T) -> R,
    errorMapper: (Int, String) -> NetworkResult<R>?
): NetworkResult<R> {
    return when {
        isSuccessful -> {
            val referenceResponse =
                body()?.let(mapper)
            if (referenceResponse != null) {
                NetworkResult.success(referenceResponse)
            } else NetworkResult.error("Invalid response", null)
        }

        else -> {
            val errorResponse = errorBody()?.string()
            val throwable = if (code() >= 500) ServerException(
                errorResponse ?: "Error occurred",
                code()
            ) else null
            errorResponse?.let { errorMapper(code(), it) }
                ?: NetworkResult.error("Error occurred", throwable)
        }
    }
}

fun <T> Gson.networkError(httpCode: Int, errorResponse: String): NetworkResult<T>? {
    return try {
        val errorJson = Gson().fromJson(errorResponse, ErrorResponse::class.java)
        errorJson
            ?.let { response ->
                val throwable = when {
                    httpCode >= 500 -> ServerException(response.responseMessage ?: errorResponse, httpCode)
                    httpCode >= 400 -> ClientException(response.responseMessage, httpCode)
                    else -> null
                }
                NetworkResult.error<T>("${response.responseMessage}", throwable)
            }
    } catch (e: Exception) {
        val errorMessage = when (httpCode) {
            HttpURLConnection.HTTP_GATEWAY_TIMEOUT -> "Gateway timeout"
            HttpURLConnection.HTTP_INTERNAL_ERROR -> "Internal server error"
            HttpURLConnection.HTTP_UNAVAILABLE -> "Service unavailable"
            else -> null
        }
        val throwable =
            if (httpCode >= 500) ServerException(errorMessage ?: "Server error", httpCode) else null
        NetworkResult.error(errorMessage ?: errorResponse, throwable)
    }
}