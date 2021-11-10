package com.goodee.cando_app.views

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import com.goodee.cando_app.viewmodel.DiaryViewModel

class DiaryDeleteDialogFragment(val dno: String): DialogFragment() {
    private val TAG: String = "로그"
    private lateinit var diaryViewModel: DiaryViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        diaryViewModel = DiaryViewModel(requireActivity().application)
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alertDialogBuilder = requireActivity().let { it -> AlertDialog.Builder(it) }
        alertDialogBuilder.apply {
            setTitle("타이틀")
            setMessage("메시지")
            setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
                Log.d(TAG,"DiaryDeleteDialogFragment - dialog postiveButton is clicked")
                diaryViewModel.deleteDiary(dno)
            })
            setNegativeButton("취소", DialogInterface.OnClickListener { dialog, which ->
                Log.d(TAG,"DiaryDeleteDialogFragment - dialog negativeButton is clicked")
            })
        }
        return alertDialogBuilder.create()
    }
}