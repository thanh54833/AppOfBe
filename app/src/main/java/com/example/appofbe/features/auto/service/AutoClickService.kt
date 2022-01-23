package com.example.appofbe.features.auto.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Intent
import android.graphics.Path
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.example.appofbe.features.app.Log
import com.example.appofbe.features.app.MainAct
import com.example.appofbe.features.auto.bean.Event
import com.example.appofbe.features.auto.logd


/**
 * Created on 2018/9/28.
 * By nesto
 */

var autoClickService: AutoClickService? = null

class AutoClickService : AccessibilityService() {
    internal val events = mutableListOf<Event>()
    override fun onInterrupt() {
        // NO-OP
    }

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
        //" name : ${rootInActiveWindow.describeContents()} ${rootInActiveWindow.className} ${rootInActiveWindow.viewIdResourceName} ${rootInActiveWindow.isAccessibilityFocused}  ${rootInActiveWindow.isCheckable} ".Log()
    }

    fun inputEditText(key: String, value: String) {

        rootInActiveWindow.findAccessibilityNodeInfosByText(key).firstOrNull()?.let { _info ->
            if (TextUtils.isEmpty(_info.text) || true) {
                val arguments = Bundle()
                arguments.putCharSequence(
                    AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, value
                )
                _info.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
                _info.performAction(AccessibilityNodeInfo.ACTION_NEXT_AT_MOVEMENT_GRANULARITY)
            }
        }
    }

    fun interactClick(key: String) {
        rootInActiveWindow.findContent(key) { _nodeInfo ->
            _nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        }
    }

    private fun AccessibilityNodeInfo?.findContent(
        content: String,
        depth: Int = 0,
        result: (nodeInfo: AccessibilityNodeInfo) -> Unit = {}
    ) {
        this?.let { _nodeInfo ->
            if (_nodeInfo.contentDescription?.toString()
                    ?.equals(content, ignoreCase = true) == true
            ) {
                result(_nodeInfo)
                return
            }
            for (i in 0 until _nodeInfo.childCount) {
                _nodeInfo.getChild(i).findContent(content, depth + 1, result)
            }
        } ?: run { return }
    }

    fun click(x: Int, y: Int) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return
        val path = Path()
        path.moveTo(x.toFloat(), y.toFloat())
        val builder = GestureDescription.Builder()
        val gestureDescription = builder
            .addStroke(GestureDescription.StrokeDescription(path, 10, 10))
            .build()
        dispatchGesture(gestureDescription, null, null)
    }

    fun run(newEvents: MutableList<Event>) {
        events.clear()
        events.addAll(newEvents)
        events.toString().logd()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return
        val builder = GestureDescription.Builder()
        events.forEach { builder.addStroke(it.onEvent()) }
        dispatchGesture(builder.build(), null, null)
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