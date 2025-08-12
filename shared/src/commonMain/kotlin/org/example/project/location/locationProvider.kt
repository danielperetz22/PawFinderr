package org.example.project.location

expect suspend fun getLocation() : Location
data class Location(val latitude: Double, val longitude: Double)