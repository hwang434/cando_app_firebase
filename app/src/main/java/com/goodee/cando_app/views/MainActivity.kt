package com.goodee.cando_app.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.goodee.cando_app.R
import com.goodee.cando_app.databinding.ActivityMainBinding
import com.goodee.cando_app.util.SocketLike
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.StringBuilder

class MainActivity : AppCompatActivity() {
    private val TAG: String = "로그"
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG,"MainActivity - onCreate() called")
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // 소켓에 연결
        SocketLike.connectSocket()
        SocketLike.getSocket().on("like") { it ->
            Log.d(TAG,"MainActivity - like received")
            val jsonString = StringBuilder()
            it.forEach {
                jsonString.append(it.toString())
            }

            val jsonElement = JsonParser.parseString(jsonString.toString())
            val jsonObject = jsonElement.asJsonObject
            val userEmail = FirebaseAuth.getInstance().currentUser?.email
            val receiver = jsonObject["receiver"].toString().replace("\"", "")
            val sender = jsonObject["sender"].toString().replace("\"", "")
            Log.d(TAG,"MainActivity - sender : $sender receiver : $receiver userEmail : $userEmail")

            // if : a user who was liked has the same email as the current user then make the notification.
            if (receiver == userEmail) {
                Log.d(TAG,"MainActivity - 좋아요를 받았습니다.")
                lifecycleScope.launch(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "$sender likes your Diary.", Toast.LENGTH_SHORT).show()
                }
            }
        }
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
        // 앱이 화면에 안보이면 소켓 해제.
        SocketLike.disconnectSocket()
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