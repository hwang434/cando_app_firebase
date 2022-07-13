package com.goodee.cando_app.views.user

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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.goodee.cando_app.R
import com.goodee.cando_app.databinding.FragmentRegisterBinding
import com.goodee.cando_app.dto.UserDto
import com.goodee.cando_app.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterFragment : Fragment() {
    companion object {
        private const val TAG: String = "LOG"
    }
    private lateinit var binding: FragmentRegisterBinding

    private val userViewModel by lazy {
        ViewModelProvider(requireActivity(), object: ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return UserViewModel(requireActivity().application) as T
            }
        }).get(UserViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG,"registerFragment - onCreate() called")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG,"registerFragment - onCreateView() called")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false)
        setEvent()

        return binding.root
    }

    private fun setEvent() {
        // 회원가입과 정규식 처리
        binding.buttonRegisterRegisterButton.setOnClickListener {
            binding.run {
                var emptyView: View? = null
                if (edittextRegisterEmailinput.text.isEmpty() || edittextRegisterEmailinput.text.isBlank()) {
                    Toast.makeText(requireActivity(),getString(R.string.register_email_input),Toast.LENGTH_SHORT).show()
                    emptyView = edittextRegisterEmailinput
                } else if (edittextRegisterNameInput.text.isEmpty() || edittextRegisterNameInput.text.isBlank()) {
                    Toast.makeText(requireActivity(),getString(R.string.register_name_input),Toast.LENGTH_SHORT).show()
                    emptyView = edittextRegisterNameInput
                } else if (edittextRegisterPasswordinput.text.isEmpty() || edittextRegisterPasswordinput.text.isBlank()) {
                    Toast.makeText(requireActivity(),getString(R.string.toast_check_password),Toast.LENGTH_SHORT).show()
                    emptyView = edittextRegisterPasswordinput
                } else if (edittextRegisterPasswordcheckinput.text.isEmpty() || edittextRegisterPasswordcheckinput.text.isBlank()) {
                    Toast.makeText(requireActivity(),getString(R.string.toast_check_recheck),Toast.LENGTH_SHORT).show()
                    emptyView = edittextRegisterPasswordcheckinput
                } else if (edittextRegisterPasswordinput.text.toString() != edittextRegisterPasswordinput.text.toString()) {
                    Toast.makeText(requireActivity(),getString(R.string.toast_check_password_not_same),Toast.LENGTH_SHORT).show()
                    emptyView = edittextRegisterPasswordinput
                } else {
                    if (userViewModel.isExistEmail(edittextRegisterEmailinput.text.toString())) {
                        Toast.makeText(requireActivity(), getString(R.string.toast_is_exist_email),Toast.LENGTH_LONG).show()
                    } else {
                        // 회원 가입 시키기
                        val email = edittextRegisterEmailinput.text.toString()
                        val name = edittextRegisterNameInput.text.toString()
                        val password = edittextRegisterPasswordinput.text.toString().trim()
                        val phone = edittextPhoneInput.text.toString()
                        val userDto = UserDto(email = email, name = name, phone = phone)
                        lifecycleScope.launch(Dispatchers.IO) {
                            // if : 회원 가입 성공 -> 다이어리 화면으로 이동
                            try {
                                if (userViewModel.register(email, userDto, password)) {
                                    withContext(Dispatchers.Main) {
                                        findNavController().navigate(R.id.action_registerFragment_to_diaryFragment)
                                    }
                                }
                            } catch (e: FirebaseAuthUserCollisionException) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(requireContext(), "이미 존재하는 이메일입니다.", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: FirebaseAuthWeakPasswordException) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(requireContext(), "너무 쉬운 비밀번호입니다.", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Log.d(TAG,"RegisterFragment - setEvent() called")
                            }
                        }
                    }
                }

                if (emptyView != null) {
                    emptyView.requestFocus()
                    val imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(emptyView, 0)
                }
            }
        }

        // 아이디 중복 확인
        binding.buttonRegisterDuplicatecheck.setOnClickListener {
            if (binding.edittextRegisterEmailinput.text.isNullOrBlank() || binding.edittextRegisterEmailinput.text.isEmpty()) {
                Toast.makeText(requireActivity(),getString(R.string.toast_check_email),Toast.LENGTH_SHORT).show()
                binding.edittextRegisterEmailinput.requestFocus()
                val imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(binding.edittextRegisterEmailinput,0)
            } else {
                // if : 중복 아이디면,
                // else : 중복아이디가 아니면
            }
        }
    }
}