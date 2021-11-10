package com.goodee.cando_app.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.goodee.cando_app.dto.UserDto
import com.goodee.cando_app.model.AppRepository
import com.google.firebase.auth.FirebaseUser

class UserViewModel(application: Application): AndroidViewModel(application) {
    private val TAG: String = "로그"
    private var appRepository: AppRepository
    private val _userLiveData: MutableLiveData<FirebaseUser>
    val userLiveData: LiveData<FirebaseUser>
        get() = _userLiveData
    
    init {
        Log.d(TAG,"UserViewModel - init called")
        appRepository = AppRepository(application)
        _userLiveData = appRepository.userLiveData as MutableLiveData<FirebaseUser>
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG,"UserViewModel - onCleared() called")
    }

    // 회원가입
    fun register(userDto: UserDto) {
        Log.d(TAG,"User - register() called")
        appRepository.register(userDto)
    }

    // 로그인
    fun login(email: String, password: String) {
        Log.d(TAG,"User - login() called")
        appRepository.login(email, password)
    }

    // 아이디 찾기
    fun findUserId(name: String, email: String) {
        Log.d(TAG,"UserViewModel - findUserId() called")
        appRepository.findUserId(name, email)
    }
    
    // 비밀번호 찾기
    fun findUserPassword(id: String, email: String) {
        Log.d(TAG,"UserViewModel - findUserPassword() called")
    }
}
