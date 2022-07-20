package com.goodee.cando_app.views.diary

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.goodee.cando_app.R
import com.goodee.cando_app.databinding.FragmentDiaryViewBinding
import com.goodee.cando_app.viewmodel.DiaryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DiaryViewFragment : Fragment() {
    companion object {
        private const val TAG: String = "로그"
    }

    private lateinit var binding: FragmentDiaryViewBinding
    private lateinit var dno: String
    private val diaryViewModel: DiaryViewModel by lazy {
        ViewModelProvider(this).get(DiaryViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 번호가 전달이 안됐으면 이전 페이지로 이동
        when (arguments) {
            null -> {
                findNavController().navigateUp()
            }
        }

        // 전달이 됐으면 글을 조회
        dno = arguments?.get("dno").toString()
        lifecycleScope.launch(Dispatchers.IO) {
            val diaryDto = diaryViewModel.getDiary(dno)
            withContext(Dispatchers.Main) {
                when (diaryDto) {
                    null -> {
                        val alertDialog = AlertDialog.Builder(requireContext()).create()
                        alertDialog.setTitle("오류")
                        alertDialog.setMessage("이미 삭제 된 글입니다.")
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "확인") { _, _ ->
                            findNavController().navigateUp()
                        }
                    }
                    else -> {
                        binding.textviewDiaryviewTitleview.text = diaryDto.title
                        binding.textviewDiaryviewContentview.text = diaryDto.content
                        binding.textviewDiaryViewAuthorView.text = diaryDto.author
                        binding.progressbarDiaryviewLoading.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG,"DiaryViewFragment - onCreateView() called")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_diary_view, container, false)
        setEvent()

        return binding.root
    }

    private fun setEvent() {
        binding.buttonDiaryviewEditbutton.setOnClickListener {
            Log.d(TAG,"DiaryViewFragment - editButton is clicked.")
            findNavController().navigate(
                DiaryViewFragmentDirections.actionDiaryViewFragmentToDiaryWriteFragment(dno)
            )
        }
        binding.buttonDiaryviewDeletebutton.setOnClickListener {
            Log.d(TAG,"DiaryViewFragment - deleteButton is clicked")
            val dialog = DiaryDeleteDialogFragment(dno)
            dialog.show(requireActivity().supportFragmentManager, "쇼")
        }
    }
}