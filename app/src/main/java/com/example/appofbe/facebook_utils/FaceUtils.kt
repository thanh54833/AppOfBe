package com.example.appofbe.facebook_utils

import android.content.Context
import android.content.Intent
import android.net.Uri

object FaceUtils {

    fun OpenAppFacebook(context: Context) {
        val uri = "facebook:/newsfeed"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        context.startActivity(intent)
    }


    //doc ... https://stackoverflow.com/questions/23493030/clearing-application-data-programmatically-android
    private fun clearAppData() {
        try {
            val runtime = Runtime.getRuntime()
            runtime.exec(
                "pm clear " + ApplicationProvider.getApplicationContext().getPackageName() + " HERE"
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}