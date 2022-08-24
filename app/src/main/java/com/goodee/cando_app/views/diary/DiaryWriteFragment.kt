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
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.goodee.cando_app.R
import com.goodee.cando_app.databinding.FragmentDiaryWriteBinding
import com.goodee.cando_app.dto.DiaryDto
import com.goodee.cando_app.viewmodel.DiaryViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.Exception

class DiaryWriteFragment : Fragment() {
    companion object {
        private const val TAG: String = "로그"
    }
    private lateinit var binding: FragmentDiaryWriteBinding
    private val diaryViewModel: DiaryViewModel by lazy { ViewModelProvider(this).get(DiaryViewModel::class.java) }


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG,"DiaryWriteFragment - onCreate() called")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG,"DiaryWriteFragment - onCreateView() called")
        setObserver()
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_diary_write, container, false)
        // if arguments is not null. refresh the diary editText.
        arguments?.let { bundle ->
            // 게시글 조회 후, 게시글 입력 화면 최신화
            try {
                diaryViewModel.refreshDiaryLiveData(bundle.get("dno").toString())
            } catch (e: Exception) {
                Log.w(TAG, "onCreateView: ", e)
                AlertDialog
                    .Builder(requireContext())
                    .setTitle("글 조회에 실패했습니다.")
                    .setMessage("나중에 다시 시도해주세요.")
                    .setPositiveButton("예") { _, _ ->
                        findNavController().navigateUp()
                    }
                    .create()
                    .show()
            }
        }
        setEvent()

        return binding.root
    }

    private fun setEvent() {
        Log.d(TAG,"DiaryWriteFragment - setEvent() called")
        binding.buttonDiarywriteWritebutton.setOnClickListener {
            writeDiary()
        }
    }

    private fun writeDiary() {
        Log.d(TAG,"DiaryWriteFragment - writeDiary() called")
        binding.apply {
            buttonDiarywriteWritebutton.isEnabled = false
            progressbarDiarywriteLoading.visibility = View.VISIBLE
        }

        val title = binding.edittextDiarywriteTitleinput.text.toString()
        val content = binding.edittextDiarywriteContentinput.text.toString()
        val author = FirebaseAuth.getInstance().currentUser?.email
        val date = System.currentTimeMillis()

        try {
            val newDiaryDto = DiaryDto(dno = requireArguments().get("dno").toString(), title = title, content = content, author = author.toString(), date = date)
            diaryViewModel.viewModelScope.launch(Dispatchers.IO) {
                // if : navigation gives dno, set init text box by given dno content.
                // else : 새로 글 작성이면 -> 새로운 글 작성
                if (arguments?.get("dno") != null) {
                    diaryViewModel.editDiary(newDiaryDto)
                } else {
                    diaryViewModel.writeDiary(newDiaryDto)
                }

                withContext(Dispatchers.Main) {
                    findNavController().navigate(R.id.action_diaryWriteFragment_to_diaryFragment)
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "setEvent: 글 수정, 작성 실패", e)
            AlertDialog.Builder(requireContext())
                .setTitle("글 작성에 실패했습니다.")
                .setMessage("잠시 후에 다시 시도해주세요.")
                .create()
                .show()

            binding.apply {
                buttonDiarywriteWritebutton.isEnabled = true
                progressbarDiarywriteLoading.visibility = View.GONE
            }
        }
    }

    private fun setObserver() {
        diaryViewModel.diaryLiveData.observe(viewLifecycleOwner) { diaryDto ->
            binding.apply {
                edittextDiarywriteTitleinput.setText(diaryDto.title)
                edittextDiarywriteContentinput.setText(diaryDto.content)
                progressbarDiarywriteLoading.visibility = View.GONE
            }
        }
    }
}