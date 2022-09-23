package com.goodee.cando_app.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.goodee.cando_app.dto.DiaryDto
import com.goodee.cando_app.util.SocketLike
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlin.Exception

object DiaryRepository {
    private const val TAG: String = "로그"
    private const val DIARY_COLLECTION = "diary"

    private val _diaryListLiveData: MutableLiveData<List<DiaryDto>> = MutableLiveData()
    val diaryListLiveData: LiveData<List<DiaryDto>>
        get() = _diaryListLiveData
    private val _diaryLiveData: MutableLiveData<DiaryDto> = MutableLiveData()
    val diaryLiveData: LiveData<DiaryDto>
        get() = _diaryLiveData


    // 게시글 조회(게시글 클릭 시 1개의 게시글을 읽음)
    suspend fun refreshDiaryLiveData(dno: String): DiaryDto? {
        Log.d(TAG,"DiaryRepository - refreshDiaryLiveData(dno : $dno)")
        val qResult = FirebaseFirestore.getInstance().collection("diary").whereEqualTo("dno", dno).get().await()
        // if : There is no document has a same diary and dno.
        if (qResult.isEmpty) {
            return null
        }

        return qResult.first().toObject(DiaryDto::class.java)
    }

    // 게시글 목록 가져오기(로그인시 바로 보이는 게시글들)
    suspend fun refreshDiaryList(): MutableList<DiaryDto> {
        Log.d(TAG,"DiaryRepository - refreshDiaryList() called")
        val qResult = FirebaseFirestore.getInstance().collection(DIARY_COLLECTION).orderBy("date").limitToLast(10).get().await()
        val diaryList = mutableListOf<DiaryDto>()
        qResult.documents.forEach { dSnapshot ->
            val diary = dSnapshot.toObject(DiaryDto::class.java)
            diary?.run {
                diaryList.add(DiaryDto(dno = dno,  title = title, author = author, date = date))
            }
        }

        return diaryList
    }

    // 게시글 작성
    suspend fun writeDiary(diaryDto: DiaryDto): Boolean {
        Log.d(TAG,"AppRepository - writeDiary(diaryDto : $diaryDto) called")
        diaryDto.dno = FirebaseFirestore.getInstance().collection(DIARY_COLLECTION).document().id
        val task = FirebaseFirestore.getInstance().collection(DIARY_COLLECTION).document(diaryDto.dno).set(diaryDto)

        task.await()
        return task.isSuccessful
    }

    // 게시글 수정하기
    suspend fun editDiary(diaryDto: DiaryDto): Boolean {
        Log.d(TAG,"AppRepository - editDiary(diaryDto : $diaryDto) called")
        val map = mutableMapOf<String, Any>()
        map["title"] = diaryDto.title
        map["content"] = diaryDto.content
        map["author"] = diaryDto.author
        map["date"] = diaryDto.date
        val task = FirebaseFirestore.getInstance().collection("diary").document(diaryDto.dno).update(map)

        task.await()
        return task.isSuccessful
    }

    suspend fun deleteDiary(dno: String) {
        Log.d(TAG,"AppRepository - deleteDiary(dno = $dno) called")
        val task = FirebaseFirestore.getInstance().collection("diary").document(dno).delete()
        task.await()

        if (!task.isSuccessful) {
            throw Exception("게시글 삭제 실패.")
        }
    }

    suspend fun like(dno: String, uid: String) {
        Log.d(TAG,"DiaryRepository - like($dno, $uid) called")
        val diaryRef = FirebaseFirestore.getInstance().collection(DIARY_COLLECTION).document(dno)
        var diaryDto: DiaryDto? = null

        val result = FirebaseFirestore.getInstance().runTransaction { transaction ->
            diaryDto = transaction.get(diaryRef).toObject(DiaryDto::class.java)
            diaryDto?.let {
                SocketLike.connectSocket()
                if (!diaryDto!!.favorites.contains(uid)) {
                    diaryDto!!.favorites.add(uid)
                    transaction.update(diaryRef, "favorites", diaryDto?.favorites)
                }
            }
        }

        result.await()
        if (!result.isSuccessful) {
            throw Exception("글 좋아요 실패")
        }

        // Send the user's email who liked the diary and the user's email who will receive like.
        val map = mutableMapOf<String, String>()
        // user who like this diary.
        map["sender"] = FirebaseAuth.getInstance().currentUser!!.email.toString()
        // user who wrote this diary.
        map["receiver"] = diaryDto!!.author
        SocketLike.emitData("like", map)

        _diaryLiveData.postValue(diaryDto)
    }

