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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.goodee.cando_app.R
import com.goodee.cando_app.adapter.DiaryRecyclerViewAdapter
import com.goodee.cando_app.databinding.FragmentDiaryBinding
import com.goodee.cando_app.dto.DiaryDto
import com.goodee.cando_app.viewmodel.DiaryViewModel
import com.goodee.cando_app.viewmodel.UserViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class DiaryFragment : Fragment() {
    private val TAG: String = "로그"
    private lateinit var binding: FragmentDiaryBinding
    private val diaryViewModel: DiaryViewModel by lazy { ViewModelProvider(this).get(DiaryViewModel::class.java) }
    private var backPressedTime = System.currentTimeMillis()

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.member_layout, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_menu_signout -> {
                Toast.makeText(
                    requireActivity(),
                    "${Firebase.auth.currentUser?.email}님 안녕히 가세요.",
                    Toast.LENGTH_SHORT
                ).show()
                val userViewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
                    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                        return UserViewModel(requireActivity().application) as T
                    }
                }).get(UserViewModel::class.java)
                userViewModel.signOut()

                findNavController().navigate(R.id.action_diaryFragment_to_mainFragment)
                return true
            }
            R.id.item_menu_myinfo -> {
                Toast.makeText(requireActivity(), "내 정보 클릭",Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_diaryFragment_to_memberWithdrawFragment)
                return true
            }
        }

        return false
    }

    // 2초 안에 2번 뒤로 누르면 어플리케이션 종료
    private val callback by lazy { object: OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (backPressedTime + 2000 > System.currentTimeMillis()) {
                requireActivity().finish()
            } else {
                Toast.makeText(requireActivity(),"앱 종료를 원하시면 뒤로 가기 버튼을 눌러주세요.", Toast.LENGTH_SHORT).show()
            }

            backPressedTime = System.currentTimeMillis()
        }
    }}

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG,"DiaryFragment - onAttach() called")
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG,"DiaryFragment - onCreate() called")
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG,"DiaryFragment - onCreateView() called")
        diaryViewModel.getDiaryList()
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_diary, container, false)
        diaryViewModel.diaryListLiveData.observe(viewLifecycleOwner) { listOfDiaryDto ->
            if (listOfDiaryDto != null) {
                setRecyclerView(diaryViewModel.diaryListLiveData)
                binding.progressbarDiaryLoading.visibility = View.GONE
            }
        }
        setEvent()

        return binding.root
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG,"DiaryFragment - onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG,"DiaryFragment - onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG,"DiaryFragment - onDestroy() called")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG,"DiaryFragment - onDestroyView() called")
    }
    
    override fun onDetach() {
        super.onDetach()
        Log.d(TAG,"DiaryFragment - onDetach() called")
        callback.remove()
    }

    private fun setRecyclerView(diaryLiveData: LiveData<List<DiaryDto>>) {
        Log.d(TAG,"DiaryFragment - setRecyclerView() called")
        val adapter = DiaryRecyclerViewAdapter(diaryLiveData)
        binding.recyclerviewDiaryDiarylist.adapter = adapter
        binding.recyclerviewDiaryDiarylist.layoutManager = LinearLayoutManager(requireActivity())
        binding.recyclerviewDiaryDiarylist.addOnScrollListener(
            object: RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    // When the scroll has arrived bottom of Entire scroll.
                    if (!binding.recyclerviewDiaryDiarylist.canScrollVertically(1)) {
                        Toast.makeText(requireActivity(), "Bottom",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }

    private fun setEvent() {
        binding.floatingDiaryWritediary.setOnClickListener {
            findNavController().navigate(R.id.action_diaryFragment_to_diaryWriteFragment)
        }
    }
}