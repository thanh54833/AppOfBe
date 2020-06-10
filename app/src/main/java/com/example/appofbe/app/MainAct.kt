package com.example.appofbe.app

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
import com.example.appofbe.auto.service.FloatingClickService
import com.example.appofbe.auto.service.autoClickService
import com.example.appofbe.auto.shortToast
import com.example.appofbe.capture.ScreenshotService
import com.example.appofbe.databinding.MainActBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


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
        //clearAppData()
        binding.start.setOnClickListener {

        }
        binding.root.setOnClickListener {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N || Settings.canDrawOverlays(this)) {
                serviceIntent = Intent(this@MainAct, FloatingClickService::class.java)
                startService(serviceIntent)
                //onBackPressed()
            } else {
                askPermission()
                shortToast("You need System Alert Window Permission to do this")
            }
        }
        //Todo : test runnable ...
        //Test.fixedThreadPoolExample()//singleThreadPoolExample()
        //openAppFacebook()
        //openPageWith()
        //val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getFacebookPageURL()))

        /*"${getFacebookPageURL("khongsocho.official")}".Log()
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(getFacebookPageURL("khongsocho.official"))
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)*/

        /*val service = Intent(this@MainAct, ScreenshotService::class.java).apply {
            putExtra(ScreenshotService.EXTRA_RESULT_CODE, "")
            putExtra(ScreenshotService.EXTRA_RESULT_INTENT, "")
        }
        startService(service)*/

        //val mgr = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        //startActivityForResult(mgr.createScreenCaptureIntent(), REQUEST_SCREENSHOT);

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


    private fun checkAccess(): Boolean {
        val string = getString(R.string.accessibility_service_id)
        val manager = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val list =
            manager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC)
        for (id in list) {
            if (string == id.id) {
                return true
            }
        }
        //Todo : Thanh test ...
        return false

        //return true
    }

    override fun onResume() {
        super.onResume()
        val hasPermission = checkAccess()
        if (!hasPermission) {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            askPermission()
        }
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

object Test {
    class RequestHandler(var name: String) : Runnable {
        override fun run() {
            try {
                // Bắt đầu xử lý request đến
                "${(Thread.currentThread().name + " Starting process " + name)}".Log()
                // cho ngủ 500 milis để ví dụ là quá trình xử lý mất 0,5 s
                Thread.sleep(500)
                // Kết thúc xử lý request
                "${(Thread.currentThread().name + " Finished process " + name)}".Log()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }

    }

    fun singleThreadPoolExample() {
        val executor: ExecutorService = Executors.newSingleThreadExecutor()
        // Có 100 request tới cùng lúc
        // Có 100 request tới cùng lúc
        for (i in 0..99) {
            executor.execute(RequestHandler("request-$i"))
        }
        executor.shutdown() // Không cho threadpool nhận thêm nhiệm vụ nào nữa
        while (!executor.isTerminated) {
            // Chờ xử lý hết các request còn chờ trong Queue ...
        }
    }

    fun fixedThreadPoolExample() {
        val executor = Executors.newFixedThreadPool(5)
        // Có 100 request tới cùng lúc
        // Có 100 request tới cùng lúc
        for (i in 0..99) {
            executor.execute(RequestHandler("request-$i"))
        }
        executor.shutdown() // Không cho threadpool nhận thêm nhiệm vụ nào nữa
        while (!executor.isTerminated) {
            // Chờ xử lý hết các request còn chờ trong Queue ...
        }
    }
}