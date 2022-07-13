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
import com.goodee.cando_app.listener.SingleClickListner
import com.goodee.cando_app.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginFragment : Fragment() {
    companion object {
        private const val TAG: String = "로그"
    }

    private lateinit var binding: FragmentLoginBinding
    private val userViewModel: UserViewModel by lazy {
        ViewModelProvider(requireActivity(), object: ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return UserViewModel(requireActivity().application) as T
            }
        }).get(UserViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG,"LoginFragment - onStart() called")
        val currentUser = Firebase.auth.currentUser

        // if current user is signed in update UI
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
        // 회원 찾기 페이지 이동
        binding.buttonLoginFindmember.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_loginFragment_to_findMember)
        }

        // 회원가입 페이지 이동
        binding.buttonLoginRegisterbutton.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_loginFragment_to_registerFragment)
        }

        // 정규식과 로그인 처리
        binding.buttonLoginLoginbutton.setOnClickListener(object: SingleClickListner() {
            override fun onSingleClick(view: View?) {
                Log.d(TAG,"LoginFragment - loginButton is activated")
                val imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

                if (binding.edittextLoginEmailinput.text.isNullOrBlank() || binding.edittextLoginEmailinput.text.isEmpty()) {
                    Toast.makeText(requireActivity(),getString(R.string.toast_id_check),Toast.LENGTH_SHORT).show()

                    binding.edittextLoginEmailinput.requestFocus()
                    imm.showSoftInput(binding.edittextLoginEmailinput,0)
                } else if (binding.edittextLoginPasswordinput.text.isNullOrBlank() || binding.edittextLoginPasswordinput.text.isEmpty()) {
                    Toast.makeText(requireActivity(),getString(R.string.toast_check_password),Toast.LENGTH_SHORT).show()

                    binding.edittextLoginPasswordinput.requestFocus()
                    imm.showSoftInput(binding.edittextLoginPasswordinput,0)
                } else {
                    // 정규식을 만족했으므로 존재하는 데이터인지 확인.
                    binding.progressbarLoginLoading.visibility = View.VISIBLE
                    val email = binding.edittextLoginEmailinput.text.toString()
                    val password = binding.edittextLoginPasswordinput.text.toString()

                    lifecycleScope.launch(Dispatchers.IO) {
                        try {
                            if (userViewModel.login(email, password)) {
                                withContext(Dispatchers.Main) {
                                    findNavController().navigate(R.id.action_loginFragment_to_diaryFragment)
                                }
                            }
                        } catch (e: FirebaseAuthInvalidCredentialsException) {
                            withContext(Dispatchers.Main) {
                                binding.progressbarLoginLoading.visibility = View.GONE
                                Toast.makeText(requireContext(), getString(R.string.toast_login_fail), Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        })

        // 키보드 엔터 누르면 클릭 이벤트 실행
        binding.edittextLoginPasswordinput.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                binding.edittextLoginPasswordinput.callOnClick()
                true
            } else {
                false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG,"LoginFragment - onDestroy() called")
    }
}