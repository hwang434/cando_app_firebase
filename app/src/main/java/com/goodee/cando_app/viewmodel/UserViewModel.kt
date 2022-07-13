package com.goodee.cando_app.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.goodee.cando_app.dto.UserDto
import com.goodee.cando_app.model.UserRepository
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class UserViewModel(application: Application): AndroidViewModel(application) {
    private val TAG: String = "로그"
    private var userRepository: UserRepository
    private val _userLiveData: MutableLiveData<FirebaseUser>
    val userLiveData: LiveData<FirebaseUser>
        get() = _userLiveData
    
    init {
        Log.d(TAG,"UserViewModel - init called")
        userRepository = UserRepository(application)
        _userLiveData = userRepository.userLiveData as MutableLiveData<FirebaseUser>
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG,"UserViewModel - onCleared() called")
    }

    // 회원가입
    fun register(email: String, userDto: UserDto, password: String) {
        Log.d(TAG,"User - register() called")
        userRepository.register(email, userDto, password)
    }

    // 로그인
    fun login(email: String, password: String) {
        Log.d(TAG,"User - login() called")
        userRepository.login(email, password)
    }

    // 아이디 찾기
    fun findUserId(name: String, email: String) {
        Log.d(TAG,"UserViewModel - findUserId() called")
        userRepository.findUserId(name, email)
    }
    
    // 비밀번호 찾기
    fun findUserPassword(email: String) {
        Log.d(TAG,"UserViewModel - findUserPassword() called")
    }
    
    // 중복 회원 찾기
    fun isExistEmail(email: String): Boolean {
        Log.d(TAG,"UserViewModel - isExistEmail() called")
        userRepository.isExistEmail(email)
        return true
    }

    // 회원 삭제
    fun withdrawUser(email: String, password: String) {
        Log.d(TAG,"UserViewModel - withdrawUser() called")
    }

    // 중복 로그인 처리
    fun autoLogin(firebaseUser: FirebaseUser) {
        _userLiveData.postValue(firebaseUser)
    }

    // 로그 아웃
    fun signOut() {
        Firebase.auth.signOut()
        _userLiveData.postValue(null)
    }
}
