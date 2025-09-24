package com.basepos.pos.common.utils

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.media.MediaPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.reflect.Method
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Random
import java.util.StringTokenizer
import java.util.Vector


/**
 * @Author: ifechukwu.udorji
 * @Date: 6/3/2024
 */
object UtilMethods {
    fun getSerialNumber(): String? {
        var serial: String? = null
        try {
            val c = Class.forName("android.os.SystemProperties")
            val get: Method = c.getMethod("get", String::class.java)
            serial = get.invoke(c, "ro.serialno") as String?
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return serial
    }

    fun generateRandomNo(len: Int): String {
        val finalString: String
        var x: Int
        val stringChars = CharArray(len)
        for (i in 0 until len) {
            val random = SecureRandom()
            x = random.nextInt(9)
            stringChars[i] = x.toString().toCharArray()[0]
        }
        finalString = String(stringChars)
        return finalString.trim { it <= ' ' }
    }

    fun generateRandomTextNo(l: Int): String {
        val chars = "abcdefghijklmnopqrstuvwxyz0123456789"
        val stringChars = CharArray(l)
        var x = 0 //stringChars.length;
        for (i in 0 until l)  {
            val random = Random()
            x = random.nextInt(chars.length)
            stringChars[i] = chars[x]
        }
        return String(stringChars) // stringChars.toString();
    }

    fun formatDateToString(date: Date): String {
        val dateFormat = "MMddHHmmss" //""MMdd-yyyy hh:mm:ss";
        val simpleDateFormat = SimpleDateFormat(dateFormat)
        return simpleDateFormat.format(date)
    }

    fun tokenize(input: String?, delim: String): Array<String?>? {
        val v: Vector<*> = Vector<Any?>()
        //System.out.println("...TOKENIZE::" + input + "    " + delim);
        val t: StringTokenizer = if (delim == "default") {
            StringTokenizer(input)
        } else {
            StringTokenizer(input, delim)
        }
        while (t.hasMoreTokens()) {
            v.addElement(t.nextToken() as Nothing?)
        }
        val cmd = arrayOfNulls<String>(v.size)
        for (i in cmd.indices) {
            cmd[i] = v.elementAt(i) as String
        }
        return cmd
    }

    fun maskCardPAN(cardPAN: String): String {
        return String.format(
            "%s************%s",
            cardPAN.substring(0, 4),
            cardPAN.substring(cardPAN.length - 3)
        )
    }

    fun formatAmount(amount: Double): String {
        return String.format("%.2f", amount)
    }

    fun formatDateToCustomFormat(dateInMillis: Long): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy hh:mm:ss a", Locale.getDefault())
        return sdf.format(Date(dateInMillis))
    }

    suspend fun beep(context: Context) {
        withContext(Dispatchers.IO) {
            try {
                val assetFileDescriptor = context.assets.openFd("beep.mp3")
                val mediaPlayer = MediaPlayer().apply {
                    setDataSource(
                        assetFileDescriptor.fileDescriptor,
                        assetFileDescriptor.startOffset,
                        assetFileDescriptor.length
                    )
                    prepare()
                    start()
                }

                // Optionally, release the MediaPlayer when done
                mediaPlayer.setOnCompletionListener {
                    it.release()
                }
            } catch (e: Exception) {
                // Handle exceptions (e.g., file not found)
                println("Error playing audio: ${e.message}")
            }
        }
    }

    fun getPackageInfo(context: Context): PackageInfo? {
        return try {
           context.packageManager.getPackageInfo(context.packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        }
    }
}