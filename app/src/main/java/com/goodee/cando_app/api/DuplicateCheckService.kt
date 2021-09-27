package com.goodee.cando_app.api

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DuplicateCheckService {
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
//    @GET("/members")
//    fun isUserExist(@Query("id") id: String): Call<Boolean>
    @GET("/")
    fun isUserExist() : Call<Boolean>
}