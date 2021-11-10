package com.goodee.cando_app.views

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.goodee.cando_app.R
import com.goodee.cando_app.databinding.FragmentDiaryViewBinding
import com.goodee.cando_app.viewmodel.DiaryViewModel

class DiaryViewFragment : Fragment() {
    private val TAG: String = "로그"
    private lateinit var binding: FragmentDiaryViewBinding
    private lateinit var dno: String
    private val diaryViewModel: DiaryViewModel by lazy {
        ViewModelProvider(this).get(DiaryViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG,"DiaryViewFragment - onCreate() called\n arguments : ${arguments.toString()}")
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
        diaryViewModel.diaryLiveData.observe(viewLifecycleOwner, { diaryDto ->
            Log.d(TAG,"DiaryViewFragment - diaryLivedata change")
            if (diaryDto != null) {
                binding.textviewDiaryviewTitleview.text = diaryDto.title
                binding.textviewDiaryviewContentview.text = diaryDto.content
                binding.textviewDiaryviewAuthorview.text = diaryDto.author
            }
        })
        setEvent()

        return binding.root
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG,"DiaryViewFragment - onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG,"DiaryViewFragment - onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG,"DiaryViewFragment - onDestroy() called")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG,"DiaryViewFragment - onDestroyView() called")
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