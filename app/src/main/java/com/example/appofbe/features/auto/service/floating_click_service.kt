package com.example.appofbe.features.auto.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import com.example.appofbe.R
import com.example.appofbe.common.logger.Log
import com.example.appofbe.features.facebook_utils.PackageUtils.openAppFacebook
import java.util.*

/**
 * Created on 2018/9/28.
 * By nesto
 */
class FloatingClickService : Service() {
    private lateinit var manager: WindowManager
    private lateinit var view: View
    private lateinit var params: WindowManager.LayoutParams
    private var xForRecord = 0
    private var yForRecord = 0
    private val location = IntArray(2)
    private var startDragDistance: Int = 0
    private var timer: Timer? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    @SuppressLint("InflateParams")
    override fun onCreate() {
        super.onCreate()
        startDragDistance = 100
        view = LayoutInflater.from(this).inflate(R.layout.widget, null)

        //setting the layout parameters
        val overlayParam =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            }
        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            overlayParam,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        //getting windows services and adding the floating view to it
        manager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        //manager.addView(view, params)

        //adding an touchlistener to make drag movement of the floating widget
        /*view.setOnTouchListener(
            TouchAndDragListener(
                params,
                startDragDistance,
                {
                    //viewOnClick()
                    //login()
                },
                { manager.updateViewLayout(view, params) })
        )*/
        // login()
        execute()
    }

    private fun login() {
        openAppFacebook(this)
        //openPageWith()
        Thread.sleep(5000)
        "Login ...".Log()
    }


    private fun execute() {


        openAppFacebook(this)
        //Thread.sleep(3000)
        /*autoClickService?.inputEditText ("Username", "dieuhong54833@gmail.com")
        Thread.sleep(1000)
        autoClickService?.inputEditText ("Password", "Lumia520")
        Thread.sleep(1000)
        autoClickService?.interactClick("Log In")*/

    }


    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
        manager.removeView(view)
    }

    // Todo  thanh comment.
//    override fun onConfigurationChanged(newConfig: Configuration?) {
//        super.onConfigurationChanged(newConfig)
//        val x = params.x
//        val y = params.y
//        params.x = xForRecord
//        params.y = yForRecord
//        xForRecord = x
//        yForRecord = y
//        manager.updateViewLayout(view, params)
//    }
}