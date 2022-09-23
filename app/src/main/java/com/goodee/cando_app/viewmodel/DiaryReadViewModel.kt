package com.goodee.cando_app.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goodee.cando_app.dto.DiaryDto
import com.goodee.cando_app.model.DiaryRepository
import com.goodee.cando_app.util.Resource
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DiaryReadViewModel: ViewModel() {
    companion object {
        private const val TAG: String = "로그"
    }

    private val _diaryLiveData: MutableLiveData<Resource<DiaryDto>> = MutableLiveData()
    val diaryLiveData: LiveData<Resource<DiaryDto>>
        get() = _diaryLiveData

    // 게시글 1개 가져오기
    fun refreshDiaryLiveData(dno: String) {
        Log.d(TAG,"DiaryViewModel - refreshDiaryLiveData() called")
        val handler = CoroutineExceptionHandler { _, throwable ->
            Log.w(TAG, "refreshDiaryLiveData: ", throwable)
            _diaryLiveData.postValue(Resource.Error(null, "System has a error."))
        }
        _diaryLiveData.postValue(Resource.Loading())

        viewModelScope.launch(Dispatchers.IO + handler) {
            val diaryDto = DiaryRepository.refreshDiaryLiveData(dno)
            when (diaryDto) {
                null -> {
                    _diaryLiveData.postValue(Resource.Error(null, "There is No Diary."))
                }
                else -> {
                    _diaryLiveData.postValue(Resource.Success(diaryDto))
                }
            }
        }
    }

    // 좋아요 기능
    fun like(dno: String, uid: String) {
        Log.d(TAG,"DiaryViewModel - like() called")
        // if : 좋아요 성공하면
        viewModelScope.launch(Dispatchers.IO) { DiaryRepository.like(dno, uid) }
    }

    // 좋아요 취소
    fun unlike(dno: String, uid: String) {
        Log.d(TAG,"DiaryViewModel - unlike() called")
        // if : 좋아요 취소 성공하면
        viewModelScope.launch(Dispatchers.IO) { DiaryRepository.unlike(dno, uid) }
    }

    // 글 삭제하기
    fun deleteDiary(dno: String) {
        Log.d(TAG,"DiaryViewModel - deleteDiary() called")
        viewModelScope.launch(Dispatchers.IO) { DiaryRepository.deleteDiary(dno) }
    }
}