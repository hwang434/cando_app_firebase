package com.goodee.cando_app.listener

import android.util.Log
import android.view.View

abstract class SingleClickListener : View.OnClickListener {
    private var mLastClickTime = 0L
    companion object {
        private const val MIN_CLICK_INTERVAL = 1000
        private const val TAG: String = "로그"
    }
    abstract fun onSingleClick(view: View?)

    override fun onClick(v: View?) {
        Log.d(TAG,"SingleClickListener - onClick() called")
        val currentClickTime = System.currentTimeMillis()
        val elapsedTime = currentClickTime - mLastClickTime
        mLastClickTime = currentClickTime

        // 중복클릭 아닌 경우
        if (elapsedTime > MIN_CLICK_INTERVAL) {
            onSingleClick(v)
        }
    }
}