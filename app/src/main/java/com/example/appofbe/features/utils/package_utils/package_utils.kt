package com.example.appofbe.features.facebook_utils

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import com.example.appofbe.common.logger.Log


fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

object PackageUtils {
    var appName = "Facebook"
    var PAGE_ID_TEST = "khongsocho.official"

    fun openAppFacebook(context: Context) {
        val uri = "facebook:/newsfeed"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun isInstalledWith(context: Context, name: String = appName): Boolean {
        val packageManager = context.packageManager
        return runCatching {
            packageManager.getPackageInfo(
                getPackageWith(context, name),
                0
            )
        }.isSuccess
    }

    fun getPackageWith(context: Context, name: String = appName): String {
        getPackage(context)?.forEach { _appInfo ->
            _appInfo?.packageName?.let { _package ->
                if (getAppNameWith(context, _package).equals(name, ignoreCase = true)) {
                    return _package
                }
            }
        }
        return ""
    }

    fun getAppNameWith(context: Context, appName: String): String? {
        val applicationInfo: ApplicationInfo? = try {
            context.packageManager.getApplicationInfo(appName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
        return applicationInfo?.let { context.packageManager.getApplicationLabel(it).toString() }
            ?: run { "" }
    }

    private fun getPackage(context: Context): List<ApplicationInfo?>? {
        return context.packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
    }

    fun Context.openAppInfo(packageName: String) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:$packageName")
        startActivity(intent)
    }

    // doc ... https://stackoverflow.com/questions/23493030/clearing-application-data-programmatically-android
    fun login() {

    }

    //method to open page facebook with id page ...
    fun openPageWith(context: Context, id: String = PAGE_ID_TEST) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse(getFacebookPageURL(context, id))
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    //method to get the right URL to use in the intent ..  path : =https://stackoverflow.com/questions/34564211/open-facebook-page-in-facebook-app-if-installed-on-android/34564284
    fun getFacebookPageURL(context: Context, idPage: String = PAGE_ID_TEST): String? {
        val packageManager = context.packageManager
        return try {
            val versionCode =
                packageManager.getPackageInfo(getPackageWith(context = context), 0).versionCode
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

    // check app install with package name ...
    fun Context.isAppInstalled(packageName: String = "com.facebook.katana"): Boolean {
        return try {
            packageManager.getApplicationInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
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