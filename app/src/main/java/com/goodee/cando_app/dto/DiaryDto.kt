package com.goodee.cando_app.dto

import java.io.Serializable


data class DiaryDto(val dno: String? = "0", val title: String = "제목", val content: String = "내용", val author: String = "관리자", val date: Long = 0) : Serializable