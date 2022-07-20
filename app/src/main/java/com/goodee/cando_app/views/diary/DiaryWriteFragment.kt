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
    ): View? {
        Log.d(TAG,"DiaryWriteFragment - onCreateView() called")
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_diary_write, container, false)
        arguments?.let { bundle ->
            // if : 글 조회에서 넘어왔으면, 입력란을 초기화 해준다.
            if (bundle.get("dno") != null) {
                diaryViewModel.diaryLiveData.value?.run {
                    binding.edittextDiarywriteTitleinput.setText(title)
                    binding.edittextDiarywriteContentinput.setText(content)
                    binding.progressbarDiarywriteLoading.visibility = View.GONE
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
            binding.progressbarDiarywriteLoading.visibility = View.VISIBLE

            lifecycleScope.launch(Dispatchers.IO) {
                val title = binding.edittextDiarywriteTitleinput.text.toString()
                val content = binding.edittextDiarywriteContentinput.text.toString()
                val author = FirebaseAuth.getInstance().currentUser?.email

                try {
                    val newDiaryDto = DiaryDto(dno = requireArguments().get("dno").toString(), title = title, content = content, author = author.toString())
                    // if : 네비게이션을 통해 dno를 받았으면(글 조회에서 넘어왔으면) -> 현재 글 수정
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
}