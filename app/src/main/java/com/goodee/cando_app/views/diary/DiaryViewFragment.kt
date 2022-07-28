package com.goodee.cando_app.views.diary

import android.app.AlertDialog
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
import com.goodee.cando_app.databinding.FragmentDiaryViewBinding
import com.goodee.cando_app.viewmodel.DiaryViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DiaryViewFragment : Fragment() {
    companion object {
        private const val TAG: String = "로그"
    }

    private lateinit var binding: FragmentDiaryViewBinding
    private lateinit var dno: String
    private val diaryViewModel: DiaryViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "DiaryViewFragment - onCreate() called")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "DiaryViewFragment - onCreateView() called")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_diary_view, container, false)
        diaryViewModel.diaryLiveData.observe(viewLifecycleOwner) { diaryDto ->
            binding.textviewDiaryviewTitleview.text = diaryDto.title
            binding.textviewDiaryviewContentview.text = diaryDto.content
            binding.textviewDiaryViewAuthorView.text = diaryDto.author
            binding.progressbarDiaryviewLoading.visibility = View.GONE
            binding.scrollviewDiaryViewBoardRoot.visibility = View.VISIBLE
            binding.buttonDiaryViewLikeButton.text = diaryDto.favorites.size.toString()

            // if : 로그인한 유저의 이메일과 게시자의 이메일이 일치 -> 글 수정, 삭제 버튼을 보여줌.
            if (FirebaseAuth.getInstance().currentUser?.email == diaryViewModel.diaryLiveData.value?.author) {
                binding.buttonDiaryviewEditbutton.visibility = View.VISIBLE
                binding.buttonDiaryviewDeletebutton.visibility = View.VISIBLE
            }
        }

        // 전달이 됐으면 글을 조회
        dno = arguments?.get("dno").toString()
        lifecycleScope.launch(Dispatchers.IO) {
            val isSuccess = diaryViewModel.refreshDiaryLiveData(dno)
            withContext(Dispatchers.Main) {
                if (!isSuccess) {
                    val alertDialog = AlertDialog.Builder(requireContext()).create()
                    alertDialog.setTitle("오류")
                    alertDialog.setMessage("이미 삭제 된 글입니다.")
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "확인") { _, _ ->
                        findNavController().navigateUp()
                    }
                    alertDialog.show()
                }
            }
        }
        setEvent()

        return binding.root
    }

    private fun setEvent() {
        binding.buttonDiaryviewEditbutton.setOnClickListener {
            Log.d(TAG, "DiaryViewFragment - editButton is clicked.")
            findNavController().navigate(
                DiaryViewFragmentDirections.actionDiaryViewFragmentToDiaryWriteFragment(dno)
            )
        }
        binding.buttonDiaryviewDeletebutton.setOnClickListener {
            Log.d(TAG, "DiaryViewFragment - deleteButton is clicked")
            val dialog = DiaryDeleteDialogFragment(dno)
            dialog.show(requireActivity().supportFragmentManager, "쇼")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // When you leave fragment or navigate to next fragment.
        // It will clear ViewModel.
        activity?.viewModelStore?.clear()
    }
}