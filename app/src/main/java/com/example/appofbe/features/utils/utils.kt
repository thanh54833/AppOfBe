package com.example.appofbe.features.utils

import android.content.Context
import android.util.Log
import android.widget.Toast

class Utils {

}

fun String.Log(message: String = "Log") {
    Log.i("===", "${message} : ${this}")
}

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}