package com.goodee.cando_app.views

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.goodee.cando_app.R
import com.goodee.cando_app.databinding.FragmentDiaryViewBinding
import com.goodee.cando_app.viewmodel.DiaryViewModel
import com.goodee.cando_app.viewmodel.UserViewModel

class DiaryViewFragment : Fragment() {
    private val TAG: String = "로그"
    private lateinit var binding: FragmentDiaryViewBinding
    private lateinit var dno: String
    private val diaryViewModel: DiaryViewModel by lazy {
        ViewModelProvider(this).get(DiaryViewModel::class.java)
    }
    private val userViewModel by lazy {
        ViewModelProvider(requireActivity(), object: ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return UserViewModel(requireActivity().application) as T
            }
        }).get(UserViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dno = arguments?.get("dno").toString()
        diaryViewModel.getDiary(dno)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG,"DiaryViewFragment - onCreateView() called")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_diary_view, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        diaryViewModel.diaryLiveData.observe(viewLifecycleOwner) { diaryDto ->
            Log.d(TAG, "DiaryViewFragment - diaryLivedata change")
            if (diaryDto != null) {
                binding.textviewDiaryviewTitleview.text = "제목 : " + diaryDto.title
                binding.textviewDiaryviewContentview.text = diaryDto.content
                binding.textviewDiaryviewAuthorview.text = "작성자 : " + diaryDto.author
            }
        }
        setEvent()

        return binding.root
    }

    private fun setEvent() {
        binding.buttonDiaryviewEditbutton.setOnClickListener {
            Log.d(TAG,"DiaryViewFragment - editButton is clicked.")
            findNavController().navigate(DiaryViewFragmentDirections.actionDiaryViewFragmentToDiaryWriteFragment(dno))
        }
        binding.buttonDiaryviewDeletebutton.setOnClickListener {
            Log.d(TAG,"DiaryViewFragment - deleteButton is clicked")
//            diaryViewModel.deleteDiary(dno)
            val dialog = DiaryDeleteDialogFragment(dno)
            dialog.show(requireActivity().supportFragmentManager, "쇼")
        }
    }
}