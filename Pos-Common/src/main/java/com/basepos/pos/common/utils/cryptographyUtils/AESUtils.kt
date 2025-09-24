package com.basepos.pos.common.utils.cryptographyUtils

import android.util.Base64
import com.basepos.pos.common.utils.HexUtils.hexStringToByteArray
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


/**
 * @Author: ifechukwu.udorji
 * @Date: 12/4/2024
 */
object AESUtils {
    private val ENCRYPTION_KEY = "7014E5C3D986AF89FD254AF132D5D565"
    private val key = ENCRYPTION_KEY.toByteArray()
    private val ENCRYPTION_IV = "E66A42798C087E60"
    private val IV = ENCRYPTION_IV.toByteArray()

    fun encrypt(plaintext: String): String {
        try {
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            val keySpec = SecretKeySpec(key, "AES")
            val ivSpec = IvParameterSpec(IV)
            cipher.init(1, keySpec, ivSpec)
            val cipherText = cipher.doFinal(plaintext.toByteArray())
            return String(Base64.encode(cipherText, 2))
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    fun decrypt(cipherText: String?): String {
        try {
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            val keySpec = SecretKeySpec(key, "AES")
            val ivSpec = IvParameterSpec(IV)
            cipher.init(2, keySpec, ivSpec)
            val decryptedText = cipher.doFinal(Base64.decode(cipherText, 2))
            return String(decryptedText)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    fun decryptHexFormat(cipherHex: String?): String {
        val decodedHex = hexStringToByteArray(cipherHex!!)
        val cipherText: String = Base64.encodeToString(decodedHex, 2)
        try {
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            val keySpec = SecretKeySpec(key, "AES")
            val ivSpec = IvParameterSpec(IV)
            cipher.init(2, keySpec, ivSpec)
            val decryptedText = cipher.doFinal(Base64.decode(cipherText, 2))
            return String(decryptedText)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}