package com.goodee.cando_app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.goodee.cando_app.dto.DiaryDto
import com.goodee.cando_app.model.AppRepository
import com.google.firebase.auth.FirebaseUser

class Diary(application: Application) : AndroidViewModel(application) {
    private var appRepository: AppRepository
    private val _diaryLiveData: MutableLiveData<List<DiaryDto>>
    val diaryLiveData: LiveData<List<DiaryDto>>
        get() = _diaryLiveData

    init {
        appRepository = AppRepository(application)
        _diaryLiveData = appRepository.diaryLiveData as MutableLiveData<List<DiaryDto>>
    }

    fun getDiaryList() {
        appRepository.getDiaryList()
    }
    fun writeDiary(diaryDto: DiaryDto) {
        appRepository.writeDiary(diaryDto)
    }

}
