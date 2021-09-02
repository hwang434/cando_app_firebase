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
import com.goodee.cando_app.api.DuplicateCheckService
import com.goodee.cando_app.databinding.FragmentRegisterBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
            } else {
                if (checkId() == true) {
                    Toast.makeText(requireActivity(), "이미 존재하는 아이디입니다.",Toast.LENGTH_LONG).show()
                } else {
                    // 회원 가입 시키기
                }
            }
        }

        // DuplicateCheckService로 해당하는 id를 가진 멤버 존재하는지 확인
        binding.buttonRegisterDuplicatecheck.setOnClickListener {
            var isExistId: Boolean? = checkId()

            if (isExistId == true) {
                Toast.makeText(requireActivity(), "이미 존재하는 아이디입니다.",Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(requireActivity(), "사용하셔도 좋은 아이디입니다.", Toast.LENGTH_SHORT).show()
                // 중복 체크 완료됐다는 로직이 들어가야함.
            }
        }

        return binding.root
    }

    fun checkId(): Boolean? {
        val retrofit = DuplicateCheckService.create()
        val duplicateCheckService = retrofit.create(DuplicateCheckService::class.java)
        var isExist: Boolean? = null

        duplicateCheckService.isUserExist(binding.edittextRegisterIdinput.text.toString()).enqueue(object: Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                Log.d(TAG,"registerFragment - onResponse() called")
                isExist = response.body()
            }
            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                Log.d(TAG,"registerFragment - onFailure() called")
            }
        })

        return isExist
    }
}