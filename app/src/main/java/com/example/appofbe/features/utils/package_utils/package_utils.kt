package com.example.appofbe.features.utils.package_utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
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

    fun isInstalled(context: Context, name: String = appName): Boolean {
        val packageManager = context.packageManager
        return runCatching {
            packageManager.getPackageInfo(
                getPackage(context, name),
                0
            )
        }.isSuccess
    }

    fun getPackage(context: Context, name: String = appName): String {
        getPackages(context).forEach { _appInfo ->
            _appInfo?.packageName?.let { _package ->
                if (getNameApp(context, _package).equals(name, ignoreCase = true)) {
                    return _package
                }
            }
        }
        return ""
    }

    private fun getNameApp(context: Context, appName: String): String {
        val applicationInfo: ApplicationInfo? = try {
            context.packageManager.getApplicationInfo(appName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
        return applicationInfo?.let { context.packageManager.getApplicationLabel(it).toString() }
            ?: run { "" }
    }


    @SuppressLint("QueryPermissionsNeeded")
    fun getPackages(context: Context): List<ApplicationInfo?> {
        /// Todo : thanh.ph handle code.
        val mainIntent = Intent(Intent.ACTION_MAIN, null)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        val pkgAppsList: List<ResolveInfo> =
            context.applicationContext.packageManager.queryIntentActivities(mainIntent, 0)
        "pkgAppsList :.${pkgAppsList.size} ".Log()
        pkgAppsList.forEach {
            "package : ${it.activityInfo.packageName}".Log()
        }

        return context.packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
    }

    fun openAppInfo(context: Context, packageName: String) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:$packageName")
        context.startActivity(intent)
    }

    // doc ... https://stackoverflow.com/questions/23493030/clearing-application-data-programmatically-android
    fun login() {
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

