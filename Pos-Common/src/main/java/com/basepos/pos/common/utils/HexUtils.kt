package com.basepos.pos.common.utils

import java.util.Locale

object HexUtils {

    fun hexStringToByteArray(str: String): ByteArray? {
        val len = str.length
        val data = ByteArray(len / 2)

        var i = 0
        while (i < len) {
            data[i / 2] =
                ((Character.digit(str[i], 16) shl 4) + Character.digit(str[i + 1], 16)).toByte()
            i += 2
        }

        return data
    }

    fun hexToAscii(hex: String): String {
        val output = StringBuilder()
        var i = 0

        while (i < hex.length) {
            val str = hex.substring(i, i + 2)
            output.append(str.toInt(16).toChar())
            i += 2
        }

        return output.toString()
    }

    fun hexStringToByte(hex: String?): ByteArray? {
        if (hex.isNullOrEmpty()) {
            return null
        }
        val len = (hex.length / 2)
        val result = ByteArray(len)
        val achar = hex.uppercase(Locale.getDefault()).toCharArray()
        for (i in 0 until len) {
            val pos = i * 2
            result[i] =
                (toByte(achar[pos]).toInt() shl 4 or toByte(achar[pos + 1]).toInt()).toByte()
        }
        return result
    }

    /**
     * Convert byte array to hexadecimal string
     */
    fun byteArrayToHexString(bytes: ByteArray): String {
        val hexChars = "0123456789ABCDEF"
        val result = StringBuilder(bytes.size * 2)
        for (byte in bytes) {
            val i = byte.toInt() and 0xFF
            result.append(hexChars[i shr 4])
            result.append(hexChars[i and 0x0F])
        }
        return result.toString()
    }

    fun toByte(c: Char): Byte {
        val b = "0123456789ABCDEF".indexOf(c).toByte()
        return b
    }
}