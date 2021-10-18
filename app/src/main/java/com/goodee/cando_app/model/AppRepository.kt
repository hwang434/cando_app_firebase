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
import com.goodee.cando_app.R
import com.goodee.cando_app.database.RealTimeDatabase
import com.goodee.cando_app.dto.DiaryDto
import com.goodee.cando_app.viewmodel.Diary
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import kotlinx.coroutines.coroutineScope
import org.json.JSONObject

class AppRepository(val application: Application) {
    private val TAG: String = "로그"
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _userLiveData: MutableLiveData<FirebaseUser> = MutableLiveData()
    val userLiveData: LiveData<FirebaseUser>
        get() = _userLiveData
    private val _diaryLivedata: MutableLiveData<List<DiaryDto>> = MutableLiveData()
    val diaryLiveData: LiveData<List<DiaryDto>>
        get() = _diaryLivedata

    fun getDiaryList() {
        Log.d(TAG,"AppRepository - getDiaryList() called")

        RealTimeDatabase.getDatabase().child("Diary").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val map = task.result?.value as Map<String, Map<String, String>>

                val diaryList = mutableListOf<DiaryDto>()
                val list = map.values
                list.forEach { it ->
                    val author = it.get("author")
                    val title = it.get("title")
                    val content = it.get("content")
                    if (title != null && content != null && author != null) {
                        diaryList.add(DiaryDto(title, content, author))
                    }
                }

                _diaryLivedata.postValue(diaryList)
            }
        }
    }

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
            _userLiveData.postValue(firebaseAuth.currentUser)
        }
    }

    fun writeDiary(diaryDto: DiaryDto) {
        Log.d(TAG,"AppRepository - writeDiary() called")
        val key = RealTimeDatabase.getDatabase().child("Diary").push().key
        val diary = HashMap<String, DiaryDto>()
        if (key == null) {
            diary.put("1", diaryDto)
        } else {
            diary.put(key, diaryDto)
        }

        val firebaseDatabase = RealTimeDatabase.getDatabase()
        firebaseDatabase.child("Diary/${key}").setValue(diaryDto).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG,"AppRepository - writeDiary() is Sueccessful.")
            }
            else
                Log.d(TAG,"AppRepository - writeDiary() is Fail.")
        }

    }
}