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
import java.lang.Exception

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
            binding.apply {
                textviewDiaryviewTitleview.text = diaryDto.title
                textviewDiaryviewContentview.text = diaryDto.content
                textviewDiaryViewAuthorView.text = diaryDto.author
                progressbarDiaryviewLoading.visibility = View.GONE
                scrollviewDiaryViewBoardRoot.visibility = View.VISIBLE

                // if : 로그인한 유저의 이메일과 게시자의 이메일이 일치 -> 글 수정, 삭제 버튼을 보여줌.
                if (FirebaseAuth.getInstance().currentUser?.email == diaryViewModel.diaryLiveData.value?.author) {
                    buttonDiaryviewEditbutton.visibility = View.VISIBLE
                    buttonDiaryviewDeletebutton.visibility = View.VISIBLE
                }

                // if : 좋아요를 눌렀으면 -> 좋아요 버튼이 색칠되어 있음
                if (diaryDto.favorites.contains(FirebaseAuth.getInstance().currentUser!!.uid)) {
                    buttonDiaryViewLikeButton.setImageResource(R.drawable.like_button)
                } else {
                    buttonDiaryViewLikeButton.setImageResource(R.drawable.unlike_button)
                }
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
            val aBuilder = AlertDialog.Builder(requireContext())
            aBuilder.run {
                setTitle("이 글을 정말 삭제하시겠습니까?")
                setMessage("삭제하려면 확인을 눌러주세요.")
                setPositiveButton("확인") { _, _ ->
                    Log.d(TAG,"DiaryDeleteDialogFragment - Dialog Positive Button is clicked")
                    lifecycleScope.launch(Dispatchers.IO) {
                        try {
                            if (diaryViewModel.deleteDiary(dno)) {
                                withContext(Dispatchers.Main) {
                                    findNavController().navigateUp()
                                }
                            }
                        } catch (e: Exception) {
                            Log.w(TAG, "setEvent: ", e)
                            withContext(Dispatchers.Main) {
                                AlertDialog.Builder(requireContext()).setTitle("에러").setMessage("글 삭제에 실패하였습니다.").create().show()
                            }
                        }
                    }
                }
                setNegativeButton("취소") { _, _ ->
                    Log.d(TAG,"DiaryDeleteDialogFragment - Dialog Negative Button is clicked")
                }
            }
            aBuilder.create().show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // When you leave fragment or navigate to next fragment.
        // It will clear ViewModel.
        activity?.viewModelStore?.clear()
    }

    private fun setEvent() {
        binding.apply {
            buttonDiaryviewEditbutton.setOnClickListener {
                findNavController().navigate(
                    DiaryViewFragmentDirections.actionDiaryViewFragmentToDiaryWriteFragment(dno)
                )
            }

            buttonDiaryviewDeletebutton.setOnClickListener {
                val dialog = DiaryDeleteDialogFragment(dno)
                dialog.show(requireActivity().supportFragmentManager, "쇼")
            }

            buttonDiaryViewLikeButton.setOnClickListener {
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                // if : 사용자가 좋아요를 전에 눌렀으면 -> 좋아요 취소 else : 좋아요 동작
                if (diaryViewModel.diaryLiveData.value?.favorites?.contains(uid) == true) {
                    unlike(diaryViewModel, uid)
                } else {
                    like(diaryViewModel, uid)
                }
            }
        }
    }

    // 좋아요 기능
    private fun like(diaryViewModel: DiaryViewModel, uid: String?) {
        binding.progressbarDiaryviewLoading.visibility = View.VISIBLE
        diaryViewModel.diaryLiveData.value?.apply {
            // if : 게시판을 읽지 못했거나, 로그인하지 않았으면 -> 종료
            if (uid == null) {
                binding.progressbarDiaryviewLoading.visibility = View.GONE
                return
            }

            lifecycleScope.launch(Dispatchers.IO) {
                val isSuccess = diaryViewModel.like(dno = dno, uid = FirebaseAuth.getInstance().currentUser!!.uid)
                withContext(Dispatchers.Main) {
                    // if : like is fail
                    if (!isSuccess) {
                        val alertDialog = AlertDialog.Builder(requireContext()).create()
                        alertDialog.setTitle("좋아요에 실패했습니다.")
                        alertDialog.setMessage("좋아요 기능 실패")
                        alertDialog.setMessage("확인인인")
                    }
                    binding.progressbarDiaryviewLoading.visibility = View.GONE
                }
            }
        }
    }

    // 좋아요 취소
    private fun unlike(diaryViewModel: DiaryViewModel, uid: String?) {
        binding.progressbarDiaryviewLoading.visibility = View.VISIBLE
        diaryViewModel.diaryLiveData.value?.apply {
            // if : 게시판을 읽지 못했거나, 로그인하지 않았으면 -> 종료
            if (uid == null) {
                binding.progressbarDiaryviewLoading.visibility = View.GONE
                return
            }

            lifecycleScope.launch(Dispatchers.IO) {
                val isSuccess = diaryViewModel.unlike(dno = dno, uid = FirebaseAuth.getInstance().currentUser!!.uid)
                withContext(Dispatchers.Main) {
                    // if : like is fail
                    if (!isSuccess) {
                        val alertDialog = AlertDialog.Builder(requireContext()).create()
                        alertDialog.setTitle("좋아요 취소에 실패했습니다.")
                        alertDialog.setMessage("좋아요 기능 실패")
                        alertDialog.setMessage("확인인인")
                    }
                    binding.progressbarDiaryviewLoading.visibility = View.GONE
                }
            }
        }
    }
}