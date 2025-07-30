package org.example.project

import android.app.Application
import com.cloudinary.android.MediaManager


class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val config = hashMapOf(
            "cloud_name" to "duk7ujnww",
            "api_key"    to "344296978824576",
            "api_secret" to "WI-D8ORFmsk1cJWP2aGuiu6mPWk"
        )
        MediaManager.init(this, config)
    }
}

