package org.example.project.location

import android.annotation.SuppressLint
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await
import org.example.project.MyApp

@SuppressLint("MissingPermission") // assume permissions are already granted
actual suspend fun getLocation(): Location {
    val client = LocationServices
        .getFusedLocationProviderClient(MyApp.ctx)

    client.lastLocation.await()?.let { last ->
        return Location(last.latitude, last.longitude)
    }

    val cts = CancellationTokenSource()
    val fresh = client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token).await()
        ?: throw IllegalStateException("Location unavailable")

    return Location(fresh.latitude, fresh.longitude)
}