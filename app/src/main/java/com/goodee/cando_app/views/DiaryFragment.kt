package com.goodee.cando_app.views

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.goodee.cando_app.R
import com.goodee.cando_app.databinding.FragmentDiaryBinding
import com.goodee.cando_app.dto.DiaryDto
import com.goodee.cando_app.viewmodel.DiaryViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class DiaryFragment : Fragment() {
    private val TAG: String = "로그"
    private lateinit var diaryBinding: FragmentDiaryBinding
    private lateinit var callback: OnBackPressedCallback
    private var backPressedTime: Long? = null
    private val diaryViewModelViewModel: DiaryViewModel by lazy {DiaryViewModel(requireActivity().application)}

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG,"DiaryFragment - onCreate() called")
        super.onCreate(savedInstanceState)
        diaryViewModelViewModel.getDiaryList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG,"DiaryFragment - onCreateView() called")
        diaryBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_diary, container, false)
        diaryViewModelViewModel.diaryListLiveData.observe(viewLifecycleOwner, Observer { it ->
            Log.d(TAG,"DiaryFragment - Data is changed.")
            if (it != null) {
                setRecyclerView(diaryViewModelViewModel.diaryListLiveData)
            }
        })
        setEvent()
        setHasOptionsMenu(true)

        return diaryBinding.root
    }

    override fun onAttach(context: Context) {
        Log.d(TAG,"DiaryFragment - onAttach() called")
        super.onAttach(context)
        // 2초 내에 두번 뒤로가기 버튼 누르면 앱 종료
        callback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (backPressedTime != null && backPressedTime!! + 2000 > System.currentTimeMillis()) {
                    requireActivity().finish()
                } else {
                    Toast.makeText(requireActivity(),"앱 종료를 원하시면 뒤로 가기 버튼을 눌러주세요.", Toast.LENGTH_SHORT).show()
                }

                backPressedTime = System.currentTimeMillis()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        Log.d(TAG,"DiaryFragment - onDetach() called")
        super.onDetach()
        callback.remove()
    }

    private fun setRecyclerView(diaryLiveData: LiveData<List<DiaryDto>>) {
        val adapter = DiaryRecyclerViewAdapter(diaryLiveData)
        diaryBinding.recyclerviewDiaryDiarylist.adapter = adapter
        diaryBinding.recyclerviewDiaryDiarylist.layoutManager = LinearLayoutManager(requireActivity())
    }

    private fun setEvent() {
        diaryBinding.floatingDiaryWritediary.setOnClickListener {
            findNavController().navigate(R.id.action_diaryFragment_to_diaryWriteFragment)
        }

        diaryBinding.bottomnavigationDiaryBottommenu.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item_menu_myinfo -> {
                    true
                }
                R.id.item_menu_signout -> {
                    Toast.makeText(
                        requireActivity(),
                        "${Firebase.auth.currentUser!!.email}님 안녕히 가세요.",
                        Toast.LENGTH_SHORT
                    ).show()
                    Firebase.auth.signOut()
                    findNavController().navigate(R.id.action_diaryFragment_to_mainFragment)
                    true
                }
                else -> {
                    false
                }
            }
        }
    }
}