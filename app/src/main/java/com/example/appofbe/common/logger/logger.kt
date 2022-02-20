package com.example.appofbe.common.logger

import android.text.TextUtils
import android.util.Log
import com.example.appofbe.BuildConfig
import com.google.gson.Gson
import java.util.*

//  CommonUtils
fun <T> T.Log(message: String = ""): T {
    //remove all log..
    if (BuildConfig.DEBUG) {
        var messenger = message
        (this as? Objects)?.apply {
            this::class.java.name.apply {
                if (!TextUtils.isEmpty(this)) {
                    messenger = "$this : "
                }
            }
        }
        logCat("$messenger ${Gson().toJson(this)}")
    }
    return this
}

fun <T> T.Log(message: String, handle: (it: T) -> String): T {
    handle(this).let { _result ->
        _result.Log(message)
    }
    return this
}

private fun logCat(messenger: String) {
    if (BuildConfig.DEBUG) {
        Log.i(tag(), messenger)
    }
}

private fun tag(): String? {
    fun getNumberStack(): Int {
        var index = 0
        Thread.currentThread().stackTrace.filterIndexed { _index, _item ->
            return@filterIndexed if (_item.fileName.equals(
                    "logger.kt",
                    ignoreCase = true
                )
            ) {
                index = _index
                true
            } else {
                false
            }
        }
        return index
    }
    return Thread.currentThread().stackTrace.getOrNull(getNumberStack())?.let { element ->
        "(${element.fileName}:${element.lineNumber})I/~~~"
    }
}

fun <T> T.toJson(): String {
    return Gson().toJson(this)
}

