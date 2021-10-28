package com.goodee.cando_app.views

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.goodee.cando_app.R
import com.goodee.cando_app.databinding.FragmentDiaryViewBinding
import com.goodee.cando_app.dto.DiaryDto
import com.goodee.cando_app.viewmodel.DiaryViewModel

class DiaryViewFragment : Fragment() {
    private val TAG: String = "로그"
    private lateinit var binding: FragmentDiaryViewBinding
    private lateinit var dno: String
    private lateinit var diaryViewModel: DiaryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG,"DiaryViewFragment - onCreate() called\n arguments : ${arguments.toString()}")
        diaryViewModel = DiaryViewModel(requireActivity().application)
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
        diaryViewModel.diaryLiveData.observe(viewLifecycleOwner, Observer { diaryDto ->
            binding.textviewDiaryviewTitleview.text = diaryDto.title
            binding.textviewDiaryviewContentview.text = diaryDto.content
        })

        return binding.root
    }
}