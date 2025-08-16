package org.example.project

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.cloudinary.android.MediaManager


class MyApp : Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var ctx: Context
            private set
    }
    override fun onCreate() {
        super.onCreate()
        ctx = applicationContext

        val config = hashMapOf(
            "cloud_name" to "duk7ujnww",
            "api_key"    to "344296978824576",
            "api_secret" to "WI-D8ORFmsk1cJWP2aGuiu6mPWk"
        )
        MediaManager.init(this, config)
    }
}

