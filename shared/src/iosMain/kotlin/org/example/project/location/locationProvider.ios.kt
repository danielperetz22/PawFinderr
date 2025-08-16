@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package org.example.project.location

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.cinterop.useContents
import platform.CoreLocation.*
import platform.Foundation.NSError
import platform.darwin.NSObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


actual suspend fun getLocation(): Location {
    if (!CLLocationManager.locationServicesEnabled()) {
        throw IllegalStateException("Location services are disabled")
    }

    return suspendCancellableCoroutine { cont ->
        val manager = CLLocationManager()
        var finished = false

        val delegate = object : NSObject(), CLLocationManagerDelegateProtocol {

            override fun locationManagerDidChangeAuthorization(manager: CLLocationManager) {
                when (manager.authorizationStatus) {
                    kCLAuthorizationStatusAuthorizedWhenInUse,
                    kCLAuthorizationStatusAuthorizedAlways -> {
                        manager.requestLocation()
                    }
                    kCLAuthorizationStatusDenied,
                    kCLAuthorizationStatusRestricted -> {
                        if (!finished) {
                            finished = true
                            cont.resumeWithException(
                                IllegalStateException("Location permission denied")
                            )
                        }
                    }
                    kCLAuthorizationStatusNotDetermined -> { /* waiting */ }
                    else -> {}
                }
            }

            @Suppress("OVERRIDE_DEPRECATION")
            override fun locationManager(
                manager: CLLocationManager,
                didChangeAuthorizationStatus: CLAuthorizationStatus
            ) {
                when (didChangeAuthorizationStatus) {
                    kCLAuthorizationStatusAuthorizedWhenInUse,
                    kCLAuthorizationStatusAuthorizedAlways -> manager.requestLocation()
                    kCLAuthorizationStatusDenied,
                    kCLAuthorizationStatusRestricted -> {
                        if (!finished) {
                            finished = true
                            cont.resumeWithException(
                                IllegalStateException("Location permission denied")
                            )
                        }
                    }
                    else -> {}
                }
            }

            override fun locationManager(
                manager: CLLocationManager,
                didUpdateLocations: List<*>
            ) {
                if (finished) return
                val last = (didUpdateLocations.lastOrNull() as? CLLocation) ?: return
                val location = last.coordinate().useContents {
                    Location(latitude, longitude)
                }
                finished = true
                manager.delegate = null
                cont.resume(location)
            }

            override fun locationManager(
                manager: CLLocationManager,
                didFailWithError: NSError
            ) {
                if (finished) return
                finished = true
                manager.delegate = null
                cont.resumeWithException(
                    IllegalStateException(didFailWithError.localizedDescription ?: "Location error")
                )
            }
        }

        manager.delegate = delegate
        manager.desiredAccuracy = kCLLocationAccuracyBest

        when (manager.authorizationStatus) {
            kCLAuthorizationStatusNotDetermined -> manager.requestWhenInUseAuthorization()
            kCLAuthorizationStatusAuthorizedWhenInUse,
            kCLAuthorizationStatusAuthorizedAlways -> manager.requestLocation()
            kCLAuthorizationStatusDenied,
            kCLAuthorizationStatusRestricted -> {
                finished = true
                cont.resumeWithException(IllegalStateException("Location permission denied"))
            }
            else -> manager.requestWhenInUseAuthorization()
        }

        cont.invokeOnCancellation {
            manager.delegate = null
        }
    }
}
