package com.goodee.cando_app.util

import java.util.regex.Pattern

object RegexChecker {
    fun isValidEmail(email: String): Boolean {
        val regex = "[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?"
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

    fun isValidPhone(number: String): Boolean {
        val regex = "^\\s*(?:\\+?(\\d{1,3}))?([-. (]*(\\d{3})[-. )]*)?((\\d{3})[-. ]*(\\d{2,4})(?:[-.x ]*(\\d+))?)\\s*\$"
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(number)
        return matcher.matches()
    }

    fun isValidPassword(password: String): Boolean {
        // 8~15자, 대문자 1개 소문자 1개 숫자1개 필수, 특수문자 가능
        val regex = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,15}\$"
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(password)
        return matcher.matches()
    }
}