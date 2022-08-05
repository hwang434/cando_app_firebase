package com.goodee.cando_app.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.goodee.cando_app.dto.UserDto
import com.goodee.cando_app.model.UserRepository
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.QuerySnapshot

class UserViewModel(application: Application): AndroidViewModel(application) {
    companion object {
        private const val TAG: String = "로그"
    }
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
    suspend fun sendRegisterEmail(email: String, userDto: UserDto, password: String): Boolean {
        Log.d(TAG,"User - register() called")
        return userRepository.sendRegisterEmail(email, userDto, password)
    }

    // 로그인
    suspend fun login(email: String, password: String): Boolean {
        Log.d(TAG,"User - login() called")
        return userRepository.login(email, password)
    }

    // 아이디 찾기
    suspend fun findUserEmail(name: String, phone: String): QuerySnapshot {
        Log.d(TAG,"UserViewModel - findUserId() called")
        return userRepository.findUserEmail(name, phone)
    }
    
    // 비밀번호 찾기
    suspend fun isExistNameAndEmail(name: String, email: String): Boolean {
        Log.d(TAG,"UserViewModel - isExistNameAndEmail() called")
        return userRepository.isExistNameAndEmail(name, email)
    }
    
    // 중복 회원 찾기
    suspend fun isExistEmail(email: String): Boolean {
        Log.d(TAG,"UserViewModel - isExistEmail() called")
        return userRepository.isExistEmail(email)
    }

    // 회원 삭제
    suspend fun withdrawUser(email: String, password: String): Boolean {
        Log.d(TAG,"UserViewModel - withdrawUser() called")
        return userRepository.withdrawUser(email, password)
    }

    // 중복 로그인 처리
    fun autoLogin(firebaseUser: FirebaseUser) {
        userRepository.autoLogin(firebaseUser)
    }

    // 로그 아웃
    fun signOut() {
        userRepository.signOut()
    }

    suspend fun sendPasswordResetEmail(email: String) : Boolean {
        return userRepository.sendPasswordResetEmail(email)
    }
}
