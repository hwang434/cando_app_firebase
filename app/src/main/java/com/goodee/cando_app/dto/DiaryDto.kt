package com.goodee.cando_app.dto

import java.io.Serializable


data class DiaryDto(val dno: String?, val title: String, val content: String, val author: String, val date: Long) : Serializable