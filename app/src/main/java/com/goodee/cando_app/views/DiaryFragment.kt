package com.goodee.cando_app.views

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.goodee.cando_app.R
import com.goodee.cando_app.databinding.FragmentDiaryBinding


class DiaryFragment : Fragment() {
    private val TAG: String = "로그"
    private lateinit var diaryBinding: FragmentDiaryBinding
    private lateinit var callback: OnBackPressedCallback
    private var backPressedTime: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG,"DiaryFragment - onCreateView() called")
        diaryBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_diary, container, false)
        setRecyclerView()
        setClickListener()

        return diaryBinding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // 2초 내에 두번 뒤로가기 버튼 누르면 앱 종료
        callback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                if (backPressedTime != null && backPressedTime!! + 2000 > System.currentTimeMillis()) {
                    Toast.makeText(requireContext(), "if문",Toast.LENGTH_SHORT).show()
                    requireActivity().finish()
                }
                Toast.makeText(requireActivity(),"앱 종료를 원하시면 뒤로 가기 버튼을 눌러주세요.", Toast.LENGTH_SHORT).show()
                backPressedTime = System.currentTimeMillis()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }

    private fun setRecyclerView() {
        val adapter = DiaryRecyclerViewAdapter()
        diaryBinding.recyclerviewDiaryDiarylist.adapter = adapter
        diaryBinding.recyclerviewDiaryDiarylist.layoutManager = LinearLayoutManager(requireActivity())
    }

//    private fun test(adapter: DiaryRecyclerViewAdapter) {
//        var data: Weather? = null
//        var titleList: MutableList<Diary>? = null
//        DiaryApi.retrofitService.get(
//            "기상청_동네예보 통보문 조회서비스 디코딩키"
//            ,1
//            ,10
//            ,108
//            ,"JSON"
//        )
//        .enqueue(object: Callback<Weather> {
//            override fun onResponse(call: Call<Weather>, response: Response<Weather>) {
//                data = response.body()
//                Log.d(TAG,"DiaryFragment - onResponse() called")
//                Log.d(TAG,"DiaryFragment - ${data}")
//                println(data?.response?.body?.items!!.item[0].wfSv1)
//                titleList = mutableListOf()
//                data?.response?.body?.items!!.item[0].wfSv1.split("\n").forEach { it ->
//                    titleList!!.add(Diary(title = it.trim(), num="조회수",writer = "작성자", writedDate = 20020101, updatedDate = 20020101, readCnt = 120))
//                }
//
//                adapter.list = titleList as MutableList<Diary>
//                adapter.notifyDataSetChanged()
//            }
//
//            override fun onFailure(call: Call<Weather>, t: Throwable) {
//                Log.d(TAG,"DiaryFragment - onFailure() called")
//                Log.d(TAG,"DiaryFragment - ${t.message}")
//            }
//        })
//    }

    private fun setClickListener() {
        diaryBinding.floatingDiaryWritediary.setOnClickListener {
            Toast.makeText(requireActivity(), "diary add button is clicked", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_diaryFragment_to_diaryWriteFragment)
        }
    }
}