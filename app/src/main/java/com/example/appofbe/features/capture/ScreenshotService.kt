package com.example.appofbe.features.capture


/***
Copyright (c) 2015 CommonsWare, LLC
Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy
of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
by applicable law or agreed to in writing, software distributed under the
License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
OF ANY KIND, either express or implied. See the License for the specific
language governing permissions and limitations under the License.
Covered in detail in the book _The Busy Coder's Guide to Android Development_
https://commonsware.com/Android
 */

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.AudioManager
import android.media.MediaScannerConnection
import android.media.ToneGenerator
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Process
import android.util.Log
import android.view.WindowManager
import androidx.annotation.Nullable
import com.example.appofbe.features.utils.Log
import java.io.File
import java.io.FileOutputStream


class ScreenshotService : Service() {
    private var projection: MediaProjection? = null
    private var vdisplay: VirtualDisplay? = null
    private val handlerThread = HandlerThread(
        javaClass.simpleName,
        Process.THREAD_PRIORITY_BACKGROUND
    )
    private var handler: Handler? = null
    private var mgr: MediaProjectionManager? = null
    private var wmgr: WindowManager? = null
    private var it: ImageTransmogrifier? = null
    private var resultCode = 0
    private var resultData: Intent? = null
    private val beeper = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)
    override fun onCreate() {
        super.onCreate()
        mgr = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        wmgr = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        handlerThread.start()
        handler = Handler(handlerThread.looper)
    }

    override fun onStartCommand(i: Intent, flags: Int, startId: Int): Int {
        "i.action ${i.action}  ${resultData} ${resultData != null}".Log()
        resultCode = i.getIntExtra(EXTRA_RESULT_CODE, 1337)
        resultData = i.getParcelableExtra(EXTRA_RESULT_INTENT)

        startCapture()
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        stopCapture()
        super.onDestroy()
    }

    @Nullable
    override fun onBind(intent: Intent): IBinder? {
        throw IllegalStateException("Binding not supported. Go away.")
    }

    val windowManager: WindowManager?
        get() = wmgr

    fun getHandler(): Handler? {
        return handler
    }

    fun processImage(png: ByteArray?) {
        object : Thread() {
            override fun run() {
                "Thread ... ".Log()
                val output = File(getExternalFilesDir(null), "screenshot.png")
                try {
                    val fos = FileOutputStream(output)
                    fos.write(png)
                    fos.flush()
                    fos.fd.sync()
                    fos.close()
                    MediaScannerConnection.scanFile(
                        this@ScreenshotService,
                        arrayOf(output.absolutePath),
                        arrayOf("image/png"),
                        null
                    )
                } catch (e: Exception) {
                    Log.e(
                        javaClass.simpleName,
                        "Exception writing out screenshot",
                        e
                    )
                }
            }
        }.start()
        beeper.startTone(ToneGenerator.TONE_PROP_ACK)
        stopCapture()
    }

    private fun stopCapture() {
        if (projection != null) {
            projection!!.stop()
            vdisplay!!.release()
            projection = null
        }
    }

    private fun startCapture() {
        projection = mgr?.getMediaProjection(resultCode, resultData!!)
        it = ImageTransmogrifier(this)
        val cb: MediaProjection.Callback = object : MediaProjection.Callback() {
            override fun onStop() {
                vdisplay!!.release()
            }
        }
        vdisplay = projection?.createVirtualDisplay(
            "andshooter",
            it?.getWidth()!!,
            it?.getHeight()!!,
            resources.displayMetrics.densityDpi,
            VIRT_DISPLAY_FLAGS,
            it?.surface!!,
            null,
            handler
        )
        projection?.registerCallback(cb, handler)
    }

    companion object {
        private const val CHANNEL_WHATEVER = "channel_whatever"
        private const val NOTIFY_ID = 9906
        const val EXTRA_RESULT_CODE = "resultCode"
        const val EXTRA_RESULT_INTENT = "resultIntent"
        val ACTION_RECORD: String = com.example.appofbe.BuildConfig.APPLICATION_ID + ".RECORD"
        val ACTION_SHUTDOWN: String = com.example.appofbe.BuildConfig.APPLICATION_ID + ".SHUTDOWN"
        const val VIRT_DISPLAY_FLAGS =
            DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY or DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC
    }
}