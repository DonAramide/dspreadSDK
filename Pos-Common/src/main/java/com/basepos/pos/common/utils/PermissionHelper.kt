package com.basepos.pos.common.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.basepos.pos.common.R

/**
 * @Author: ifechukwu.udorji
 * @Date: 2/1/2025
 */
class PermissionHelper(private val activity: AppCompatActivity) {

    private val context: Context = activity
    private val permissionInfoMap = mutableMapOf<String, PermissionInfo>()
    private var permissionCallback: PermissionCallback? = null
    private lateinit var multiplePermissionLauncher: ActivityResultLauncher<Array<String>>

    init {
        initializePermissionInfo()
        setupPermissionLauncher()
    }

    interface PermissionCallback {
        fun onPermissionsGranted()
        fun onPermissionsDenied(deniedPermissions: List<String>) {}
        fun onPermissionsPermanentlyDenied(permanentlyDeniedPermissions: List<String>) {}
    }

    private data class PermissionInfo(
        val permission: String,
        val rationaleMessage: String,
        val settingsMessage: String
    )

    private fun initializePermissionInfo() {
        // Storage permissions
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            addPermissionInfo(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                "Storage access is required to save files",
                "Storage permission is required. Please enable it in settings"
            )
            addPermissionInfo(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                "Storage access is required to read files",
                "Storage permission is required. Please enable it in settings"
            )
        } else {
            addPermissionInfo(
                Manifest.permission.READ_MEDIA_IMAGES,
                "Storage access is required to read files",
                "Storage permission is required. Please enable it in settings"
            )
        }

        // Location permissions
        addPermissionInfo(
            Manifest.permission.ACCESS_FINE_LOCATION,
            "Precise location is required for location-based features",
            "Location permission is required. Please enable it in settings"
        )
        addPermissionInfo(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            "Location access is required for basic features",
            "Location permission is required. Please enable it in settings"
        )

        // Camera permission
        addPermissionInfo(
            Manifest.permission.CAMERA,
            "Camera access is required to take pictures",
            "Camera permission is required. Please enable it in settings"
        )

        // Read Phone state
        addPermissionInfo(
            Manifest.permission.READ_PHONE_STATE,
            "Read phone state is required to get phone information",
            "Read phone state permission is required. Please enable it in settings"
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Notification
            addPermissionInfo(
                Manifest.permission.POST_NOTIFICATIONS,
                "Notification access is required to send notifications",
                "Notification permission is required. Please enable it in settings"
            )
        }
    }

    private fun setupPermissionLauncher() {
        multiplePermissionLauncher = activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val deniedPermissions = mutableListOf<String>()
            val permanentlyDeniedPermissions = mutableListOf<String>()

            permissions.forEach { (permission, isGranted) ->
                if (!isGranted) {
                    if (!activity.shouldShowRequestPermissionRationale(permission)) {
                        permanentlyDeniedPermissions.add(permission)
                    } else {
                        deniedPermissions.add(permission)
                    }
                }
            }

            when {
                deniedPermissions.isEmpty() && permanentlyDeniedPermissions.isEmpty() -> {
                    permissionCallback?.onPermissionsGranted()
                }
                permanentlyDeniedPermissions.isNotEmpty() -> {
                    permissionCallback?.onPermissionsPermanentlyDenied(permanentlyDeniedPermissions)
                    showSettingsDialog(permanentlyDeniedPermissions)
                }
                else -> {
                    permissionCallback?.onPermissionsDenied(deniedPermissions)
                    showRationaleDialog(deniedPermissions)
                }
            }
        }
    }

    fun addPermissionInfo(permission: String, rationaleMessage: String, settingsMessage: String) {
        permissionInfoMap[permission] = PermissionInfo(permission, rationaleMessage, settingsMessage)
    }

    fun requestPermissions(permissions: Array<String>, callback: PermissionCallback) {
        this.permissionCallback = callback

        if (permissions.isEmpty()) {
            callback.onPermissionsGranted()
            return
        }

        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isEmpty()) {
            callback.onPermissionsGranted()
            return
        }

        multiplePermissionLauncher.launch(permissionsToRequest.toTypedArray())
    }

    private fun showRationaleDialog(permissions: List<String>) {
        val message = buildString {
            append("The following permissions are required:\n\n")
            permissions.forEach { permission ->
                permissionInfoMap[permission]?.let { info ->
                    append("• ${info.rationaleMessage}\n")
                }
            }
        }

        AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.permissions_required))
            .setMessage(message)
            .setPositiveButton(context.getString(R.string.grant)) { _, _ ->
                multiplePermissionLauncher.launch(permissions.toTypedArray())
            }
            .setNegativeButton(context.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    private fun showSettingsDialog(permissions: List<String>) {
        val message = buildString {
            append("To use these features, enable the following permissions in settings:\n\n")
            permissions.forEach { permission ->
                permissionInfoMap[permission]?.let { info ->
                    append("• ${info.settingsMessage}\n")
                }
            }
        }

        AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.permissions_required))
            .setMessage(message)
            .setPositiveButton("Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
            }
            .setNegativeButton(context.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    fun hasPermissions(vararg permissions: String): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun permissions(): Array<String> {
        return permissionInfoMap.keys.toTypedArray()
    }

    fun storagePermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        } else {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        }
    }

    fun cameraStoragePermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        } else {
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES)
        }
    }
}