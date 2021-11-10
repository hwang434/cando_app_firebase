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
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.goodee.cando_app.R
import com.goodee.cando_app.database.RealTimeDatabase
import com.goodee.cando_app.databinding.FragmentRegisterBinding
import com.goodee.cando_app.dto.UserDto
import com.goodee.cando_app.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.ktx.Firebase
import java.lang.StringBuilder
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

class registerFragment : Fragment() {
    private val TAG: String = "로그"
    private var isExistId = false
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var userViewModelViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG,"registerFragment - onCreate() called")
        super.onCreate(savedInstanceState)
        // Initialize firebase auth
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG,"registerFragment - onCreateView() called")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        userViewModelViewModel = UserViewModel(requireActivity().application)
        userViewModelViewModel.userLiveData.observe(viewLifecycleOwner, Observer { firebaseUser ->
            if (firebaseUser == null) Toast.makeText(requireContext(), "회원가입 실패", Toast.LENGTH_SHORT).show()
            else findNavController().navigate(R.id.action_registerFragment_to_diaryFragment)
        })
        setEvent()

        return binding.root
    }

    private fun setEvent() {
        // 회원가입과 정규식 처리
        binding.buttonRegisterRegisterbutton.setOnClickListener {
            if (binding.edittextRegisterIdinput.text.isNullOrEmpty() || binding.edittextRegisterIdinput.text.isBlank()) {
                Toast.makeText(requireActivity(),"아이디를 확인해주세요",Toast.LENGTH_SHORT).show()
                binding.edittextRegisterIdinput.requestFocus()
                val imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(binding.edittextRegisterIdinput,0)
            } else if (binding.edittextRegisterPasswordinput.text.isNullOrEmpty() || binding.edittextRegisterPasswordinput.text.isEmpty()) {
                Toast.makeText(requireActivity(),"비밀번호를 확인해주세요",Toast.LENGTH_SHORT).show()
                binding.edittextRegisterPasswordinput.requestFocus()
                val imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(binding.edittextRegisterPasswordinput,0)
            } else if (binding.edittextRegisterPasswordcheckinput.text.isNullOrEmpty() || binding.edittextRegisterPasswordcheckinput.text.isEmpty()) {
                Toast.makeText(requireActivity(),"비밀번호 확인 칸을 확인해주세요",Toast.LENGTH_SHORT).show()
                binding.edittextRegisterPasswordcheckinput.requestFocus()
                val imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(binding.edittextRegisterPasswordcheckinput,0)
            } else if (!binding.edittextRegisterPasswordinput.text.toString().equals(binding.edittextRegisterPasswordinput.text.toString())) {
                Toast.makeText(requireActivity(),"입력하신 비밀번호가 일치하지 않습니다",Toast.LENGTH_SHORT).show()
                binding.edittextRegisterPasswordinput.requestFocus()
                val imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(binding.edittextRegisterPasswordinput,0)
            } else if (binding.edittextRegisterNameinput.text.isNullOrEmpty() || binding.edittextRegisterNameinput.text.isEmpty()) {
                Toast.makeText(requireActivity(),"이름을 확인해주세요",Toast.LENGTH_SHORT).show()
                binding.edittextRegisterNameinput.requestFocus()
                val imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(binding.edittextRegisterNameinput,0)
            } else if (binding.edittextRegisterEmailinput.text.isNullOrEmpty() || binding.edittextRegisterEmailinput.text.isEmpty()) {
                Toast.makeText(requireActivity(),"이메일을 확인해주세요",Toast.LENGTH_SHORT).show()
                binding.edittextRegisterEmailinput.requestFocus()
                val imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(binding.edittextRegisterEmailinput, 0)
            } else {
                if (isExistId) {
                    Toast.makeText(requireActivity(), "이미 존재하는 아이디입니다.",Toast.LENGTH_LONG).show()
                } else {
                    // 회원 가입 시키기
                    val email = binding.edittextRegisterEmailinput.text.toString()
                    val password = binding.edittextRegisterPasswordinput.text.toString()
                    val name = binding.edittextRegisterNameinput.text.toString()
                    val phone = binding.edittextPhonePhoneinput.text.toString()
                    val id = binding.edittextRegisterIdinput.text.toString()
                    val userDto = UserDto(name = name, password = password, email = email, phone = phone, id = id)
                    userViewModelViewModel.register(userDto)
                }
            }
        }

        // 아이디 중복 확인
        binding.buttonRegisterDuplicatecheck.setOnClickListener {
            if (binding.edittextRegisterIdinput.text.isNullOrBlank() || binding.edittextRegisterIdinput.text.isNullOrEmpty()) {
                Toast.makeText(requireActivity(),"아이디를 확인해주세요",Toast.LENGTH_SHORT).show()
                binding.edittextRegisterIdinput.requestFocus()
                val imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(binding.edittextRegisterIdinput,0)
            } else {
                if (isExistId == true) {
                    Toast.makeText(requireActivity(), "이미 존재하는 아이디입니다.",Toast.LENGTH_LONG).show()
                } else if (isExistId == false) {
                    Toast.makeText(requireActivity(), "사용하셔도 좋은 아이디입니다.", Toast.LENGTH_SHORT).show()
                    // 중복 체크 완료됐다는 로직이 들어가야함.
                }
            }
        }
    }
}