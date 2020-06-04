package com.example.appofbe.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.appofbe.R
import com.example.appofbe.databinding.MainActBinding


class MainAct : AppCompatActivity() {

    lateinit var binding: MainActBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@MainAct, R.layout.main_act)


    }

}