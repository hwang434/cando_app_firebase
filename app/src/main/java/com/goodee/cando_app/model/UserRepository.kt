package com.goodee.cando_app.model

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.goodee.cando_app.database.RealTimeDatabase
import com.goodee.cando_app.dto.UserDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class UserRepository(val application: Application) {
    private val TAG: String = "로그"
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _userLiveData: MutableLiveData<FirebaseUser> = MutableLiveData()
    val userLiveData: LiveData<FirebaseUser>
        get() = _userLiveData

    // 회원가입
    fun register(email: String, userDto: UserDto, password: String) {
        Log.d(TAG,"AppRepository - register() called")
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
            ContextCompat.getMainExecutor(application.applicationContext)) { task ->
            Log.d(TAG,"AppRepository - register task.isSuccessful : ${task.isSuccessful}")
            if (task.isSuccessful) {
                RealTimeDatabase.getDatabase().child("Users/${firebaseAuth.currentUser?.uid}").setValue(userDto)
                _userLiveData.postValue(firebaseAuth.currentUser)
            } else {
                Toast.makeText(application, "Register Fail.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Firebase Authentication 로그인
    fun login(email: String, password: String) {
        Log.d(TAG,"AppRepository - login() called")
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener{ task ->
            Log.d(TAG,"AppRepository - register task.isSuccessful : ${task.isSuccessful}")
            when (task.isSuccessful) {
                true -> _userLiveData.postValue(firebaseAuth.currentUser)
                false -> _userLiveData.postValue(null)
            }
        }
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