package com.goodee.cando_app.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.goodee.cando_app.dto.DiaryDto
import com.goodee.cando_app.model.DiaryRepository
import com.goodee.cando_app.util.Resource
import kotlinx.coroutines.*

class DiaryViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val TAG: String = "로그"
    }

    private val _diaryListLiveData: MutableLiveData<Resource<List<DiaryDto>>> = MutableLiveData()
    val diaryListLiveData: LiveData<Resource<List<DiaryDto>>>
        get() = _diaryListLiveData

    private val _diaryLiveData: MutableLiveData<Resource<DiaryDto>> = MutableLiveData()
    val diaryLiveData: LiveData<Resource<DiaryDto>>
        get() = _diaryLiveData

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG,"DiaryViewModel - onCleared() called")
    }

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

    // 글 삭제하기
    fun deleteDiary(dno: String) {
        Log.d(TAG,"DiaryViewModel - deleteDiary() called")
        viewModelScope.launch(Dispatchers.IO) { DiaryRepository.deleteDiary(dno) }
    }

    // refresh Diary List live data from Firestore.
    fun refreshDiaryList() {
        Log.d(TAG,"DiaryViewModel - refreshDiaryList() called")
        val handler = CoroutineExceptionHandler { _, throwable ->
            Log.w(TAG, "refreshDiaryList: ", throwable)
            _diaryListLiveData.postValue(Resource.Error(null, "System has a Error."))
        }
        _diaryListLiveData.postValue(Resource.Loading())

        viewModelScope.launch(Dispatchers.IO + handler){
            val diaryList = DiaryRepository.refreshDiaryList()
            _diaryListLiveData.postValue(Resource.Success(diaryList))
        }
    }

    fun deleteAllDiary(email: String, password: String) {
        Log.d(TAG,"DiaryViewModel - deleteAllDiary() called")
        viewModelScope.launch(Dispatchers.IO) {
            DiaryRepository.deleteAllDiary(email, password)
        }
    }
}
