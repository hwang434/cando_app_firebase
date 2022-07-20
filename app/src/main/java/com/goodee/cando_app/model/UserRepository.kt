package com.goodee.cando_app.model

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.goodee.cando_app.dto.UserDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class UserRepository(val application: Application) {
    companion object {
        private const val TAG: String = "로그"
        private const val USER_COLLECTION = "user"
    }
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _userLiveData: MutableLiveData<FirebaseUser> = MutableLiveData()
    val userLiveData: LiveData<FirebaseUser>
        get() = _userLiveData

    // 회원가입
    suspend fun register(email: String, userDto: UserDto, password: String): Boolean {
        Log.d(TAG,"AppRepository - register() called")
        try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            if (authResult.user != null) {
                FirebaseFirestore.getInstance().collection("$USER_COLLECTION").add(userDto).await()
                _userLiveData.postValue(firebaseAuth.currentUser)
                return true
            }
        } catch (e: Exception) {
            throw e
        }

        return false
    }

    // Firebase Authentication 로그인
    suspend fun login(email: String, password: String): Boolean {
        Log.d(TAG,"AppRepository - login() called")
        val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        if (authResult.user != null) {
            _userLiveData.postValue(authResult.user)
            return true
        }

        return false
    }

    // 유저 아이디 찾기
    suspend fun findUserEmail(name: String, phone: String): QuerySnapshot {
        Log.d(TAG, "AppRepository - findUserId() called")
        val firebaseDatabase = FirebaseFirestore.getInstance().collection(USER_COLLECTION)

        return firebaseDatabase.whereEqualTo("name", name).whereEqualTo("phone", phone).get()
            .await()
    }

    suspend fun isExistEmail(email: String): Boolean {
        Log.d(TAG,"UserRepository - isExistEmail() called")
        val result = firebaseAuth.fetchSignInMethodsForEmail(email).await()

        // null이 아니고 비지 않았으면 존재하지 않는 이메일이므로 true를 리턴
        return result?.signInMethods != null && result.signInMethods!!.isNotEmpty()
    }

    suspend fun isExistNameAndEmail(name: String, email: String): Boolean {
        val firebaseDatabase = FirebaseFirestore.getInstance().collection(USER_COLLECTION)
        Log.d(TAG,"UserRepository - ${firebaseDatabase.whereEqualTo("name", name).whereEqualTo("email", email).get().await().documents}")
        return !firebaseDatabase.whereEqualTo("name", name).whereEqualTo("email", email).get().await().isEmpty
    }

    suspend fun sendPasswordResetEmail(email: String): Boolean {
        val result = FirebaseAuth.getInstance().sendPasswordResetEmail(email)
        result.await()
        return result.isSuccessful
    }
}