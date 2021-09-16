package com.goodee.cando_app.api

import com.goodee.cando_app.api.DiaryService.Companion.retrofit
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface DiaryService {
//    companion object {
//        val BASE_URL = "http://apis.data.go.kr/1360000/VilageFcstMsgService/"
//
//        fun create(): Retrofit {
//            val retrofit = Retrofit.Builder()
//                .baseUrl(BASE_URL)
//                .build()
//            return retrofit
//        }
//    }
    companion object {
        val BASE_URL = "http://apis.data.go.kr/1360000/VilageFcstMsgService/"
        val retrofit = Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
    }


    @GET("getWthrSituation")
    fun get(
        @Query("serviceKey") serviceKey: String,
        @Query("pageNo") pageNo: Int,
        @Query("numOfRows") numOfRows: Int,
        @Query("dataType") dataType: String,
        @Query("stnId") stnId: Int
        ):Call<String>
}

object DiaryApi {
    val retrofitService : DiaryService by lazy {
        retrofit.create(DiaryService::class.java)
    }
}