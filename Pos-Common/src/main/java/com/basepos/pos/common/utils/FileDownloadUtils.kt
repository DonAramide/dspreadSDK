package com.basepos.pos.common.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.basepos.pos.common.R
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import kotlin.math.roundToInt

/**
 * @Author: ifechukwu.udorji
 * @Date: 7/3/2024
 */
class FileDownloadUtils @Inject constructor(
    private val context: Context,
    private val dispatchers: CoroutineDispatcher
) {

    /**
     * Downloads an image from the given URL and saves it to the app's internal storage.
     * Params:
     * url - The URL of the image to download.
     * onComplete - A callback to be invoked when the image has been downloaded and saved.
     */
    suspend fun downloadImageFromUrl(url: String, onComplete: () -> Unit) =
        withContext(dispatchers) {
            val result = try {
                val fileUrl = URL(url)
                val connection = fileUrl.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connect()

                if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                    Timber.e("Failed to download image: ${connection.responseMessage}")
                    onComplete()
                    return@withContext
                }

                saveImageToAppStorage(connection.inputStream, onComplete)
            } catch (e: Exception) {
                Timber.e("Failed to download image: ${e.message}")
                onComplete()
            }
        }

    /**
     * Saves an image to the app's internal storage.
     * Params:
     * inputStream - The input stream containing the image data.
     * onComplete - A callback to be invoked when the image has been saved.
     */
    private fun saveImageToAppStorage(inputStream: InputStream, onComplete: () -> Unit) {
        try {
            val logoDir = File(context.filesDir, LOGO_DIR)
            logoDir.mkdir()

            val file = File(logoDir, LOGO_FILE_NAME)
            val fileOutputStream = FileOutputStream(file)

            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                fileOutputStream.write(buffer, 0, bytesRead)
            }

            fileOutputStream.close()
            inputStream.close()

            onComplete()
            Timber.i("Logo saved successfully")
        } catch (ioException: IOException) {
            Timber.i(ioException)
        }
    }

    /**
     * Loads a bitmap from the given file path, scales it to fit within the maximum dimensions, and returns the scaled bitmap.
     * Params:
     * filePath - The path to the image file.
     * Returns:
     * The scaled bitmap, or null if the file path is blank or the bitmap could not be loaded.
     */
    fun getLogoBitmap(filePath: String?): Bitmap? {
        if (filePath?.isNotBlank() == true) {
            val originalBitmap = BitmapFactory.decodeFile(filePath)
            if (originalBitmap != null) {
                val originalWidth = originalBitmap.width.toDouble()
                val originalHeight = originalBitmap.height.toDouble()
                val scale = Math.min(MAX_WIDTH / originalWidth, MAX_HEIGHT / originalHeight)
                val newWidth = (originalWidth * scale).roundToInt()
                val newHeight = (originalHeight * scale).roundToInt()

                return Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)
            }
            return null
        }
        return null
    }

    fun getLogoBitmap(): Bitmap? {
        val originalBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.paylode)
        if (originalBitmap != null) {
            val originalWidth = originalBitmap.width.toDouble()
            val originalHeight = originalBitmap.height.toDouble()
            val scale = Math.min(MAX_WIDTH / originalWidth, MAX_HEIGHT / originalHeight)
            val newWidth = (originalWidth * scale).roundToInt()
            val newHeight = (originalHeight * scale).roundToInt()

            return Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)
        }
        return null
    }

    /**
     * This function would return BaseApp Default Logo
     */
    fun getLogoPath(): String {
        return "${context.filesDir}/$LOGO_DIR/$LOGO_FILE_NAME"
    }

    companion object {
        const val LOGO_DIR = "logo"
        const val LOGO_FILE_NAME = "BasePosLogo.png"
        const val MAX_WIDTH = 384.00
        const val MAX_HEIGHT = 100.00
    }
}