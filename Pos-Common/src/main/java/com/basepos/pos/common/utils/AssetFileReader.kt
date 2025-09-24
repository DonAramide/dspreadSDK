package com.basepos.pos.common.utils

/**
 * @Author: ifechukwu.udorji
 * @Date: 12/24/2024
 */
import android.content.Context
import android.content.ContextWrapper
import com.basepos.pos.common.domain.models.TmsParameterResponse
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStreamReader

object AssetFileReader {

    fun readFileFromAssets(context: Context, fileName: String): String {
        val assetManager = context.assets
        val stringBuilder = StringBuilder()

        try {
            val inputStream = assetManager.open(fileName)
            val reader = BufferedReader(InputStreamReader(inputStream))

            var line: String? = reader.readLine()
            while (line != null) {
                stringBuilder.append(line).append("\n")
                line = reader.readLine()
            }

            reader.close()
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return stringBuilder.toString()
    }

    fun readAssetsLine(context: Context?, fileName: String): ByteArray? {
        val buffer = ByteArrayOutputStream()
        try {
            val contextWrapper = ContextWrapper(context)
            val assetManager = contextWrapper.assets
            val inputStream = assetManager.open(fileName)
            val data = ByteArray(512)
            var current = 0
            while ((inputStream.read(data, 0, data.size).also { current = it }) != -1) {
                buffer.write(data, 0, current)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        return buffer.toByteArray()
    }

    fun readParameterJsonFile(context: Context): TmsParameterResponse {
        val jsonString = readFileFromAssets(context, "parameter.json")
        return Gson().fromJson(jsonString, TmsParameterResponse::class.java)
    }
}
