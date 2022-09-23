package com.goodee.cando_app.util

import android.util.Log
import com.goodee.cando_app.BuildConfig
import com.goodee.cando_app.views.MainActivity
import com.google.firebase.auth.FirebaseAuth
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException

object SocketLike {
    private const val ADDRESS = "https://serene-eyrie-33508.herokuapp.com/"
    private const val TAG: String = "로그"
    private lateinit var socket: Socket

    init {
        try {
            socket = IO.socket(ADDRESS)
        } catch (e: URISyntaxException) {
            Log.w(TAG, ": ", e)
        }
    }

    fun getSocket(): Socket {
        return socket
    }

    fun connectSocket() {
        Log.d(TAG,"SocketLike - setSocket() called")
        if (isSocketConnected()) {
            return
        }

        socket.connect()
    }

    fun disconnectSocket() {
        Log.d(TAG,"SocketLike - disconnectSocket() called")
        socket.disconnect()
    }

    fun emitData(key: String, args: Any) {
        Log.d(TAG,"SocketLike - emitData($key) called")
        socket.emit(key, args)
    }

    fun isSocketConnected(): Boolean {
        Log.d(TAG,"SocketLike - isSocketConnected() called")
        return socket.connected()
    }
}