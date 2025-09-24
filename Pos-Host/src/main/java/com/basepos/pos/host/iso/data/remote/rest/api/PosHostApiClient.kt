package com.basepos.pos.host.iso.data.remote.rest.api

import com.basepos.pos.host.iso.data.remote.rest.dto.request.ClearMasterKeyRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * @Author: ifechukwu.udorji
 * @Date: 7/6/2024
 */
interface PosHostApiClient {
    @POST("api/GetPlainMasterKey")
    suspend fun getClearMasterKey(
        @Header("Authorization") authorization: String,
        @Body masterKey: ClearMasterKeyRequest
    ): Response<String>
}