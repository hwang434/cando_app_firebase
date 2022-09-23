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

class DiaryWriteViewModel: ViewModel() {
    companion object {
        private const val TAG: String = "로그"
    }

    private val diaryRepository = DiaryRepository
    private val _diaryLiveData: MutableLiveData<Resource<DiaryDto>> = MutableLiveData()
    val diaryLiveData: LiveData<Resource<DiaryDto>>
        get() = _diaryLiveData

    private val _isWriteDone: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    val isWriteDone: LiveData<Resource<Boolean>>
        get() = _isWriteDone

    // 글 작성하기
    fun writeDiary(diaryDto: DiaryDto) {
        Log.d(TAG,"DiaryViewModel - writeDiary() called")
        val handler = CoroutineExceptionHandler { _, error ->
            Log.w(TAG, "writeDiary: ", error)
            _isWriteDone.postValue(Resource.Error(null, "System has a Error"))
        }

        viewModelScope.launch(Dispatchers.IO + handler) {
            if (diaryRepository.writeDiary(diaryDto)) {
                _isWriteDone.postValue(Resource.Success(true))
            } else {
                _isWriteDone.postValue(Resource.Error(false, "Fail to write Diary."))
            }
        }
    }

    // 글 수정하기
    fun editDiary(diaryDto: DiaryDto) {
        Log.d(TAG,"DiaryViewModel - editDiary(${diaryDto.dno}) called")
        val handler = CoroutineExceptionHandler { _, error ->
            Log.w(TAG, "writeDiary: ", error)
            _isWriteDone.postValue(Resource.Error(null, "System has a Error"))
        }

        viewModelScope.launch(Dispatchers.IO + handler) {
            if (diaryRepository.editDiary(diaryDto)) {
                _isWriteDone.postValue(Resource.Success(true))
            } else {
                _isWriteDone.postValue(Resource.Error(false, "Fail to edit Diary."))
            }
        }
    }

    fun refreshDiaryLiveData(dno: String) {
        Log.d(TAG,"DiaryWriteViewModel - refreshDiaryLiveData() called")
        _diaryLiveData.postValue(Resource.Loading())
        val handler = CoroutineExceptionHandler { _, error ->
            Log.w(TAG, "refreshDiaryLiveData: ", error)
            _isWriteDone.postValue(Resource.Error(null, "System has a Error"))
        }

        viewModelScope.launch(Dispatchers.IO + handler) {
            val diaryDto = DiaryRepository.refreshDiaryLiveData(dno)

            when (diaryDto) {
                null -> {
                    _diaryLiveData.postValue(Resource.Error(null, "Diary is not exist."))
                }
                else -> {
                    _diaryLiveData.postValue(Resource.Success(diaryDto))
                }
            }
        }
    }
}