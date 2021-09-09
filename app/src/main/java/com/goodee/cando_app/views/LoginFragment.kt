package com.goodee.cando_app.views

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.goodee.cando_app.R
import com.goodee.cando_app.databinding.FragmentLoginBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    private val TAG: String = "로그"
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG,"LoginFragment - onCreateView() called")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login,container, false)

        // 로그인 버튼 이벤트처리
        binding.buttonLoginLoginbutton.setOnClickListener {
            Log.d(TAG,"LoginFragment - loginButton is activated")
            var imm: InputMethodManager?

            // 아이디가 비었거나 Blank거나 null일 때
            if (binding.textviewLoginIdinput.text.isNullOrBlank() || binding.textviewLoginIdinput.text.isEmpty()) {
                Toast.makeText(requireActivity(),"아이디를 확인해주세요.",Toast.LENGTH_SHORT).show()

                // 포커스가 되어 있는 키보드와 showSoftInput 메소드의 view가 일치해야 기보드가 뜹니다.
                // 포커스가 되어 있지 않으면 키보드가 뜨지 않습니다.
                binding.textviewLoginIdinput.requestFocus()
                imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(binding.textviewLoginIdinput,0)
                false
            } else if (binding.edittextLoginPasswordinput.text.isNullOrBlank() || binding.edittextLoginPasswordinput.text.isEmpty()) {
                Toast.makeText(requireActivity(),"비밀번호를 확인해주세요.",Toast.LENGTH_SHORT).show()

                binding.edittextLoginPasswordinput.requestFocus()
                imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(binding.edittextLoginPasswordinput,0)
                false
            } else {
                // 로그인 로직을 처리할 공간.
                Toast.makeText(requireActivity(),"아이디와 비밀번호가 모두 존재합니다.",Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_loginFragment_to_diaryFragment)
                true
            }
        }
        // 패스워트창에서 키보드에 엔터 누르면 실행되는 이벤트
        binding.edittextLoginPasswordinput.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                binding.edittextLoginPasswordinput.callOnClick()
                true
            }
            false
        }
        // 회원가입 버튼 누르면 회원 가입 프래그먼트로 이동
        binding.buttonLoginRegisterbutton.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_loginFragment_to_registerFragment)
        }

        return binding.root
    }
}