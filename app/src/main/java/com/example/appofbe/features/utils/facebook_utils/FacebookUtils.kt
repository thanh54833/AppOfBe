package com.example.appofbe.features.utils.facebook_utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import com.example.appofbe.common.logger.Log
import com.example.appofbe.features.utils.package_utils.PackageUtils


class FacebookUtils {

    fun openAppFacebook(context: Context) {
        val uri = "facebook:/newsfeed"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    //method to get the right URL to use in the intent ..  path : =https://stackoverflow.com/questions/34564211/open-facebook-page-in-facebook-app-if-installed-on-android/34564284
    private fun getFacebookPageURL(
        context: Context,
        idPage: String = PackageUtils.PAGE_ID_TEST
    ): String? {
        val packageManager = context.packageManager
        return try {
            val versionCode =
                packageManager.getPackageInfo(
                    PackageUtils.getPackage(context = context),
                    0
                ).versionCode
            if (versionCode >= 3002850 || !Utils.isNumber(idPage)) { //newer versions of fb app
                "fb://facewebmodal/f?href=https://www.facebook.com/${idPage}"
            } else { //older versions of fb app
                "fb://page/$idPage"
            }
        } catch (e: PackageManager.NameNotFoundException) {
            "error message : ${e.message}".Log()
            idPage
        }
    }

    //method to open page facebook with id page ...
    fun openPageWith(context: Context, id: String = PackageUtils.PAGE_ID_TEST) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse(getFacebookPageURL(context, id))
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}

object Utils {
    fun isNumber(str: String): Boolean {
        var i = 0
        val len = str.length
        var a = false
        var b = false
        var c = false
        var d = false
        if (i < len && (str[i] == '+' || str[i] == '-')) i++
        while (i < len && isDigit(str[i])) {
            i++
            a = true
        }
        if (i < len && str[i] == '.') i++
        while (i < len && isDigit(str[i])) {
            i++
            b = true
        }
        if (i < len && (str[i] == 'e' || str[i] == 'E') && (a || b)) {
            i++
            c = true
        }
        if (i < len && (str[i] == '+' || str[i] == '-') && c) i++
        while (i < len && isDigit(str[i])) {
            i++
            d = true
        }
        return i == len && (a || b) && (!c || c && d)
    }

    private fun isDigit(c: Char): Boolean {
        return c in '0'..'9'
    }

}