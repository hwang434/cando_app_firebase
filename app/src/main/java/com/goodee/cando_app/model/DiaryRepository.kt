package com.goodee.cando_app.model

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.goodee.cando_app.database.RealTimeDatabase
import com.goodee.cando_app.dto.DiaryDto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class DiaryRepository(val application: Application) {
    companion object {
        private const val TAG: String = "로그"
        private const val DIARY_COLLECTION = "diary"
    }

    private val _diaryListLiveData: MutableLiveData<List<DiaryDto>> = MutableLiveData()
    val diaryListLiveData: LiveData<List<DiaryDto>>
        get() = _diaryListLiveData
    private val _diaryLiveData: MutableLiveData<DiaryDto> = MutableLiveData()
    val diaryLiveData: LiveData<DiaryDto>
        get() = _diaryLiveData

    // 게시글 조회(게시글 클릭 시 1개의 게시글을 읽음)
    suspend fun refreshDiaryLiveData(dno: String): Boolean {
        Log.d(TAG,"AppRepository - getDiary() called")
        val qResult = FirebaseFirestore.getInstance().collection("diary").whereEqualTo("dno", dno).get().await()
        // if : There is no document has a same diary and dno.
        if (qResult.isEmpty) {
            return false
        }

        _diaryLiveData.postValue(qResult.first().toObject(DiaryDto::class.java))
        return true
    }

    // 게시글 목록 가져오기(로그인시 바로 보이는 게시글들)
    suspend fun refreshDiaryList(): Boolean {
        Log.d(TAG,"DiaryRepository - refreshDiaryList() called")
        val qResult = FirebaseFirestore.getInstance().collection(DIARY_COLLECTION).orderBy("date").limitToLast(10).get().await()
        val diaryList = mutableListOf<DiaryDto>()
        qResult.documents.forEach { dSnapshot ->
            Log.d(TAG,"DiaryRepository - dSnapshot.data : ${dSnapshot.data}")
            val diary = dSnapshot.toObject(DiaryDto::class.java)
            diary?.run {
                DiaryDto(dno = dno,  title = title, content = content, author = author, date = date)
                diaryList.add(this)
            }
        }
        _diaryListLiveData.postValue(diaryList)
        return true
    }

    // 게시글 작성
    suspend fun writeDiary(diaryDto: DiaryDto): Boolean {
        Log.d(TAG,"AppRepository - writeDiary() called")
        val task = FirebaseFirestore.getInstance().collection(DIARY_COLLECTION).document().set(diaryDto)

        task.await()
        if (!task.isSuccessful) {
            throw Exception("글 작성 실패")
        }
        return task.isSuccessful
    }

    // 게시글 수정하기
    suspend fun editDiary(diaryDto: DiaryDto): Boolean {
        Log.d(TAG,"AppRepository - editDiary() called")
        val map = mutableMapOf<String, Any>()
        map["title"] = diaryDto.title
        map["content"] = diaryDto.content
        map["author"] = diaryDto.author
        map["date"] = diaryDto.date
        val task = FirebaseFirestore.getInstance().collection("diary").document(diaryDto.dno).update(map)

        task.await()
        if (!task.isSuccessful) {
            throw Exception("글 수정 실패")
        }
        return task.isSuccessful
    }

    fun deleteDiary(dno: String) {
        Log.d(TAG,"AppRepository - deleteDiary() called")
        RealTimeDatabase.getDatabase().child("Diary").child(dno).removeValue().addOnCompleteListener { task ->
            Log.d(TAG,"AppRepository - task.isSuccessful : ${task.isSuccessful}")
            _diaryLiveData.postValue(null)
        }
    }
}