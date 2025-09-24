package com.basepos.pos.common.utils.cryptographyUtils

import com.basepos.pos.common.utils.HexUtils
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * @Author: ifechukwu.udorji
 * @Date: 6/3/2024
 */
class TripleDESUtils(key: ByteArray) {
    private var encrypter: Cipher? = null
    private var decrypter: Cipher? = null

    init {
        Security.addProvider(BouncyCastleProvider())

        val algorithm = "DESede"
        val keySpec = SecretKeySpec(key, algorithm)
        val bouncyCastleProvider = "BC"
        val tripleDesTransformation = "DESede/ECB/Nopadding"

        encrypter = Cipher.getInstance(tripleDesTransformation, bouncyCastleProvider)
        encrypter!!.init(Cipher.ENCRYPT_MODE, keySpec)

        decrypter = Cipher.getInstance(tripleDesTransformation, bouncyCastleProvider)
        decrypter!!.init(Cipher.DECRYPT_MODE, keySpec)
    }

    fun encode(input: ByteArray?): ByteArray? {
        return encrypter!!.doFinal(input)
    }

    fun decode(input: ByteArray?): ByteArray? {
        return decrypter!!.doFinal(input)
    }

    fun getKCV(): String {
        val encrypted = encrypter!!.doFinal(HexUtils.hexStringToByte("0000000000000000"))
        return HexUtils.byteArrayToHexString(encrypted)
    }
}