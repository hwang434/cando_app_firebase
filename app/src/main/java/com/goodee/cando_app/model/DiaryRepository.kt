package com.goodee.cando_app.model

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.goodee.cando_app.database.RealTimeDatabase
import com.goodee.cando_app.dto.DiaryDto
import com.google.firebase.database.*

class DiaryRepository(val application: Application) {
    private val TAG: String = "로그"
//    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _diaryListLiveData: MutableLiveData<List<DiaryDto>> = MutableLiveData()
    val diaryListLiveData: LiveData<List<DiaryDto>>
        get() = _diaryListLiveData

    private val _diaryLiveData: MutableLiveData<DiaryDto> = MutableLiveData()
    val diaryLiveData: LiveData<DiaryDto>
        get() = _diaryLiveData

    // 게시글 조회(게시글 클릭 시 1개의 게시글을 읽음)
    fun getDiary(dno: String) {
        Log.d(TAG,"AppRepository - getDiary() called")
        RealTimeDatabase.getDatabase().child("Diary/${dno}").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val map = mutableMapOf<String, String>()
                task.result?.children?.forEach { child ->
                    Log.d(TAG,"AppRepository - key : ${child.key} value : ${child.value}")
                    map[child.key.toString()] = child.value.toString()
                }

                val title = map["title"]
                val content = map["content"]
                val author = map["author"]
                val date = map["date"]
                if (title != null && content != null && author != null && date != null) {
                    val diaryDto = DiaryDto(dno = dno, title = title, content = content, author = author, date = date.toLong())
                    _diaryLiveData.value = diaryDto
                }
            }
        }
    }

    // 게시글 목록 가져오기(로그인시 바로 보이는 게시글들)
    fun getDiaryList() {
        Log.d(TAG,"AppRepository - getDiaryList() called")
        val rootRef = RealTimeDatabase.getDatabase().ref
        val diaryRef = rootRef.child("Diary")
        val query = diaryRef.orderByChild("dno").limitToLast(10)
        val valueEventListener = object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG,"AppRepository - onDataChange() called")
                val diaryList = mutableListOf<DiaryDto>()
                snapshot.children.forEach { ds ->
                    val dno = ds.key
                    val author = ds.child("author").value.toString()
                    val title = ds.child("title").value.toString()
                    val date = ds.child("date").value.toString().toLong()
                    diaryList.add(DiaryDto(dno = dno, title = title, content = "", author = author, date = date))
                }
                diaryList.reverse()
                _diaryListLiveData.postValue(diaryList)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG,"AppRepository - onCancelled() called")
            }
        }
        query.addListenerForSingleValueEvent(valueEventListener)
    }

    // 게시글 작성
    fun writeDiary(diaryDto: DiaryDto) {
        Log.d(TAG,"AppRepository - writeDiary() called")
        val diaryRef = RealTimeDatabase.getDatabase().child("Diary").ref
        val key = diaryRef.push().key
        val lastDno = diaryRef.limitToLast(1).get().addOnCompleteListener {
            Log.d(TAG,"AppRepository - it : ${it.result}")
        }
        if (!lastDno.isSuccessful) {
            Log.d(TAG,"AppRepository - diaryRef.limitToLast(1).get().isSuccessful : false")
            return
        }
        Log.d(TAG,"AppRepository - lastDno : ${lastDno.result}")

        val diary = HashMap<String, DiaryDto>()
        key?.let {
            diary.put(key, diaryDto)
        }

        val firebaseDatabase = RealTimeDatabase.getDatabase()
        firebaseDatabase.child("Diary/${key}").setValue(diaryDto).addOnCompleteListener { task ->       // Diary/${key}에 글 저장하기
            Log.d(TAG,"AppRepository - task.isSuccessful : ${task.isSuccessful}")
            if (task.isSuccessful) Toast.makeText(application, "글 작성 성공", Toast.LENGTH_SHORT).show()
            else Toast.makeText(application, "글 작성 실패", Toast.LENGTH_SHORT).show()
        }
    }

    // 게시글 수정하기
    fun editDiary(diaryDto: DiaryDto) {
        Log.d(TAG,"AppRepository - editDiary() called")
        val firebaseDatabase = RealTimeDatabase.getDatabase()
        val map = HashMap<String, Any>()
        map["title"] = diaryDto.title
        map["content"] = diaryDto.content
        map["author"] = diaryDto.author
        map["date"] = diaryDto.date
        Log.d(TAG,"AppRepository - map : $map")
        firebaseDatabase.child("Diary/${diaryDto.dno}").updateChildren(map).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG,"AppRepository - 글 수정 성공")
                _diaryLiveData.postValue(diaryDto)
            } else {
                Log.d(TAG,"AppRepository - 글 수정 실패")
            }
        }
    }

    fun deleteDiary(dno: String) {
        Log.d(TAG,"AppRepository - deleteDiary() called")
        RealTimeDatabase.getDatabase().child("Diary").child(dno).removeValue().addOnCompleteListener { task ->
            Log.d(TAG,"AppRepository - task.isSuccessful : ${task.isSuccessful}")
            _diaryLiveData.postValue(null)
        }
    }
}