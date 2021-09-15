package com.goodee.cando_app.dto

import androidx.lifecycle.ViewModel

data class Diary(
    val num: String,
    val title: String,
    val writer: String,
    val writedDate: Long,
    val updatedDate: Long,
    val readCnt: Int): ViewModel() {

}
