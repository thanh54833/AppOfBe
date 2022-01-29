package com.example.appofbe.features.app

import android.accessibilityservice.AccessibilityServiceInfo
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.appofbe.R
import com.example.appofbe.databinding.MainActBinding
import com.example.appofbe.features.auto.service.FloatingClickService
import com.example.appofbe.features.auto.service.autoClickService
import com.example.appofbe.features.capture.ScreenshotService


class MainAct : AppCompatActivity() {
    lateinit var binding: MainActBinding
    private var serviceIntent: Intent? = null

    companion object {
        private const val PERMISSION_CODE = 110
        private const val REQUEST_SCREENSHOT = 59706
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@MainAct, R.layout.main_act)

        /// detect onClick start
        binding.start.setOnClickListener {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N || Settings.canDrawOverlays(this)) {
                serviceIntent = Intent(this@MainAct, FloatingClickService::class.java)
                startService(serviceIntent)
            } else {
                askPermission()
            }
        }

    }


    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SCREENSHOT) {
            if (resultCode == Activity.RESULT_OK) {
                val i = Intent(this, ScreenshotService::class.java)
                    .putExtra(ScreenshotService.EXTRA_RESULT_CODE, resultCode)
                    .putExtra(ScreenshotService.EXTRA_RESULT_INTENT, data)

                startService(i)
            }
        }
        finish()
    }


    override fun onResume() {
        super.onResume()
        val hasPermission = isEnableAccessibility()
        if (!hasPermission) {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            askPermission()
        }
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


    @TargetApi(Build.VERSION_CODES.M)
    private fun askPermission() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
        startActivityForResult(intent, PERMISSION_CODE)
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