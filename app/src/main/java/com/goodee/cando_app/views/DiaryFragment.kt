package com.goodee.cando_app.views

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.goodee.cando_app.R
import com.goodee.cando_app.api.DiaryApi
import com.goodee.cando_app.api.DiaryService
import com.goodee.cando_app.databinding.FragmentDiaryBinding
import com.goodee.cando_app.dto.Diary
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DiaryFragment : Fragment() {
    private val TAG: String = "로그"
    private lateinit var diaryBinding: FragmentDiaryBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG,"DiaryFragment - onCreateView() called")
        diaryBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_diary, container, false)

        var adapter = DiaryRecyclerViewAdapter()
        test()
//        adapter.list = test()
        diaryBinding.recyclerviewDiaryDiarylist.adapter = adapter
        diaryBinding.recyclerviewDiaryDiarylist.layoutManager = LinearLayoutManager(requireActivity())

        diaryBinding.floatingDiaryWritediary.setOnClickListener {
            Toast.makeText(requireActivity(), "diary add button is clicked", Toast.LENGTH_SHORT).show()
        }
        return diaryBinding.root
    }

    fun test(): String? {
        var data: String? = null
        DiaryApi.retrofitService.get("디인코딩 키값"
            ,1
            ,10
            ,"json"
            ,108).enqueue(object: Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.d(TAG,"DiaryFragment - onResponse() called")
                data = response.body()
                Log.d(TAG,"DiaryFragment - ${data}")
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d(TAG,"DiaryFragment - onFailure() called")
                Log.d(TAG,"DiaryFragment - ${t.message}")
            }

        })

//        diaryRetrofit.create(DiaryService::class.java).get()
//            .enqueue(object: retrofit2.Callback<String> {
//                override fun onResponse(call: Call<String>, response: Response<String>) {
//                    Log.d(TAG,"DiaryFragment - onResponse() called")
//                }
//
//                override fun onFailure(call: Call<String>, t: Throwable) {
//                    Log.d(TAG,"DiaryFragment - onFailure() called")
//                    Log.d(TAG,"call : ${call}\n$t : ${t.message}")
//                }
//
//
//            })

        return data
    }
}