package com.goodee.cando_app.model

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.goodee.cando_app.database.RealTimeDatabase
import com.goodee.cando_app.dto.UserDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class UserRepository(val application: Application) {
    private val TAG: String = "로그"
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
                RealTimeDatabase.getDatabase().child("Users/${firebaseAuth.currentUser?.uid}").setValue(userDto).await()
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
    fun findUserId(name: String, email: String) {
        Log.d(TAG,"AppRepository - findUserId() called")
        val firebaseDatabase = RealTimeDatabase.getDatabase().child("Users")
        firebaseDatabase.orderByChild("name").equalTo(name).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                val map = mutableMapOf<String, String>()
                snapshot?.children?.forEach { it ->
                    it.children.forEach { children ->
                        map[children.key!!] = children.value.toString()
                    }
                }
                val userName = map["name"]
                val userEmail = map["email"]
                val userId = map["id"]
                Log.d(TAG,"AppRepository - userName : $userName\nuserEmail : $userEmail\nuserId : $userId")

                if (userName.equals(name) && userEmail.equals(email)) {
                    Log.d(TAG,"AppRepository - $name $email")
                    Toast.makeText(application.applicationContext, "찾으시는 아이디는 ${userId}입니다.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(application.applicationContext, "일치하는 회원이 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun isExistEmail(email: String) {
        firebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {

            }
        }
    }
}