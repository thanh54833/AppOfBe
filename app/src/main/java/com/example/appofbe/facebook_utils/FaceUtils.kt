package com.example.appofbe.facebook_utils

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings

object FaceUtils {
    var appName = "Facebook"

    fun OpenAppFacebook(context: Context) {
        val uri = "facebook:/newsfeed"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        context.startActivity(intent)
    }

    fun Context.isInstalledWith(name: String = appName): Boolean {
        val packageManager = packageManager
        return runCatching { packageManager.getPackageInfo(getPackageWith(name), 0) }.isSuccess
    }

    fun Context.getPackageWith(name: String = appName): String {
        getPackage()?.forEach { _appInfo ->
            _appInfo?.packageName?.let { _package ->
                if (getAppNameWith(_package).equals(name, ignoreCase = true)) {
                    return _package
                }
            }
        }
        return ""
    }

    private fun Context.getAppNameWith(appName: String): String? {
        val applicationInfo: ApplicationInfo? = try {
            packageManager.getApplicationInfo(appName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
        return applicationInfo?.let { packageManager.getApplicationLabel(it).toString() }
            ?: run { "" }
    }

    private fun Context.getPackage(): List<ApplicationInfo?>? {
        return packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
    }

    fun Context.openAppInfo(packageName: String) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:$packageName")
        startActivity(intent)
    }

    // doc ... https://stackoverflow.com/questions/23493030/clearing-application-data-programmatically-android
    /*fun Context.clearAppData(packageName: String = getPackageWith()) {
        try {
            // clearing app data
            val runtime = Runtime.getRuntime()
            "pm clear $packageName HERE".Log()
            runtime.exec("pm clear $packageName HERE")
        } catch (e: Exception) {
            "error : ${e.message} ".Log()
            e.printStackTrace()
        }
    }*/

    fun login() {

    }
}