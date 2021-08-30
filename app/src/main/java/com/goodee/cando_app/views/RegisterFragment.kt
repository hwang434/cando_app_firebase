package com.goodee.cando_app.views

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.goodee.cando_app.R
import com.goodee.cando_app.databinding.FragmentRegisterBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [registerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class registerFragment : Fragment() {
    private val TAG: String = "로그"
    private lateinit var binding: FragmentRegisterBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false)

        // 회원 가입 버튼 눌렀을 시.
        binding.buttonRegisterRegisterbutton.setOnClickListener {
            if (binding.edittextRegisterIdinput.text.isNullOrEmpty() || binding.edittextRegisterIdinput.text.isNullOrEmpty()) {
                Toast.makeText(requireActivity(),"아이디를 확인해주세요",Toast.LENGTH_SHORT).show()
                binding.edittextRegisterIdinput.requestFocus()
                val imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(binding.edittextRegisterIdinput,0)
            } else if (binding.edittextRegisterPasswordinput.text.isNullOrEmpty() || binding.edittextRegisterPasswordinput.text.isNullOrEmpty()) {
                Toast.makeText(requireActivity(),"비밀번호를 확인해주세요",Toast.LENGTH_SHORT).show()
                binding.edittextRegisterPasswordinput.requestFocus()
                val imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(binding.edittextRegisterPasswordinput,0)
            } else if (binding.edittextRegisterPasswordcheckinput.text.isNullOrEmpty() || binding.edittextRegisterPasswordcheckinput.text.isNullOrBlank()) {
                Toast.makeText(requireActivity(),"비밀번호 확인 칸을 확인해주세요",Toast.LENGTH_SHORT).show()
                binding.edittextRegisterPasswordcheckinput.requestFocus()
                val imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(binding.edittextRegisterPasswordcheckinput,0)
            } else if (!binding.edittextRegisterPasswordinput.text.toString().equals(binding.edittextRegisterPasswordinput.text.toString())) {
                Toast.makeText(requireActivity(),"입력하신 비밀번호가 일치하지 않습니다",Toast.LENGTH_SHORT).show()
                binding.edittextRegisterPasswordinput.requestFocus()
                val imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(binding.edittextRegisterPasswordinput,0)
            } else if (binding.edittextRegisterNameinput.text.isNullOrEmpty() || binding.edittextRegisterNameinput.text.isNullOrBlank()) {
                Toast.makeText(requireActivity(),"이름을 확인해주세요",Toast.LENGTH_SHORT).show()
                binding.edittextRegisterNameinput.requestFocus()
                val imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(binding.edittextRegisterNameinput,0)
            } else if (binding.edittextRegisterEmailinput.text.isNullOrEmpty() || binding.edittextRegisterEmailinput.text.isNullOrBlank()) {
                Toast.makeText(requireActivity(),"이메일을 확인해주세요",Toast.LENGTH_SHORT).show()
                binding.edittextRegisterEmailinput.requestFocus()
                val imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(binding.edittextRegisterEmailinput, 0)
            }
            // 중복 체크됐는지 확인하는 기능 추가해줘야함.
        }

        // DuplicateCheckService로 해당하는 id를 가진 멤버 존재하는지 확인
        binding.buttonRegisterDuplicatecheck.setOnClickListener {

        }

        return binding.root
    }
}