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

    // 게시글 1개 가져오기
    suspend fun refreshDiaryLiveData(dno: String): Boolean {
        return diaryRepository.refreshDiaryLiveData(dno)
    }

    // 글 작성하기
    fun writeDiary(diaryDto: DiaryDto) {
        diaryRepository.writeDiary(diaryDto)
    }

    fun editDiary(diaryDto: DiaryDto) {
        diaryRepository.editDiary(diaryDto)
    }

    fun deleteDiary(dno: String) {
        diaryRepository.deleteDiary(dno)
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG,"DiaryViewModel - onCleared() called")
    }

    // refresh Diary List live data from Firestore.
    suspend fun refreshDiaryList(): Boolean {
        return diaryRepository.refreshDiaryList()
    }
}
