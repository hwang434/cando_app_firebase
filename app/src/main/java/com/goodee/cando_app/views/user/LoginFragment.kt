package com.goodee.cando_app.views.user

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.goodee.cando_app.R
import com.goodee.cando_app.databinding.FragmentLoginBinding
import com.goodee.cando_app.util.RegexChecker
import com.goodee.cando_app.util.Resource
import com.goodee.cando_app.viewmodel.UserViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {
    companion object {
        private const val TAG: String = "로그"
    }
    private lateinit var binding: FragmentLoginBinding
    private val userViewModel by viewModels<UserViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG,"LoginFragment - onCreate() called")
        super.onCreate(savedInstanceState)
        userViewModel.userLiveData.observe(this) {
            when (it) {
                is Resource.Success -> {
                    hideProgressBar()
                    Toast.makeText(requireContext(), "${it.data?.email}님 환영합니다.", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_loginFragment_to_diaryFragment)
                }
                is Resource.Error -> {
                    hideProgressBar()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG,"LoginFragment - onStart() called")
        val currentUser = Firebase.auth.currentUser

        // if current user is not null than user is signed in. Thus navigate to diary fragment.
        currentUser?.let {
            Log.d(TAG,"LoginFragment - $currentUser")
            findNavController().navigate(R.id.action_loginFragment_to_diaryFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG,"LoginFragment - onCreateView() called")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login,container, false)
        setEvent()

        return binding.root
    }

    private fun setEvent() {
        binding.apply {
            // 회원 찾기 페이지 이동
            buttonLoginFindmember.setOnClickListener {
                view?.findNavController()?.navigate(R.id.action_loginFragment_to_findMember)
            }

            // 회원가입 페이지 이동
            buttonLoginRegisterbutton.setOnClickListener {
                view?.findNavController()?.navigate(R.id.action_loginFragment_to_registerFragment)
            }

            // 키보드 엔터 누르면 클릭 이벤트 실행
            edittextLoginPasswordinput.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                    binding.edittextLoginPasswordinput.callOnClick()
                    true
                } else {
                    false
                }
            }

            // 정규식과 로그인 처리
            buttonLoginLoginbutton.setOnClickListener {
                val email = binding.edittextLoginEmailinput.text.trim().toString()
                val password = binding.edittextLoginPasswordinput.text.trim().toString()
                if (isInputOkay(email, password)) {
                    login(email, password)
                }
            }
        }
    }

    private fun isInputOkay(email: String, password: String): Boolean {
        binding.apply {
            val imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            if (!RegexChecker.isValidEmail(email)) {
                Toast.makeText(requireActivity(),getString(R.string.toast_id_check),Toast.LENGTH_SHORT).show()
                edittextLoginEmailinput.requestFocus()
                imm.showSoftInput(edittextLoginEmailinput,0)
                return false
            }

            if (password.isBlank() || password.isEmpty()) {
                Toast.makeText(requireActivity(),getString(R.string.toast_check_password),Toast.LENGTH_SHORT).show()
                edittextLoginPasswordinput.requestFocus()
                imm.showSoftInput(edittextLoginPasswordinput,0)
                return false
            }
        }

        return true
    }

    private fun login(email: String, password: String) {
        userViewModel.login(email, password)
    }

    private fun showProgressBar() {
        binding.progressbarLoginLoading.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressbarLoginLoading.visibility = View.GONE
    }
}