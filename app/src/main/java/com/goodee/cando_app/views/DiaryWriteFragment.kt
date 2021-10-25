package com.goodee.cando_app.views

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.navigation.fragment.findNavController
import com.goodee.cando_app.R
import com.goodee.cando_app.dto.DiaryDto
import com.goodee.cando_app.viewmodel.DiaryViewModel
import com.google.firebase.auth.FirebaseAuth
import java.lang.Exception
import java.text.SimpleDateFormat

class DiaryWriteFragment : Fragment() {
    private val TAG: String = "로그"
    private lateinit var writeButton: Button
    private lateinit var contentView: EditText
    private lateinit var titleInputView: EditText
    private lateinit var diaryViewModel: DiaryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG,"DiaryWriteFragment - onCreate() called")
        super.onCreate(savedInstanceState)
        diaryViewModel = DiaryViewModel(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG,"DiaryWriteFragment - onCreateView() called")
        val view = inflater.inflate(R.layout.fragment_diary_write, container, false)
        writeButton = view.findViewById<Button>(R.id.button_diarywrite_writebutton)
        titleInputView = view.findViewById<EditText>(R.id.edittext_diarywrite_titleinput)
        contentView = view.findViewById<EditText>(R.id.edittext_diarywrite_contentinput)
        setEvent()

        return view
    }

    private fun setEvent() {
        Log.d(TAG,"DiaryWriteFragment - setEvent() called")
        writeButton?.setOnClickListener {
            Log.d(TAG,"DiaryWriteFragment - writeButton is clicked.")
            val title = titleInputView.text.toString()
            val content = contentView.text.toString()
            val userEmail = FirebaseAuth.getInstance().currentUser?.email.toString()
            val time = System.currentTimeMillis()
            val diaryDto = DiaryDto(dno = "", title = title, content = content, author = userEmail, date = time)
            diaryViewModel.writeDiary(diaryDto)

            findNavController().navigate(R.id.action_diaryWriteFragment_to_diaryFragment)
        }
    }
}