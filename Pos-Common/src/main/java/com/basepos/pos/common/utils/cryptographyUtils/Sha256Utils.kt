package com.basepos.pos.common.utils.cryptographyUtils

import timber.log.Timber
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * @Author: ifechukwu.udorji
 * @Date: 6/3/2024
 */
object Sha256Utils {
    private const val TAG = "Sha256Utils"

    fun performSha256Hash(input: ByteArray?, seed: ByteArray?): ByteArray? {
        val bb: Byte = 0x00

        //perform hash
        val md: MessageDigest
        var digest: ByteArray? = byteArrayOf(0x00) //replace with bb

        try {
            md = MessageDigest.getInstance("SHA-256")
            md.reset()
            md.update(seed)
            md.update(input)
            digest = md.digest()
        } catch (ex: NoSuchAlgorithmException) {
            Timber.e("performSha256Hash: Error (Sha256 ops) - " + ex.message.toString())
        }

        return digest
    }

    fun performSha256Hash(data: String): String? {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hashBytes = data.toByteArray(StandardCharsets.UTF_8)
            val messageDigest = digest.digest(hashBytes)
            val sb = StringBuffer()
            for (i in messageDigest.indices) {
                var h = Integer.toHexString(0xFF and messageDigest[i].toInt())
                while (h.length < 2) h = "0$h"
                sb.append(h)
            }
            sb.toString()
        } catch (ex: Exception) {
            ex.printStackTrace()
            null
        }
    }
}