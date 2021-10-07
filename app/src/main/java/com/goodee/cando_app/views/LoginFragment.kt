package com.goodee.cando_app.views

import android.app.ProgressDialog
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.goodee.cando_app.R
import com.goodee.cando_app.database.RealTimeDatabase
import com.goodee.cando_app.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    private val TAG: String = "로그"
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG,"LoginFragment - onCreateView() called")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login,container, false)

        return binding.root
    }

    private fun setEvent() {
        binding.buttonLoginFindmember.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_loginFragment_to_findMember)
        }
        // 로그인 버튼 이벤트처리
        binding.buttonLoginLoginbutton.setOnClickListener {
            Log.d(TAG,"LoginFragment - loginButton is activated")
            val imm: InputMethodManager?
            binding.progressbarLoginLoading.visibility = View.VISIBLE

            // 아이디가 비었거나 Blank거나 null일 때
            if (binding.textviewLoginIdinput.text.isNullOrBlank() || binding.textviewLoginIdinput.text.isEmpty()) {
                Toast.makeText(requireActivity(),"아이디를 확인해주세요.",Toast.LENGTH_SHORT).show()

                // 포커스가 되어 있는 키보드와 showSoftInput 메소드의 view가 일치해야 키보드가 뜹니다.
                // 포커스가 되어 있지 않으면 키보드가 뜨지 않습니다.
                binding.textviewLoginIdinput.requestFocus()
                imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(binding.textviewLoginIdinput,0)
            } else if (binding.edittextLoginPasswordinput.text.isNullOrBlank() || binding.edittextLoginPasswordinput.text.isEmpty()) {
                Toast.makeText(requireActivity(),"비밀번호를 확인해주세요.",Toast.LENGTH_SHORT).show()

                binding.edittextLoginPasswordinput.requestFocus()
                imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(binding.edittextLoginPasswordinput,0)
            } else {
                // 정규식을 만족했으므로 존재하는 데이터인지 확인.
                val database = RealTimeDatabase.getDatabase()
                database.child("users")
                    .child(binding.textviewLoginIdinput.text.toString())
                    .get()
                    .addOnSuccessListener { info ->
                        Log.i(TAG, "onCreateView:${info.child("password").value} ")
                        val password = info.child("password").value

                        if (password != null) {
                            if (password.equals(binding.edittextLoginPasswordinput.text.toString())) {
                                Toast.makeText(requireContext(), "${binding.textviewLoginIdinput.text.toString()}님 환영합니다.",Toast.LENGTH_SHORT).show()
                                findNavController().navigate(R.id.action_loginFragment_to_diaryFragment)
                            } else {
                                Toast.makeText(requireContext(), "비밀번호가 틀렸습니다.",Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(requireContext(), "존재하지 않는 회원입니다..",Toast.LENGTH_SHORT).show()
                        }

                        binding.progressbarLoginLoading.visibility = View.INVISIBLE
                    }
            }
        }

        // 키보드 엔터 누르면 클릭 이벤트 실행
        binding.edittextLoginPasswordinput.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                binding.edittextLoginPasswordinput.callOnClick()
                true
            }
            false
        }

        // 회원가입 페이지 이동
        binding.buttonLoginRegisterbutton.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }
}