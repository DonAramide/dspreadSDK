package com.basepos.pos.common.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.BatteryManager
import android.telephony.TelephonyManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.basepos.pos.common.R
import com.basepos.pos.common.domain.models.BaseStation
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

/**
 * @Author: ifechukwu.udorji
 * @Date: 6/3/2024
 */
fun Context.showAlertDialog(
    title: String,
    message: String? = null,
    positiveTitle: String? = "Okay",
    negativeTitle: String? = "Cancel",
    showNegativeButton: Boolean = true,
    onPositiveButtonPressed: (Boolean) -> Unit,
) {
    val builder = AlertDialog.Builder(this)
        .setTitle(title)
        .setCancelable(false)
        .setPositiveButton(
            positiveTitle
        ) { dialog, _ ->
            run {
                dialog.dismiss()
                onPositiveButtonPressed(true)
            }
        }

    if (!message.isNullOrEmpty()) {
        builder.setMessage(message)
    }
    if (showNegativeButton) {
        builder.setNegativeButton(
            negativeTitle
        ) { dialog, _ ->
            run {
                dialog.dismiss()
                onPositiveButtonPressed(false)
            }
        }
    }
    builder.create()
    builder.show()
}

fun Context.showSingleChoiceDialog(
    title: String,
    items: Array<String>,
    selectedItemIndex: Int = 0,
    onItemSelected: (index: Int, item: String) -> Unit,
    onCancelled: () -> Unit = {}
) {
    var selectedIndex = selectedItemIndex

    AlertDialog.Builder(this)
        .setTitle(title)
        .setSingleChoiceItems(items, selectedItemIndex) { _, index ->
            selectedIndex = index
        }
        .setPositiveButton("OK") { dialog, _ ->
            onItemSelected(selectedIndex, items[selectedIndex])
            dialog.dismiss()
        }
        .setNegativeButton("Cancel") { dialog, _ ->
            onCancelled()
            dialog.dismiss()
        }
        .show()
}

fun Context.hasLocationPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
}

fun Context.batteryLevel(): String {
    val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
    val batteryStatus = registerReceiver(null, intentFilter)

    batteryStatus?.let { intent ->
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        return (level * 100 / scale).toString()
    }
    return "0"
}

fun Context.cellInfo(): BaseStation? {
    val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    if (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        Timber.i("Location Permission not granted, cannot get cellInfo")
        return null
    }

    val allCellInfo = telephonyManager.allCellInfo
    return if (allCellInfo.isNotEmpty()) {
        BaseStation.bindCellInfoData(allCellInfo[0])
    } else null
}

fun Context.getConnectionMode(): String {
    val connectivityManager =
        this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = connectivityManager.activeNetworkInfo

    if (activeNetwork != null && activeNetwork.isConnected) {
        if (activeNetwork.type == ConnectivityManager.TYPE_WIFI) {
            return "WIFI"
        } else if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE) {
            return getSIMName(this) ?: "SIM"
        }
    }
    return "No Connection"
}

private fun getSIMName(context: Context): String? {
    val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    return telephonyManager.simOperatorName
}

fun View.hideKeyboard() {
    val manager = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    manager?.hideSoftInputFromWindow(this.windowToken, 0)
}

fun View.showWarningSnackBar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT)
        .setBackgroundTint(resources.getColor(R.color.color_warning))
        .show()
}

fun View.showSuccessSnackBar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT)
        .setBackgroundTint(resources.getColor(R.color.color_success))
        .setTextColor(resources.getColor(R.color.white))
        .show()
}