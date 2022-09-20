package com.goodee.cando_app.views.diary

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.goodee.cando_app.R
import com.goodee.cando_app.databinding.FragmentDiaryViewBinding
import com.goodee.cando_app.viewmodel.DiaryViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlin.Exception

class DiaryViewFragment : Fragment() {
    companion object {
        private const val TAG: String = "로그"
    }

    private lateinit var binding: FragmentDiaryViewBinding
    private lateinit var dno: String
    private val diaryViewModel: DiaryViewModel by lazy { ViewModelProvider(this).get(DiaryViewModel::class.java) }

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
        observeDiaryLiveData()

        // if : dno is Empty. then navigate up to DiaryFragment.
        dno = arguments?.get("dno").toString()
        if (dno.isEmpty()) {
            findNavController().navigateUp()
        }

        readDiary(dno)
        setEvent()
        return binding.root
    }

    private fun setEvent() {
        binding.apply {
            buttonDiaryviewEditbutton.setOnClickListener {
                findNavController().navigate(
                    DiaryViewFragmentDirections.actionDiaryViewFragmentToDiaryWriteFragment(dno)
                )
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

            buttonDiaryviewDeletebutton.setOnClickListener {
                deleteDiary()
            }
        }
    }

    // 글 삭제
    private fun deleteDiary() {
        Log.d(TAG,"DiaryViewFragment - deleteDiary() called")
        val aBuilder = AlertDialog.Builder(requireContext())

        aBuilder.run {
            setTitle(getString(R.string.alert_diary_view_title))
            setMessage(getString(R.string.alert_diary_view_message))
            setPositiveButton(getString(R.string.alert_diary_view_postive_button)) { _, _ ->
                try {
                    diaryViewModel.deleteDiary(dno)
                    findNavController().navigateUp()
                } catch (e: Exception) {
                    Log.w(TAG, "setEvent: delete diary fail.", e)
                    AlertDialog.Builder(requireContext()).setTitle(getString(R.string.alert_diary_view_fail_title)).setMessage(getString(R.string.alert_diary_view_fail_message)).create().show()
                }
            }
            setNegativeButton("취소") { _, _ -> }
        }

        aBuilder.create().show()
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

            try {
                diaryViewModel.like(dno = dno, uid = FirebaseAuth.getInstance().currentUser!!.uid)
            } catch (e: Exception) {
                Log.w(TAG, "like: ", e)
                val alertDialog = AlertDialog.Builder(requireContext()).create()
                alertDialog.apply {
                    setTitle(getString(R.string.alert_diary_view_like_fail_title))
                    setMessage(getString(R.string.alert_diary_view_error_message))
                }
            }
            binding.progressbarDiaryviewLoading.visibility = View.GONE
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

            try {
                diaryViewModel.unlike(dno = dno, uid = FirebaseAuth.getInstance().currentUser!!.uid)
            } catch (e: Exception) {
                Log.w(TAG, "unlike: ", e)
                val alertDialog = AlertDialog.Builder(requireContext()).create()
                alertDialog.apply {
                    setTitle("좋아요 취소에 실패했습니다.")
                    setMessage(getString(R.string.alert_diary_view_fail_message))
                }
            }

            binding.progressbarDiaryviewLoading.visibility = View.GONE
        }
    }

    // 라이브 데이터르 게시글 최신화
    private fun observeDiaryLiveData() {
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
                    buttonDiaryViewLikeButton.setMinAndMaxFrame(50, 100)
                    buttonDiaryViewLikeButton.playAnimation()
                } else {
                    buttonDiaryViewLikeButton.setMinAndMaxFrame(130, 150)
                    buttonDiaryViewLikeButton.playAnimation()
                }
            }
        }
    }

    // 글을 조회하여 현재 페이지를 최신화
    private fun readDiary(dno: String) {
        try {
            diaryViewModel.refreshDiaryLiveData(dno)
        } catch (e: Exception) {
            Log.w(TAG, "readDiary: ", e)
            val alertDialog = AlertDialog.Builder(requireContext()).create()
            alertDialog.apply {
                setTitle("오류")
                setMessage("이미 삭제 된 글입니다.")
                setButton(AlertDialog.BUTTON_NEUTRAL, "확인") { _, _ ->
                    findNavController().navigateUp()
                }
                show()
            }
        }
    }
}