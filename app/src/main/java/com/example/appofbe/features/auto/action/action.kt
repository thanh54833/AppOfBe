package com.example.appofbe.features.auto.action

import android.accessibilityservice.AccessibilityService.GestureResultCallback
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.TextView
import com.example.appofbe.features.auto.bean.Event
import com.example.appofbe.features.auto.service.autoClickService
import kotlin.concurrent.fixedRateTimer

//
class Action(
    var rootInActiveWindow: AccessibilityNodeInfo, var events: MutableList<Event>,
    var dispatchGesture: (gesture: GestureDescription, callback: GestureResultCallback?, handler: Handler?) -> Boolean
) {

    fun openPackageApp(name :String){

    }

    fun inputEditText(key: String, value: String) {
        rootInActiveWindow.findAccessibilityNodeInfosByText(key).firstOrNull()?.let { _info ->
            val arguments = Bundle()
            arguments.putCharSequence(
                AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, value
            )
            _info.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
            _info.performAction(AccessibilityNodeInfo.ACTION_NEXT_AT_MOVEMENT_GRANULARITY)
        }
    }

    fun addPress(key: String) {
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
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return
        val builder = GestureDescription.Builder()
        events.forEach { builder.addStroke(it.onEvent()) }
        dispatchGesture(builder.build(), null, null)
    }

//    private var isOn = false
//    private fun viewOnClick() {
//        if (isOn) {
//            timer?.cancel()
//        } else {
//            timer = fixedRateTimer(
//                initialDelay = 0,
//                period = 200
//            ) {
//                view.getLocationOnScreen(location)
//                autoClickService?.click(
//                    location[0] + view.right + 10,
//                    location[1] + view.bottom + 10
//                )
//            }
//        }
//        isOn = !isOn
//        (view as TextView).text = if (isOn) "ON" else "OFF"
//    }
}