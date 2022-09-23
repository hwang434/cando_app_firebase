package com.goodee.cando_app.views.diary

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.goodee.cando_app.R
import com.goodee.cando_app.databinding.FragmentDiaryWriteBinding
import com.goodee.cando_app.dto.DiaryDto
import com.goodee.cando_app.util.Resource
import com.goodee.cando_app.viewmodel.DiaryWriteViewModel
import com.google.firebase.auth.FirebaseAuth

class DiaryWriteFragment : Fragment() {
    companion object {
        private const val TAG: String = "로그"
    }
    private lateinit var binding: FragmentDiaryWriteBinding
    private val diaryViewModel: DiaryWriteViewModel by viewModels()


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
            bundle.getString("dno")?.let {
                diaryViewModel.refreshDiaryLiveData(it)
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

        val newDiaryDto = DiaryDto(dno = requireArguments().get("dno").toString(), title = title, content = content, author = author.toString(), date = date)
        // if : navigation gives dno, set init text box by given dno content.
        // else : 새로 글 작성이면 -> 새로운 글 작성
        if (arguments?.get("dno") != null) {
            diaryViewModel.editDiary(newDiaryDto)
        } else {
            diaryViewModel.writeDiary(newDiaryDto)
        }
    }

    private fun setObserver() {
        diaryViewModel.diaryLiveData.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    hideProgressBar()

                    binding.apply {
                        resource.data?.apply {
                            edittextDiarywriteTitleinput.setText(title)
                            edittextDiarywriteContentinput.setText(content)
                        }
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }

                is Resource.Error -> {
                    hideProgressBar()
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        diaryViewModel.isWriteDone.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    hideProgressBar()
                    findNavController().navigate(R.id.action_diaryWriteFragment_to_diaryFragment)
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
                is Resource.Error -> {
                    hideProgressBar()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
            }
        }
    }

    private fun showProgressBar() {
        binding.progressbarDiarywriteLoading.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressbarDiarywriteLoading.visibility = View.GONE
    }
}