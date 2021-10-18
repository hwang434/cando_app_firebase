package com.goodee.cando_app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.goodee.cando_app.dto.DiaryDto
import com.goodee.cando_app.model.AppRepository
import com.google.firebase.auth.FirebaseUser

class DiaryViewModel(application: Application) : AndroidViewModel(application) {
    private var appRepository: AppRepository
    private val _diaryLiveData: MutableLiveData<List<DiaryDto>>
    val diaryLiveData: LiveData<List<DiaryDto>>
        get() = _diaryLiveData

    init {
        appRepository = AppRepository(application)
        _diaryLiveData = appRepository.diaryLiveData as MutableLiveData<List<DiaryDto>>
    }

    // 모든 게시글 불러오기
    fun getDiaryList() {
        appRepository.getDiaryList()
    }
    // 글 작성하기
    fun writeDiary(diaryDto: DiaryDto) {
        appRepository.writeDiary(diaryDto)
    }

}
