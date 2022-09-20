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
    private var diaryRepository: DiaryRepository

    private val _diaryListLiveData: MutableLiveData<List<DiaryDto>>
    val diaryListLiveData: LiveData<List<DiaryDto>>
        get() = _diaryListLiveData

    private val _diaryLiveData: MutableLiveData<DiaryDto>
    val diaryLiveData: LiveData<DiaryDto>
        get() = _diaryLiveData

    private val _isWriteDone: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    val isWriteDone: LiveData<Resource<Boolean>>
        get() = _isWriteDone

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
    fun refreshDiaryLiveData(dno: String) {
        Log.d(TAG,"DiaryViewModel - refreshDiaryLiveData() called")
        viewModelScope.launch(Dispatchers.IO) { diaryRepository.refreshDiaryLiveData(dno) }
    }

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

    // 글 삭제하기
    fun deleteDiary(dno: String) {
        Log.d(TAG,"DiaryViewModel - deleteDiary() called")
        viewModelScope.launch(Dispatchers.IO) { diaryRepository.deleteDiary(dno) }
    }

    // refresh Diary List live data from Firestore.
    fun refreshDiaryList() {
        Log.d(TAG,"DiaryViewModel - refreshDiaryList() called")
        viewModelScope.launch(Dispatchers.IO){ diaryRepository.refreshDiaryList() }
    }

    // 좋아요 기능
    fun like(dno: String, uid: String) {
        Log.d(TAG,"DiaryViewModel - like() called")
        // if : 좋아요 성공하면
        viewModelScope.launch(Dispatchers.IO) { diaryRepository.like(dno, uid) }
    }

    // 좋아요 취소
    fun unlike(dno: String, uid: String) {
        Log.d(TAG,"DiaryViewModel - unlike() called")
        // if : 좋아요 취소 성공하면
        viewModelScope.launch(Dispatchers.IO) { diaryRepository.unlike(dno, uid) }
    }

    fun deleteAllDiary(email: String, password: String) {
        Log.d(TAG,"DiaryViewModel - deleteAllDiary() called")
        viewModelScope.launch(Dispatchers.IO) {
            diaryRepository.deleteAllDiary(email, password)
        }
    }
}
