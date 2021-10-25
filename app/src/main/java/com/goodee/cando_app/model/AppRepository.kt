package com.goodee.cando_app.model

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.goodee.cando_app.database.RealTimeDatabase
import com.goodee.cando_app.dto.DiaryDto
import com.goodee.cando_app.dto.UserDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class AppRepository(val application: Application) {
    private val TAG: String = "로그"
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _userLiveData: MutableLiveData<FirebaseUser> = MutableLiveData()
    val userLiveData: LiveData<FirebaseUser>
        get() = _userLiveData

    private val _diaryListLiveData: MutableLiveData<List<DiaryDto>> = MutableLiveData()
    val diaryListLiveData: LiveData<List<DiaryDto>>
        get() = _diaryListLiveData

    private val _diaryLiveData: MutableLiveData<DiaryDto> = MutableLiveData()
    val diaryLiveData: LiveData<DiaryDto>
        get() = _diaryLiveData

    fun getDiaryList() {
        Log.d(TAG,"AppRepository - getDiaryList() called")
        val rootRef = RealTimeDatabase.getDatabase().ref
        val diaryRef = rootRef.child("Diary")
        val query = diaryRef.orderByChild("date")
        val valueEventListner = object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG,"AppRepository - onDataChange() called")
                val diaryList = mutableListOf<DiaryDto>()
                snapshot.children.forEach { it ->
                    val dno = it.key
                    val author = it.child("author").value.toString()
                    val title = it.child("title").value.toString()
                    val content = it.child("content").value.toString()
                    val date = it.child("date").getValue()

                    if (dno != null && title != null && content != null && author != null && date != null) {
                        diaryList.add(DiaryDto(dno = dno, title = title, content = content, author = author, date = date as Long))
                    }
                }
                diaryList.reverse()
                _diaryListLiveData.postValue(diaryList)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG,"AppRepository - onCancelled() called")
            }
        }
        query.addListenerForSingleValueEvent(valueEventListner)
    }

    fun register(userDto: UserDto) {
        Log.d(TAG,"AppRepository - register() called")

        firebaseAuth.createUserWithEmailAndPassword(userDto.email, userDto.password).addOnCompleteListener(ContextCompat.getMainExecutor(application.applicationContext)) { task ->
            if (task.isSuccessful) {
                val key = RealTimeDatabase.getDatabase().child("Users").push().key
                RealTimeDatabase.getDatabase().child("Users/${key}").setValue(userDto)
                _userLiveData.postValue(firebaseAuth.currentUser)
            }
            else Toast.makeText(application, "Register Fail.", Toast.LENGTH_SHORT).show()
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
        val key = RealTimeDatabase.getDatabase().child("Diary").push().key  // 주식별자 뽑기
        val diary = HashMap<String, DiaryDto>()
        if (key != null) diary.put(key, diaryDto)
        else throw Exception("AppRepository - writeDiary() key is null")

        val firebaseDatabase = RealTimeDatabase.getDatabase()
        firebaseDatabase.child("Diary/${key}").setValue(diaryDto).addOnCompleteListener { task ->       // Diary/${key}에 글 저장하기
            if (task.isSuccessful) Toast.makeText(application, "글 작성에 성공했습니다.", Toast.LENGTH_SHORT).show()
            else Toast.makeText(application, "글 작성에 실패했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    fun findUserId(name: String, email: String) {
        Log.d(TAG,"AppRepository - findUserId() called")
        val firebaseDatabase = RealTimeDatabase.getDatabase().child("Users")
        firebaseDatabase.orderByChild("name").equalTo(name).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                var userName: String? = null
                var userEmail: String? = null
                var userId: String? = null

                snapshot?.children?.forEach { it ->
                    it.children.forEach { children ->
                        if (children.key.equals("name")) userName = children.getValue(String::class.java)
                        else if (children.key.equals("email")) userEmail = children.getValue(String::class.java)
                        else if (children.key.equals("id")) userId = children.getValue(String::class.java)
                    }
                }
                Log.d(TAG, "findUserId: name : $name email : ${email}\n$userName : $userName userEmail : $userEmail")
                if (userName.equals(name) && userEmail.equals(email)) {
                    Log.d(TAG,"AppRepository - $name $email")
                    Toast.makeText(application.applicationContext, "찾으시는 아이디는 ${userId}입니다.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(application.applicationContext, "일치하는 회원이 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun getDiary(dno: String) {
        Log.d(TAG,"AppRepository - getDiary() called")
        RealTimeDatabase.getDatabase().child("Diary/${dno}").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val map = mutableMapOf<String, String>()
                task.result?.children?.forEach { child ->
                    Log.d(TAG,"AppRepository - key : ${child.key}")
                    Log.d(TAG,"AppRepository - value : ${child.value}")
                    map.put(child.key.toString(), child.value.toString())
                }

                val title = map.get("title")
                val content = map.get("content")
                val author = map.get("author")
                val date = map.get("date")
                if (dno != null && title != null && content != null && author != null && date != null) {
                    val diaryDto = DiaryDto(dno = dno, title = title, content = content, author = author, date = date.toLong())
                    _diaryLiveData.postValue(diaryDto)
                }
            }
        }
    }
}