package com.goodee.cando_app.views.user

import android.app.Dialog
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.goodee.cando_app.R
import com.goodee.cando_app.databinding.FragmentRegisterBinding
import com.goodee.cando_app.dto.UserDto
import com.goodee.cando_app.util.RegexChecker
import com.goodee.cando_app.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.squareup.okhttp.Dispatcher
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
                } else if (!RegexChecker.isValidPassword(edittextRegisterPasswordinput.text.toString())){
                    val dialog = AlertDialog.Builder(requireContext()).create()
                    dialog.setTitle(getString(R.string.dialog_re_check_password))
                    dialog.setMessage(getString(R.string.dialog_regex_password))
                    dialog.setButton(Dialog.BUTTON_POSITIVE, getString(R.string.yes)) { _, _ -> }
                    dialog.show()
                } else if (edittextRegisterPasswordcheckinput.text.isEmpty() || edittextRegisterPasswordcheckinput.text.isBlank()) {
                    Toast.makeText(requireActivity(),getString(R.string.toast_check_recheck),Toast.LENGTH_SHORT).show()
                    emptyView = edittextRegisterPasswordcheckinput
                } else if (edittextRegisterPasswordinput.text.toString() != edittextRegisterPasswordinput.text.toString()) {
                    Toast.makeText(requireActivity(),getString(R.string.toast_check_password_not_same),Toast.LENGTH_SHORT).show()
                    emptyView = edittextRegisterPasswordinput
                } else {
                    val email = edittextRegisterEmailinput.text.toString()
                    val name = edittextRegisterNameInput.text.toString()
                    val password = edittextRegisterPasswordinput.text.toString().trim()
                    val phone = edittextPhoneInput.text.toString()
                    val userDto = UserDto(email = email, name = name, phone = phone)
                    lifecycleScope.launch(Dispatchers.IO) {
                        // if : 회원 가입 성공 -> 다이어리 화면으로 이동
                        try {
                            val isSendEmailSuccess = userViewModel.sendRegisterEmail(email, userDto, password)
                            withContext(Dispatchers.Main) {
                                val alertDialog = AlertDialog.Builder(requireContext()).create()
                                if (isSendEmailSuccess) {
                                    alertDialog.setTitle("회원가입 이메일 전송")
                                    alertDialog.setMessage("회원 가입 메일을 $email 로 전송하였습니다.\n해당 계정으로 접속하여 받은 이메일 링크를 클릭해주세요.")
                                } else {
                                    alertDialog.setTitle("회원가입 실패")
                                    alertDialog.setMessage("오류가 발생했습니다.\n다시 시도해주세요")
                                }
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "확인")  { _, _ ->
                                    findNavController().navigateUp()
                                }
                                alertDialog.show()
                            }
                        } catch (e: FirebaseAuthUserCollisionException) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(requireContext(), getString(R.string.toast_is_exist_email), Toast.LENGTH_SHORT).show()
                                binding.buttonRegisterDuplicatecheck.isEnabled = true
                                binding.edittextRegisterEmailinput.isEnabled = true
                            }
                        } catch (e: FirebaseAuthWeakPasswordException) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(requireContext(), getString(R.string.toast_password_too_easy), Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Log.w(TAG, "setEvent: register Exception", e)
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
                if (!RegexChecker.isValidEmail(binding.edittextRegisterEmailinput.text.toString())) {
                    Toast.makeText(requireContext(), getString(R.string.toast_register_is_wrong_email), Toast.LENGTH_SHORT).show()
                } else {
                    lifecycleScope.launch(Dispatchers.IO) {
                        // if : 존재하지 않는 이메일
                        if (!userViewModel.isExistEmail(binding.edittextRegisterEmailinput.text.toString())) {
                            withContext(Dispatchers.Main) {
                                val dialog = AlertDialog.Builder(requireContext()).create()
                                dialog.setTitle(getString(R.string.dialog_is_valid_email_title))
                                dialog.setMessage(getString(R.string.dialog_is_valid_email_message))
                                dialog.setButton(Dialog.BUTTON_POSITIVE, getString(R.string.yes)) { _, _ ->
                                    binding.edittextRegisterEmailinput.isEnabled = false
                                    binding.buttonRegisterDuplicatecheck.isEnabled = false
                                }
                                dialog.setButton(Dialog.BUTTON_NEGATIVE, getString(R.string.no)) { _, _ -> }
                                dialog.show()
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                val dialog = AlertDialog.Builder(requireContext()).create()
                                dialog.setTitle(getString(R.string.toast_is_exist_email))
                                dialog.setMessage(getString(R.string.dialog_change_email_message))
                                dialog.setButton(Dialog.BUTTON_NEUTRAL, getString(R.string.yes)) { _, _ -> }
                                dialog.show()
                            }
                        }
                    }
                }
            }
        }
    }
}