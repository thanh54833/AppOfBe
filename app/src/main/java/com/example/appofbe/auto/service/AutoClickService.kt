package com.example.appofbe.auto.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Intent
import android.graphics.Path
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.example.appofbe.app.Log
import com.example.appofbe.app.MainAct
import com.example.appofbe.auto.bean.Event
import com.example.appofbe.auto.logd


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
        //"event type : ${event?.eventType} ".Log()
        when {
            (event?.eventType == AccessibilityEvent.TYPE_VIEW_CLICKED) or (event?.eventType == AccessibilityEvent.TYPE_VIEW_FOCUSED) -> {
                //"On_CLick...".Log()
                val nodeInfo = event?.source

                "nodeInfo description : ${nodeInfo?.describeContents()} ${nodeInfo?.contentDescription} ".Log()

                //nodeInfo?.contentDescription
                takeIf { (nodeInfo?.className == "android.widget.EditText") }?.apply {
                    val arguments = Bundle()
                    arguments.putCharSequence(
                        AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                        "Thanh thanh ne"
                    )
                    nodeInfo?.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
                    nodeInfo?.performAction(AccessibilityNodeInfo.ACTION_NEXT_AT_MOVEMENT_GRANULARITY)
                }
            }
        }

        //Todo : code log id screen ...
        if ((rootInActiveWindow?.childCount ?: 0) >= 1) {
            //"windown :...  ${rootInActiveWindow.childCount} ${rootInActiveWindow.getChild(0).className}".Log()
            logViewHierarchy(rootInActiveWindow, 0)
        }


        //fillEdit("Username", "phamhoaithanh32@gmail.com")

        //fillEdit("Password", "thanh54833Lumia520")

        interactClick("Log In")

        //Todo : Chup anh man hinh và filter ...
    }

    private fun fillEdit(key: String, value: String) {
        rootInActiveWindow.findAccessibilityNodeInfosByText(key).firstOrNull()?.let { _info ->

            " value : ${_info.text}".Log()

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

    private fun interactClick(key: String) {

        "size :==  ${rootInActiveWindow.findAccessibilityNodeInfosByText(key).size}".Log()
        //rootInActiveWindow.fin
        rootInActiveWindow.findAccessibilityNodeInfosByText(key).firstOrNull()?.let { _info ->
            _info.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        }
    }

    private fun logViewHierarchy(nodeInfo: AccessibilityNodeInfo?, depth: Int) {
        nodeInfo?.apply {
            var spacerString = ""
            for (i in 0 until depth) {
                spacerString += '-'
            }
            //Log the info you care about here... I choce classname and view resource name, because they are simple, but interesting.
            "logViewHierarchy :: ${spacerString + className }  ${viewIdResourceName} ${contentDescription}".Log()
            for (i in 0 until childCount) {
                logViewHierarchy(getChild(i), depth + 1)
            }
        } ?: run { return }
    }


    fun click(x: Int, y: Int) {
        "click $x $y".logd()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return
        val path = Path()
        path.moveTo(x.toFloat(), y.toFloat())
        val builder = GestureDescription.Builder()
        val gestureDescription = builder
            .addStroke(GestureDescription.StrokeDescription(path, 10, 10))
            .build()
        dispatchGesture(gestureDescription, null, null)
    }

    fun setText() {
        /*if (Build.VERSION_CODES.JELLY_BEAN_MR2 <= Build.VERSION.SDK_INT) {
            nodeInput.refresh();
        }
        val response = "sometext";
        if (Build.VERSION_CODES.LOLLIPOP <= Build.VERSION.SDK_INT) {
            val bundle = Bundle();
            bundle.putCharSequence(
                AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                response
            )
            //set the text
            nodeInput.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, bundle);
        } else {
            val clipboardManager = activity?.getSystemService(Context.CLIPBOARD_SERVICE)
            if (clipboardManager != null) {

                var lastClip = "";
                val clipData = clipboardManager . getPrimaryClip ();

                if (clipData != null) {
                    lastClip = clipData.getItemAt(0).coerceToText(activity).toString();
                }
                clipboardManager.setPrimaryClip(ClipData.newPlainText("label", response), response)
                if (Build.VERSION_CODES.JELLY_BEAN_MR2 <= Build.VERSION.SDK_INT) {
                    nodeInput.performAction(AccessibilityNodeInfo.ACTION_PASTE);
                } else {
                    nodeInput.performAction(AccessibilityNodeInfoCompat.ACTION_PASTE);
                }
                clipboardManager.setPrimaryClip(ClipData.newPlainText(lastClip, lastClip));
            }
        }*/
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
        "AutoClickService onUnbind".logd()
        autoClickService = null
        return super.onUnbind(intent)
    }


    override fun onDestroy() {
        "AutoClickService onDestroy".logd()
        autoClickService = null
        super.onDestroy()
    }
}