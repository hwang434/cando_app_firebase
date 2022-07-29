package com.goodee.cando_app.dto

import java.io.Serializable


data class DiaryDto(
    var dno: String = "0",  // 글의 번호
    val title: String = "제목", // 글의 제목
    val content: String = "내용", // 글의 내용
    val author: String = "관리자", // 글 작성자
    val date: Long = 0, // 글 작성일
    val favorites: ArrayList<String> = ArrayList()// 좋아요 누른 유저 uid
) : Serializable