package com.goodee.cando_app.views.user

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.goodee.cando_app.R
import com.goodee.cando_app.databinding.FragmentLoginBinding
import com.goodee.cando_app.listener.SingleClickListener
import com.goodee.cando_app.util.RegexChecker
import com.goodee.cando_app.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class LoginFragment : Fragment() {
    companion object {
        private const val TAG: String = "로그"
    }

    private lateinit var binding: FragmentLoginBinding
    private val userViewModel: UserViewModel by lazy {
        UserViewModel(requireActivity().application)
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
            buttonLoginLoginbutton.setOnClickListener(getLoginSingleClickListener())
        }
    }

    private fun getLoginSingleClickListener(): SingleClickListener {
        val singleClickListener = object: SingleClickListener() {
            override fun onSingleClick(view: View?) {
                binding.apply {
                    val imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    val email = edittextLoginEmailinput.text.trim().toString()
                    val password = edittextLoginPasswordinput.text.trim().toString()
                    if (!RegexChecker.isValidEmail(email)) {
                        Toast.makeText(requireActivity(),getString(R.string.toast_id_check),Toast.LENGTH_SHORT).show()
                        edittextLoginEmailinput.requestFocus()
                        imm.showSoftInput(edittextLoginEmailinput,0)
                        return
                    }

                    if (password.isBlank() || password.isEmpty()) {
                        Toast.makeText(requireActivity(),getString(R.string.toast_check_password),Toast.LENGTH_SHORT).show()
                        edittextLoginPasswordinput.requestFocus()
                        imm.showSoftInput(edittextLoginPasswordinput,0)
                        return
                    }

                    // 정규식을 만족했으므로 존재하는 데이터인지 확인.
                    progressbarLoginLoading.visibility = View.VISIBLE
                    lifecycleScope.launch(Dispatchers.IO) {
                        try {
                            val isLoginSuccess = userViewModel.login(email, password)
                            withContext(Dispatchers.Main) {
                                if (isLoginSuccess) {
                                    findNavController().navigate(R.id.action_loginFragment_to_diaryFragment)
                                } else {
                                    Toast.makeText(requireContext(), "해당 아이디로 이메일 인증을 해주세요.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: FirebaseAuthInvalidCredentialsException) {
                            // 비밀번호 잘못 입력
                            Log.w(TAG, "onSingleClick: ", e)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(requireContext(), getString(R.string.toast_login_fail), Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: FirebaseAuthInvalidUserException) {
                            // 존재하지 않는 유저
                            Log.w(TAG, "onSingleClick: ", e)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(requireContext(), getString(R.string.toast_login_fail), Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Log.w(TAG, "onSingleClick: ", e)
                        } finally {
                            withContext(Dispatchers.Main) {
                                progressbarLoginLoading.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }
        return singleClickListener
    }
}