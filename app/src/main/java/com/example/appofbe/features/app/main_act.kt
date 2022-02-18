package com.example.appofbe.features.app

import android.accessibilityservice.AccessibilityServiceInfo
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.appofbe.R
import com.example.appofbe.databinding.MainActBinding
import com.example.appofbe.features.auto.service.FloatingClickService
import com.example.appofbe.features.auto.service.autoClickService
import com.example.appofbe.features.capture.screenshot_service
import com.example.appofbe.features.facebook_utils.Log


class MainAct : AppCompatActivity() {
    lateinit var binding: MainActBinding
    private var serviceIntent: Intent? = null

    companion object {
        private const val PERMISSION_CODE = 110
        private const val REQUEST_SCREENSHOT = 59706
    }

    //----------------------------------------------------------------------------------------------
    var isRequestPermission = false;
    override fun onResume() {
        super.onResume()
        if (isRequestPermission) {
            checkPermission()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@MainAct, R.layout.main_act)

        /// detect onClick start
        binding.start.setOnClickListener {
            serviceIntent = Intent(this@MainAct, FloatingClickService::class.java)
            startService(serviceIntent)
        }

        /// Todo : thanh.ph handle code.

        val mainIntent = Intent(Intent.ACTION_MAIN, null)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        val pkgAppsList: List<ResolveInfo> =
            this.applicationContext.packageManager.queryIntentActivities(mainIntent, 0)
        "pkgAppsList :.. ${pkgAppsList.size} ".Log();
        pkgAppsList.forEach {
            "package : ${it.activityInfo.packageName}".Log()
        }

    }

    private fun checkPermission() {
        val hasPermission = isEnableAccessibility()
        if (!hasPermission) {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
//            askPermission()
//        }
    }


    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SCREENSHOT) {
            if (resultCode == Activity.RESULT_OK) {
                val i = Intent(this, screenshot_service::class.java)
                    .putExtra(screenshot_service.EXTRA_RESULT_CODE, resultCode)
                    .putExtra(screenshot_service.EXTRA_RESULT_INTENT, data)

                startService(i)
            }
        }
        finish()
    }

    private fun isEnableAccessibility(): Boolean {
        val string = getString(R.string.accessibility_service_id)
        val manager = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val list =
            manager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC)
        for (id in list) {
            if (string == id.id) {
                return true
            }
        }
        return false
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun askPermission() {
//        val intent = Intent(
//            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//            Uri.parse("package:$packageName")
//        )
//        startActivityForResult(intent, PERMISSION_CODE)
    }


    override fun onDestroy() {
        super.onDestroy()
        serviceIntent?.let {
            stopService(it)
        }
        autoClickService?.let {
            it.stopSelf()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) return it.disableSelf()
            autoClickService = null
        }
    }
}