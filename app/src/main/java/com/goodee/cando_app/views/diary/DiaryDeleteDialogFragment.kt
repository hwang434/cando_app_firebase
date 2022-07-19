package com.goodee.cando_app.views.diary

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.goodee.cando_app.viewmodel.DiaryViewModel

class DiaryDeleteDialogFragment(private val dno: String): DialogFragment() {
    companion object {
        private const val TAG: String = "로그"
    }
    private val diaryViewModel: DiaryViewModel by lazy {
        ViewModelProvider(this).get(DiaryViewModel::class.java)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alertDialogBuilder = requireActivity().let { AlertDialog.Builder(it) }
        alertDialogBuilder.apply {
            setTitle("이 글을 정말 삭제하시겠습니까?")
            setMessage("삭제하려면 확인을 눌러주세요.")
            setPositiveButton("확인") { _, _ ->
                Log.d(TAG, "DiaryDeleteDialogFragment - Dialog Positive Button is clicked")
                diaryViewModel.deleteDiary(dno)
                findNavController().navigateUp()
            }
            setNegativeButton("취소") { _, _ ->
                Log.d(TAG,"DiaryDeleteDialogFragment - dialog negativeButton is clicked")
            }
        }

        return alertDialogBuilder.create()
    }
}