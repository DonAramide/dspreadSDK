package com.basepos.pos.host.iso.data.remote.rest.dto.request

import com.google.gson.annotations.SerializedName

/**
 * @Author: ifechukwu.udorji
 * @Date: 7/6/2024
 */
data class ClearMasterKeyRequest(
    @SerializedName("MasterKey")
    val masterKey: String
)
