package com.goodee.cando_app.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.goodee.cando_app.R
import com.goodee.cando_app.databinding.FragmentFindPasswordBinding

class FindPasswordFragment : Fragment() {
    lateinit var binding: FragmentFindPasswordBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_find_password, container, false)
        binding.buttonFindpasswordEmailbutton.setOnClickListener {
            if (binding.edittextFindpasswordIdinput.text.isNullOrEmpty() || binding.edittextFindpasswordIdinput.text.isBlank()) {
                Toast.makeText(requireActivity(), "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else if (binding.edittextFindpasswordEmailinput.text.isNullOrEmpty() || binding.edittextFindpasswordEmailinput.text.isBlank()) {
                Toast.makeText(requireActivity(), "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                // if 아이디와 이메일이 일치하는 회원이 존재함 -> 이메일 인증 시작, 인증화면 보이게
                binding.textviewFindpasswordEmailchecklabel.visibility = View.VISIBLE
                binding.edittextFindpasswordEmailcheckinput.visibility = View.VISIBLE
                // else -> 존재하지 않는 회원임.
            }
        }

        return binding.root
    }
}