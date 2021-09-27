package com.goodee.cando_app.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.goodee.cando_app.R
import com.goodee.cando_app.databinding.FragmentFindIdBinding

class FindIdFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentFindIdBinding>(inflater, R.layout.fragment_find_id, container, false)

        binding.buttonFindidSubmit.setOnClickListener {
            if (binding.edittextFindidNameinput.text.isNullOrEmpty() || binding.edittextFindidNameinput.text.isBlank()) {
                Toast.makeText(requireActivity(), "아이디를 입력해주세요", Toast.LENGTH_SHORT).show()
            } else if (binding.edittextFindidEmailinput.text.isNullOrEmpty() || binding.edittextFindidEmailinput.text.isBlank()) {
                Toast.makeText(requireActivity(), "이메일을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            // 아이디랑 이메일 입력을 했고,
//            else {
//                if (이름과 이메일이 일치하는 회원이 존재) {
//                     아이디를 리턴
//                }
//                이름과 이메일이 일치하는 회원이 존재하지 않음.
//                else {
//                    리턴할 거 없음.
//                }
//            }
        }

        return binding.root
    }
}