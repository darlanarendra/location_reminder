package com.udacity.project4.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import com.google.android.gms.maps.model.LatLng
import org.koin.core.context.GlobalContext
import java.util.concurrent.Executors

fun Location.toLatLng() = LatLng(latitude, longitude)

object LocationUtils {
    private const val PROVIDER = LocationManager.GPS_PROVIDER

    private val locationPermissions =
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        } else {
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        }

    private val requestExecutor by lazy { Executors.newSingleThreadExecutor() }

    private val locationManager: LocationManager?
        get() =
            GlobalContext.getOrNull()
                ?.koin
                ?.get<Application>()
                ?.getSystemService(Context.LOCATION_SERVICE) as? LocationManager

    @SuppressLint("MissingPermission")
    fun requestSingleUpdate(block: (Location) -> Unit) {
        @RequiresApi(Build.VERSION_CODES.R)
        @SuppressLint("NewApi")
        fun doRequest() {
            locationManager?.getLastKnownLocation(PROVIDER)?.let {
                block(it)
                return
            }

            locationManager?.getCurrentLocation(PROVIDER, null, requestExecutor) {
                Handler(Looper.getMainLooper()).post { block(it ?: return@post) }
            }
        }

        if (!hasLocationPermissions()) {
            PermissionManager.requestPermissions(*locationPermissions) {
                if (it.areAllGranted) {
                    doRequest()
                }
            }
        } else {
            doRequest()
        }
    }

    fun hasLocationPermissions(): Boolean =
        PermissionManager.arePermissionsGranted(*locationPermissions)

    fun requestPermissions(handler: (PermissionsResultEvent) -> Unit) =
        PermissionManager.requestPermissions(*locationPermissions, handler = handler)
}