package org.example.project

import android.content.Context
import android.net.Uri
import android.util.Log
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback


object CloudinaryUploader {
    fun upload(
        context: Context,
        uri: Uri,
        onResult: (String?) -> Unit
    ) {
        // MediaManager.init(...) must already have been called in your MyApp.onCreate
        MediaManager.get().upload(uri)
            .callback(object : UploadCallback {
                override fun onStart(requestId: String) = Unit
                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) = Unit
                override fun onSuccess(requestId: String, resultData: Map<Any?, Any?>) {
                    val url = resultData["secure_url"] as? String
                    onResult(url)
                }
                override fun onError(requestId: String, error: ErrorInfo) {
                    Log.e("Cloudinary", "Upload error: ${error.getDescription()}")
                    onResult(null)
                }

                override fun onReschedule(requestId: String, error: ErrorInfo) {
                    Log.w("Cloudinary", "Upload rescheduled: ${error.description}")
                    onResult(null)
                }
            })
            .dispatch()
    }
}