package com.goodee.cando_app.model

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.goodee.cando_app.dto.UserDto
import com.goodee.cando_app.util.SocketLike
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class UserRepository(val application: Application) {
    companion object {
        private const val TAG: String = "로그"
        private const val USER_COLLECTION = "user"
    }
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _userLiveData: MutableLiveData<FirebaseUser> = MutableLiveData<FirebaseUser>()
    val userLiveData: LiveData<FirebaseUser>
        get() = _userLiveData

    init {
        _userLiveData.postValue(firebaseAuth.currentUser)
    }

    // 회원가입
    suspend fun sendRegisterEmail(email: String, userDto: UserDto, password: String): Boolean {
        Log.d(TAG,"UserRepository - sendRegisterEmail() called")
        // 파이어 베이스 회원 가입
        val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        Log.d(TAG,"UserRepository - authResult : $authResult")
        // if : 파이어 베이스 회원 가입 성공했으면
        if (authResult.user != null) {
            // 파이어스토어에 회원 정보를 저장
            val saveUserToDatabase = FirebaseFirestore.getInstance().collection(USER_COLLECTION).document(authResult!!.user!!.uid).set(userDto)
            saveUserToDatabase.await()
            // if : 회원 정보를 저장하는데 실패하면, 회원 탈퇴
            if (!saveUserToDatabase.isSuccessful) {
                firebaseAuth.currentUser?.delete()?.await()
                return false
            }

            // 회원 정보 저장 성공하면 인증 이메일을 보내고 로그아웃
            firebaseAuth.currentUser?.sendEmailVerification()?.await()
            firebaseAuth.signOut()
            return true
        }

        return false
    }

    // Firebase Authentication 로그인
    suspend fun login(email: String, password: String): Boolean {
        Log.d(TAG,"UserRepository - login() called")
        val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        if (authResult.user != null && authResult.user!!.isEmailVerified) {
            _userLiveData.postValue(authResult.user)
            // Socket 설정
            SocketLike.connectSocket()
            return true
        }

        firebaseAuth.signOut()
        return false
    }

    // 유저 아이디 찾기
    suspend fun findUserEmail(name: String, phone: String): QuerySnapshot {
        Log.d(TAG, "AppRepository - findUserId() called")
        val firebaseDatabase = FirebaseFirestore.getInstance().collection(USER_COLLECTION)
        return firebaseDatabase.whereEqualTo("name", name).whereEqualTo("phone", phone).get().await()
    }

    suspend fun isExistEmail(email: String): Boolean {
        Log.d(TAG,"UserRepository - isExistEmail() called")
        val result = firebaseAuth.fetchSignInMethodsForEmail(email).await()

        // If this email is already registered. return true.
        return result?.signInMethods != null && result.signInMethods!!.isNotEmpty()
    }

    suspend fun isExistNameAndEmail(name: String, email: String): Boolean {
        Log.d(TAG,"UserRepository - isExistNameAndEmail() called")
        val firebaseDatabase = FirebaseFirestore.getInstance().collection(USER_COLLECTION)
        return !firebaseDatabase.whereEqualTo("name", name).whereEqualTo("email", email).get().await().isEmpty
    }

    suspend fun sendPasswordResetEmail(email: String): Boolean {
        Log.d(TAG,"UserRepository - sendPasswordResetEmail() called")
        val result = firebaseAuth.sendPasswordResetEmail(email)
        result.await()
        return result.isSuccessful
    }

    fun autoLogin(firebaseUser: FirebaseUser) {
        Log.d(TAG,"UserRepository - autoLogin() called")
        _userLiveData.postValue(firebaseUser)
        SocketLike.connectSocket()
    }

    fun signOut() {
        Log.d(TAG,"UserRepository - signOut() called")
        Firebase.auth.signOut()
        _userLiveData.postValue(null)
        SocketLike.disconnectSocket()
    }

    suspend fun withdrawUser(email: String, password: String): Boolean {
        Log.d(TAG,"UserRepository - withdrawUser() called")
        val user = FirebaseAuth.getInstance().currentUser ?: return false
        val deleteUserInfo = FirebaseFirestore.getInstance().collection("user").document(user.uid).delete()
        deleteUserInfo.await()
        // if : 유저 정보를 지우는데 실패하면 메서드 종료
        if (!deleteUserInfo.isSuccessful) {
            return false
        }

        val credential = EmailAuthProvider.getCredential(email, password)
        val isAuthenticate = user.reauthenticate(credential)
        isAuthenticate.await()
        if (isAuthenticate.isSuccessful) {
            val deleteJob = user.delete()
            deleteJob.await()

            if (deleteJob.isSuccessful) {
                return true
            }
        }

        return false
    }
}