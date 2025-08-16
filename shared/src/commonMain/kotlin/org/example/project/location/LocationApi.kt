package org.example.project.location

class LocationApi {
    suspend fun get(): Location = getLocation()
}