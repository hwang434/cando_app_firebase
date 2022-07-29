package com.goodee.cando_app.dto

import java.io.Serializable

data class UserDto(
    val email: String,
    val name: String = "김덕배",
    val phone: String
): Serializable
