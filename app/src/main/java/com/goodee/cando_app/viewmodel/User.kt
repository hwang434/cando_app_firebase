package com.goodee.cando_app.viewmodel

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.goodee.cando_app.model.AppRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class User(application: Application): AndroidViewModel(application) {
    private val TAG: String = "로그"
    private var appRepository: AppRepository
    private val _userLiveData: MutableLiveData<FirebaseUser>
    val userLiveData: LiveData<FirebaseUser>
        get() = _userLiveData
    
    init {
        appRepository = AppRepository(application)
        _userLiveData = appRepository.userLiveData as MutableLiveData<FirebaseUser>
    }

    fun register(email: String, password: String) {
        Log.d(TAG,"User - register() called")
        appRepository.register(email, password)
    }
}