    suspend fun unlike(dno: String, uid: String) {
        Log.d(TAG,"DiaryRepository - unlike() called")
        val diaryRef = FirebaseFirestore.getInstance().collection(DIARY_COLLECTION).document(dno)
        var diaryDto: DiaryDto? = null

        val result = FirebaseFirestore.getInstance().runTransaction { transaction ->
            diaryDto = transaction.get(diaryRef).toObject(DiaryDto::class.java)
            diaryDto?.let {
                if (diaryDto!!.favorites.contains(uid)) {
                    diaryDto!!.favorites.remove(uid)
                    transaction.update(diaryRef, "favorites", diaryDto!!.favorites)
                }
            }
        }

        result.await()
        if (!result.isSuccessful) {
            throw Exception("좋아요 취소 실패")
        }

        _diaryLiveData.postValue(diaryDto)
    }

    suspend fun deleteAllDiary(): Boolean {
        Log.d(TAG,"DiaryRepository - deleteAllDiary() called")
        // 트랜잭션으로 유저 정보도 다 삭제해야함.
        val fs = FirebaseFirestore.getInstance()
        val email = FirebaseAuth.getInstance().currentUser?.email ?: return false

        // Read diary written by the user
        val docOfUserDiary = fs.collection("diary").whereEqualTo("author", email).get().await()
        // Delete Users diary then delete users info.
        val transaction = fs.runTransaction { transaction ->
            // 삭제해야할 유저 다이어리들
            val listOfDiaryRef = Array<DocumentReference?>(docOfUserDiary.size()) { null }
            docOfUserDiary.forEachIndexed { idx, diary ->
                listOfDiaryRef[idx] = fs.collection("diary").document(diary.toObject(DiaryDto::class.java).dno)
            }

            // 유저의 다이어리들 삭제
            listOfDiaryRef.forEach { documentReference ->
                documentReference?.let { it ->
                    transaction.delete(it)
                }
            }

            // 삭제할 유저 문서
            val userInfoRef = fs.collection("user").document(FirebaseAuth.getInstance().currentUser?.email.toString())
            transaction.delete(userInfoRef)
        }
        transaction.await()

        return transaction.isSuccessful
    }

    suspend fun deleteAllDiary(email: String, password: String): Boolean {
        Log.d(TAG,"DiaryRepository - deleteAllDiary() called")
        // 트랜잭션으로 유저 정보도 다 삭제해야함.
        val fs = FirebaseFirestore.getInstance()
        val isUserSame = FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
        isUserSame.await()

        // Read diary written by the user
        val docOfUserDiary = fs.collection("diary").whereEqualTo("author", email).get().await()
        // Delete Users diary then delete users info.
        val transaction = fs.runTransaction { transaction ->
            // 삭제해야할 유저 다이어리들
            val listOfDiaryRef = Array<DocumentReference?>(docOfUserDiary.size()) { null }
            docOfUserDiary.forEachIndexed { idx, diary ->
                listOfDiaryRef[idx] = fs.collection("diary").document(diary.toObject(DiaryDto::class.java).dno)
            }

            // 유저의 다이어리들 삭제
            listOfDiaryRef.forEach { documentReference ->
                documentReference?.let { it ->
                    transaction.delete(it)
                }
            }

            // 삭제할 유저 문서
            val userInfoRef = fs.collection("user").document(FirebaseAuth.getInstance().currentUser?.email.toString())
            transaction.delete(userInfoRef)
        }
        transaction.await()

        return transaction.isSuccessful
    }
}