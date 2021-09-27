package com.goodee.cando_app.api

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface LoginService {
    companion object {
        val BASE_URL = "http://localhost:8080/"
        fun create(): Retrofit {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit
        }
    }
    @POST("/members/login")
    fun login(@Query("id") id: String, @Query("passwrod") password: String): Call<Boolean>
}