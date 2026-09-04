package com.mineinspect.app.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

data class LocationFix(
    val latitude: Double,
    val longitude: Double,
    val accuracyMeters: Float,
    val capturedAt: Long
)

/**
 * Real GPS acquisition (plan §7). Callers are responsible for holding
 * ACCESS_FINE_LOCATION before calling either method — this class doesn't request
 * permissions itself, only reads location once it's granted.
 */
@Singleton
class LocationRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val client: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    /** One-shot fix, used by the GPS gate check. */
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): LocationFix? = suspendCancellableCoroutine { cont ->
        val request = CurrentLocationRequest.Builder()
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build()
        client.getCurrentLocation(request, null)
            .addOnSuccessListener { location ->
                val fix = location?.let {
                    LocationFix(
                        latitude = it.latitude,
                        longitude = it.longitude,
                        accuracyMeters = it.accuracy,
                        capturedAt = System.currentTimeMillis()
                    )
                }
                if (cont.isActive) cont.resume(fix)
            }
            .addOnFailureListener {
                if (cont.isActive) cont.resume(null)
            }
    }

    /** Continuous breadcrumb fixes (plan §7, wired in Phase 4's ActiveTrackingScreen). */
    @SuppressLint("MissingPermission")
    fun observeLocationUpdates(intervalMillis: Long): Flow<LocationFix> = callbackFlow {
        val request = LocationRequest.Builder(intervalMillis)
            .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
            .build()
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    trySend(
                        LocationFix(
                            latitude = location.latitude,
                            longitude = location.longitude,
                            accuracyMeters = location.accuracy,
                            capturedAt = System.currentTimeMillis()
                        )
                    )
                }
            }
        }
        client.requestLocationUpdates(request, callback, Looper.getMainLooper())
        awaitClose { client.removeLocationUpdates(callback) }
    }
}
