package com.goodee.cando_app.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.goodee.cando_app.dto.DiaryDto
import com.goodee.cando_app.model.DiaryRepository

class DiaryViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val TAG: String = "로그"
    }
    private var diaryRepository: DiaryRepository
    private val _diaryListLiveData: MutableLiveData<List<DiaryDto>>
    val diaryListLiveData: LiveData<List<DiaryDto>>
        get() = _diaryListLiveData
    private val _diaryLiveData: MutableLiveData<DiaryDto>
    val diaryLiveData: LiveData<DiaryDto>
        get() = _diaryLiveData

    init {
        Log.d(TAG,"DiaryViewModel - init called")
        diaryRepository = DiaryRepository(application)
        _diaryListLiveData = diaryRepository.diaryListLiveData as MutableLiveData<List<DiaryDto>>
        _diaryLiveData = diaryRepository.diaryLiveData as MutableLiveData<DiaryDto>
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG,"DiaryViewModel - onCleared() called")
    }

    // 게시글 1개 가져오기
    suspend fun refreshDiaryLiveData(dno: String): Boolean {
        Log.d(TAG,"DiaryViewModel - refreshDiaryLiveData() called")
        return diaryRepository.refreshDiaryLiveData(dno)
    }

    // 글 작성하기
    suspend fun writeDiary(diaryDto: DiaryDto): Boolean {
        Log.d(TAG,"DiaryViewModel - writeDiary() called")
        return diaryRepository.writeDiary(diaryDto)
    }

    // 글 수정하기
    suspend fun editDiary(diaryDto: DiaryDto): Boolean {
        Log.d(TAG,"DiaryViewModel - editDiary() called")
        return diaryRepository.editDiary(diaryDto)
    }

    // 글 삭제하기
    suspend fun deleteDiary(dno: String): Boolean {
        Log.d(TAG,"DiaryViewModel - deleteDiary() called")
        return diaryRepository.deleteDiary(dno)
    }

    // refresh Diary List live data from Firestore.
    suspend fun refreshDiaryList(): Boolean {
        Log.d(TAG,"DiaryViewModel - refreshDiaryList() called")
        return diaryRepository.refreshDiaryList()
    }

    // 좋아요 기능
    suspend fun like(dno: String, uid: String): Boolean {
        Log.d(TAG,"DiaryViewModel - like() called")
        // if : 좋아요 성공하면
        if (diaryRepository.like(dno, uid)) {
            _diaryLiveData.postValue(diaryLiveData.value)
            return true
        }

        return false
    }

    // 좋아요 취소
    suspend fun unlike(dno: String, uid: String): Boolean {
        Log.d(TAG,"DiaryViewModel - unlike() called")
        // if : 좋아요 성공하면
        if (diaryRepository.unlike(dno, uid)) {
            _diaryLiveData.postValue(diaryLiveData.value)
            return true
        }

        return false
    }
}
