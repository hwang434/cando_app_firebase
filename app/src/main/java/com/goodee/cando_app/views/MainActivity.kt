package com.goodee.cando_app.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.goodee.cando_app.R
import com.goodee.cando_app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val TAG: String = "로그"
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG,"MainActivity - onCreate() called")
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    }

    override fun onStart() {
        Log.d(TAG,"MainActivity - onStart() called")
        super.onStart()
    }

    override fun onResume() {
        Log.d(TAG,"MainActivity - onResume() called")
        super.onResume()
    }

    override fun onPause() {
        Log.d(TAG,"MainActivity - onPause() called")
        super.onPause()
    }

    override fun onStop() {
        Log.d(TAG,"MainActivity - onStop() called")
        super.onStop()
    }

    override fun onRestart() {
        Log.d(TAG,"MainActivity - onRestart() called")
        super.onRestart()
    }

    override fun onDestroy() {
        Log.d(TAG,"MainActivity - onDestroy() called")
        super.onDestroy()
    }
}