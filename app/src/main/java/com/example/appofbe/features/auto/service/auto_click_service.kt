package com.example.appofbe.features.auto.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Intent
import android.graphics.Path
import android.os.Build
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.example.appofbe.features.app.MainAct
import com.example.appofbe.features.auto.bean.Event
import com.example.appofbe.features.auto.logd
import com.example.appofbe.features.utils.Log


/**
 * Created on 2018/9/28.
 * By nesto
 */

var autoClickService: AutoClickService? = null
class AutoClickService : AccessibilityService() {
    private val events = mutableListOf<Event>()

    override fun onServiceConnected() {
        super.onServiceConnected()
        "onServiceConnected".Log()
        autoClickService = this
        startActivity(Intent(this, MainAct::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        when {
            (event?.eventType == AccessibilityEvent.TYPE_VIEW_CLICKED) or (event?.eventType == AccessibilityEvent.TYPE_VIEW_FOCUSED) -> {
                val nodeInfo = event?.source
                takeIf { (nodeInfo?.className == "android.widget.EditText") }?.apply {
                    val arguments = Bundle()
                    arguments.putCharSequence(
                        AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                        "Text input ... "
                    )
                    nodeInfo?.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
                    nodeInfo?.performAction(AccessibilityNodeInfo.ACTION_NEXT_AT_MOVEMENT_GRANULARITY)
                }
            }
        }
        rootInActiveWindow
        dispatchGesture()

        //" name : ${rootInActiveWindow.describeContents()} ${rootInActiveWindow.className} ${rootInActiveWindow.viewIdResourceName} ${rootInActiveWindow.isAccessibilityFocused}  ${rootInActiveWindow.isCheckable} ".Log()
    }

    override fun onInterrupt() {
        //TODO("Not yet implemented")
    }



    override fun onUnbind(intent: Intent?): Boolean {
        "AutoClickService onUnbind".Log()
        autoClickService = null
        return super.onUnbind(intent)
    }


    override fun onDestroy() {
        "AutoClickService onDestroy".Log()
        autoClickService = null
        super.onDestroy()
    }
}