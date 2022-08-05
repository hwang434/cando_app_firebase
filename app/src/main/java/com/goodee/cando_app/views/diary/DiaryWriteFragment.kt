package com.goodee.cando_app.views.diary

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.goodee.cando_app.R
import com.goodee.cando_app.databinding.FragmentDiaryWriteBinding
import com.goodee.cando_app.dto.DiaryDto
import com.goodee.cando_app.viewmodel.DiaryViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class DiaryWriteFragment : Fragment() {
    companion object {
        private const val TAG: String = "로그"
    }
    private lateinit var binding: FragmentDiaryWriteBinding
    private val diaryViewModel: DiaryViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG,"DiaryWriteFragment - onCreate() called")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG,"DiaryWriteFragment - onCreateView() called")
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_diary_write, container, false)
        arguments?.let { bundle ->
            // if : 글 조회에서 넘어왔으면, 글 수정이므로 이전 입력 내용을 입력해준다.
            if (bundle.get("dno") != null) {
                diaryViewModel.diaryLiveData.value?.let { dto ->
                    binding.apply {
                        edittextDiarywriteTitleinput.setText(dto.title)
                        edittextDiarywriteContentinput.setText(dto.content)
                        progressbarDiarywriteLoading.visibility = View.GONE
                    }
                }
            }
            binding.progressbarDiarywriteLoading.visibility = View.GONE
        }
        setEvent()

        return binding.root
    }

    private fun setEvent() {
        Log.d(TAG,"DiaryWriteFragment - setEvent() called")
        binding.buttonDiarywriteWritebutton.setOnClickListener {
            it.isEnabled = false
            writeDiary()
            it.isEnabled = true
        }
    }

    private fun writeDiary() {
        Log.d(TAG,"DiaryWriteFragment - writeDiary() called")
        binding.progressbarDiarywriteLoading.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.IO) {
            val title = binding.edittextDiarywriteTitleinput.text.toString()
            val content = binding.edittextDiarywriteContentinput.text.toString()
            val author = FirebaseAuth.getInstance().currentUser?.email
            val date = System.currentTimeMillis()

            try {
                val newDiaryDto = DiaryDto(dno = requireArguments().get("dno").toString(), title = title, content = content, author = author.toString(), date = date)
                // if : navigation gives dno, set init text box by given dno content.
                // else : 새로 글 작성이면 -> 새로운 글 작성
                if (arguments?.get("dno") != null) {
                    diaryViewModel.editDiary(newDiaryDto)
                } else {
                    // 새로 작성
                    diaryViewModel.writeDiary(newDiaryDto)
                }
            } catch (e: Exception) {
                Log.w(TAG, "setEvent: 글 수정, 작성 실패", e)
            }

            // 게시판 목록 페이지로 이동
            withContext(Dispatchers.Main) {
                findNavController().navigate(R.id.action_diaryWriteFragment_to_diaryFragment)
            }
        }
    }
}