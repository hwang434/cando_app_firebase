package com.goodee.cando_app.model

import android.app.Application
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.coroutineScope

class AppRepository(val application: Application) {
    private val TAG: String = "로그"
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _userLiveData: MutableLiveData<FirebaseUser> = MutableLiveData()
    val userLiveData: LiveData<FirebaseUser>
        get() = _userLiveData

    fun register(email: String, password: String) {
        Log.d(TAG,"AppRepository - register() called")
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(ContextCompat.getMainExecutor(application.applicationContext)) { task ->
            if (task.isSuccessful) _userLiveData.postValue(firebaseAuth.currentUser)

            // 지워야할 거
            else {
                Toast.makeText(application, "Register Fail.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun login(email: String, password: String) {
        Log.d(TAG,"AppRepository - login() called")
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(ContextCompat.getMainExecutor(application.applicationContext)) { task ->
//            if (task.isSuccessful) _userLiveData.postValue(firebaseAuth.currentUser)
//            else {
//                Toast.makeText(application, "Register Fail.", Toast.LENGTH_SHORT).show()
//            }
            _userLiveData.postValue(firebaseAuth.currentUser)
        }
    }
}