package com.goodee.cando_app.views.diary

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.goodee.cando_app.R
import com.goodee.cando_app.databinding.FragmentDiaryViewBinding
import com.goodee.cando_app.util.Resource
import com.goodee.cando_app.viewmodel.DiaryReadViewModel
import com.goodee.cando_app.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlin.Exception

class DiaryReadFragment : Fragment() {
    companion object {
        private const val TAG: String = "로그"
    }

    private lateinit var binding: FragmentDiaryViewBinding
    private lateinit var dno: String
    private val diaryReadViewModel: DiaryReadViewModel by viewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private val userUid by lazy { userViewModel.userLiveData.value?.data }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "DiaryViewFragment - onCreate() called")

        if (!isSignIn()) {
            Toast.makeText(requireContext(), "User is not signed in.", Toast.LENGTH_SHORT).show()
            requireActivity().finish()
        }
    }

    private fun isSignIn(): Boolean {
        return userUid != null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "DiaryViewFragment - onCreateView() called")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_diary_view, container, false)

        // if : dno is Empty. then navigate up to DiaryFragment.
        dno = arguments?.get("dno").toString()
        if (dno.isEmpty()) {
            findNavController().navigateUp()
        }

        setObserver()
        setEvent()
        readDiary(dno)

        return binding.root
    }

    private fun setEvent() {
        binding.apply {
            buttonDiaryviewEditbutton.setOnClickListener {
                findNavController().navigate(
                    DiaryReadFragmentDirections.actionDiaryViewFragmentToDiaryWriteFragment(dno)
                )
            }

            buttonDiaryViewLikeButton.setOnClickListener {
                val uid = userUid.toString()
                // if : 사용자가 좋아요를 전에 눌렀으면 -> 좋아요 취소 else : 좋아요 동작
                if (diaryReadViewModel.diaryLiveData.value?.data?.favorites?.contains(uid) == true) {
                    unlike(uid)
                } else {
                    like(uid)
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
                    diaryReadViewModel.deleteDiary(dno)
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
    private fun like(uid: String) {
        binding.progressbarDiaryviewLoading.visibility = View.VISIBLE
        diaryReadViewModel.diaryLiveData.value?.apply {
            diaryReadViewModel.like(dno, uid)
        }
    }

    // 좋아요 취소
    private fun unlike(uid: String) {
        binding.progressbarDiaryviewLoading.visibility = View.VISIBLE
        diaryReadViewModel.diaryLiveData.value?.apply {
            diaryReadViewModel.unlike(dno = dno, uid = uid)
        }
    }

    // 라이브 데이터르 게시글 최신화
    private fun setObserver() {
        diaryReadViewModel.diaryLiveData.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    hideProgressBar()

                    val diaryDto = resource.data!!
                    binding.apply {
                        textviewDiaryviewTitleview.text = diaryDto.title
                        textviewDiaryviewContentview.text = diaryDto.content
                        textviewDiaryViewAuthorView.text = diaryDto.author
                        progressbarDiaryviewLoading.visibility = View.GONE
                        scrollviewDiaryViewBoardRoot.visibility = View.VISIBLE

                        // if : 로그인한 유저의 이메일과 게시자의 이메일이 일치 -> 글 수정, 삭제 버튼을 보여줌.
                        if (FirebaseAuth.getInstance().currentUser?.email == diaryDto.author) {
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

                is Resource.Error -> {
                    hideProgressBar()
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }
    }

    private fun showProgressBar() {
        binding.progressbarDiaryviewLoading.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressbarDiaryviewLoading.visibility = View.GONE
    }

    // 글을 조회하여 현재 페이지를 최신화
    private fun readDiary(dno: String) {
        diaryReadViewModel.refreshDiaryLiveData(dno)
    }
}