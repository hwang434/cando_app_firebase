package com.goodee.cando_app.api

import com.goodee.cando_app.api.DiaryService.Companion.retrofit
import com.goodee.cando_app.data.Weather
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface DiaryService {
    companion object {
        val BASE_URL = "http://apis.data.go.kr/1360000/VilageFcstMsgService/"
        private val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val retrofit = Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(BASE_URL)
            .build()
//            .addConverterFactory(ScalarsConverterFactory.create())

    }

    @GET("getWthrSituation")
    fun get(
        @Query("serviceKey") serviceKey: String,
        @Query("numOfRows") numOfRows: Int,
        @Query("pageNo") pageNo: Int,
        @Query("stnId") stnId: Int,
        @Query("dataType") dataType: String = "json"): Call<Weather>
}

object DiaryApi {
    val retrofitService : DiaryService by lazy {
        retrofit.create(DiaryService::class.java)
    }
}