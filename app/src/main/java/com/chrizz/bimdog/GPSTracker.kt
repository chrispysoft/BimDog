package com.chrizz.bimdog

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.Locale


class GPSTracker (private val context: Context, private val listener: GPSTrackerListener) : Service(), LocationListener {

    interface GPSTrackerListener {
        fun locationUpdated(location: Location)
        fun logMessage(message: String)
    }

    companion object {
        private const val LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION
        private const val PERMISSION_REQUEST_CODE = 0
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 10
        private const val MIN_TIME_BW_UPDATES: Long = 1000 * 3
    }


    private val locationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    fun start() {
        listener.logMessage("start")

        if (! isLocationServiceEnabled) {
            AlertDialog.Builder(context)
                    .setTitle(R.string.nolocation_title)
                    .setMessage(R.string.nolocation_message)
                    .setPositiveButton("OK") { _, _ ->
                        context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    }
                    .show()
            return
        }

        if (ActivityCompat.checkSelfPermission(context, LOCATION_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context as Activity, arrayOf(LOCATION_PERMISSION), PERMISSION_REQUEST_CODE)
            return
        }

        var location: Location? = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if (location == null) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            if (location != null) {
                listener.logMessage("init from NTW")
            }
        } else {
            listener.logMessage("init from GPS")
        }

        if (location != null) {
            checkNewLocation(location)
        } else {
            listener.logMessage("GPS and NTW is null")
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this)
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this)
    }

    fun stop() {
        listener.logMessage("stop")
        locationManager.removeUpdates(this@GPSTracker)
    }


    private val isLocationServiceEnabled: Boolean
        get() {
            var enabled = false
            try {
                val locationMode = Settings.Secure.getInt(context.contentResolver, Settings.Secure.LOCATION_MODE)
                enabled = locationMode != Settings.Secure.LOCATION_MODE_OFF
            } catch (e: Settings.SettingNotFoundException) {
                e.printStackTrace()
            }
            return enabled
        }



    private var currentLocation: Location? = null

    private fun checkNewLocation(newLocation: Location) {
        if (isBetterLocation(newLocation, currentLocation)) {
            listener.locationUpdated(newLocation)
            logLocation(newLocation, true)
        } else {
            logLocation(newLocation, false)
        }
        currentLocation = newLocation
    }

    private fun isBetterLocation(newLocation: Location, oldLocation: Location?): Boolean {
        return true

        if (oldLocation == null) {
            return true
        }

        val newLat = newLocation.latitude
        val newLon = newLocation.longitude
        val oldLat = oldLocation.latitude
        val oldLon = oldLocation.longitude
        if (newLat == oldLat && newLon == oldLon) {
            return false
        }

        if (newLocation.accuracy <= oldLocation.accuracy) {
            return true
        }
        if (newLocation.time - oldLocation.time > 10*1000) {
            return true
        }
        return false
    }

    private fun logLocation(location: Location?, showBetter: Boolean) {
        if (location != null) {
            var betterInfo = ""; if (showBetter) betterInfo = "B"
            var provider = location.provider.toUpperCase(); if (location.provider == LocationManager.NETWORK_PROVIDER) provider = "NTW"
            var delta: Float = 0f; if (currentLocation != null) { delta = currentLocation!!.distanceTo(location) }
            listener.logMessage(String.format(Locale.ENGLISH, "%s: %.6f,%.6f a=%.1f %s d=%.0f", provider, location.latitude, location.longitude, location.accuracy, betterInfo, delta))
        } else {
            listener.logMessage("location is null")
        }
    }


    override fun onLocationChanged(location: Location) {
        checkNewLocation(location)
    }
    override fun onProviderDisabled(provider: String) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
    override fun onBind(arg0: Intent): IBinder? { return null }
}