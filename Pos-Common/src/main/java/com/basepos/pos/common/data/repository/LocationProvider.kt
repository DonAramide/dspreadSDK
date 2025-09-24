package com.basepos.pos.common.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import com.basepos.pos.common.domain.models.LocationResult
import com.basepos.pos.common.domain.repository.SessionManager
import com.basepos.pos.common.utils.hasLocationPermission
import timber.log.Timber
import javax.inject.Inject

/**
 * @Author: ifechukwu.udorji
 * @Date: 7/3/2024
 */
class LocationProvider @Inject constructor(
    private val context: Context,
    private val sessionManager: SessionManager
) : LocationListener {

    @SuppressLint("MissingPermission")
    fun getLocation(onLocationResult: ((LocationResult) -> Unit)) {
        if (!context.hasLocationPermission()) {
            Timber.i("Missing location permission")
            onLocationResult(LocationResult.NoPermission("Missing location permission"))
        }

        var location: Location? = null
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!isGpsEnabled && !isNetworkEnabled) {
            onLocationResult(LocationResult.NotEnabled("Missing location permission"))
        } else {
            // First get location from Network Provider
            if (isNetworkEnabled) {
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(),
                    this
                )
                Timber.d("Network", "Network")
                location =
                    locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (location != null) {
                    sessionManager.saveLatitude(location.latitude.toString())
                    sessionManager.saveLongitude(location.longitude.toString())

                    Timber.i("LOCATION ::::: ${sessionManager.getLatitude()},\n${sessionManager.getLongitude()}")
                }
            }
            // if GPS Enabled get lat/long using GPS Services
            if (isGpsEnabled) {
                if (location == null) {
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(),
                        this
                    )
                    Timber.d("GPS Enabled", "GPS Enabled")
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (location != null) {
                        sessionManager.saveLatitude(location.latitude.toString())
                        sessionManager.saveLongitude(location.longitude.toString())

                        Timber.i("LOCATION ::::: ${sessionManager.getLatitude()},\n${sessionManager.getLongitude()}")
                    }
                }
            }

            onLocationResult(LocationResult.Success)
        }
    }

    override fun onLocationChanged(location: Location) {
    }

    @Deprecated("Deprecated in Java")
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }

    override fun onProviderEnabled(provider: String) {
        super.onProviderEnabled(provider)
    }

    override fun onProviderDisabled(provider: String) {
        super.onProviderDisabled(provider)
    }

    companion object {
        // The minimum distance to change Updates in meters
        const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 10 // 10 meters

        // The minimum time between updates in milliseconds
        const val MIN_TIME_BW_UPDATES = (1000 * 60 * 1).toLong() // 1 minute
    }
}