package com.example.android_meteo_app.data

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.example.android_meteo_app.domain.LocationTracker
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.suspendCancellableCoroutine
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.coroutines.resume
import android.location.Geocoder
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocationTrackerImpl @Inject constructor(
    private val locationClient: FusedLocationProviderClient,
    @ApplicationContext private val context: Context
): LocationTracker {

    override suspend fun getCurrentLocation(): Location? {
        val hasAccessFineLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasAccessCoarseLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if(!hasAccessCoarseLocationPermission || !hasAccessFineLocationPermission || !isGpsEnabled) {
            return null
        }

        return suspendCancellableCoroutine { cont ->
            locationClient.lastLocation.apply {
                if(isComplete) {
                    if(isSuccessful) {
                        cont.resume(result)
                    } else {
                        cont.resume(null)
                    }
                    return@suspendCancellableCoroutine
                }
                addOnSuccessListener {
                    cont.resume(it)
                }
                addOnFailureListener {
                    cont.resume(null)
                }
                addOnCanceledListener {
                    cont.cancel()
                }
            }
        }
    }

    override suspend fun getCityName(latitude: Double, longitude: Double): String? {
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    addresses[0].locality // locality usually gives the city name
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}
