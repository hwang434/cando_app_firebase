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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.goodee.cando_app.R
import com.goodee.cando_app.databinding.FragmentRegisterBinding
import com.goodee.cando_app.dto.UserDto
import com.goodee.cando_app.util.RegexChecker
import com.goodee.cando_app.util.Resource
import com.goodee.cando_app.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterFragment : Fragment() {
    companion object {
        private const val TAG: String = "로그"
    }
    private lateinit var binding: FragmentRegisterBinding
    private val userViewModel: UserViewModel by activityViewModels()

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
        setObserver()

        return binding.root
    }

    private fun setEvent() {
        Log.d(TAG,"RegisterFragment - setEvent() called")
        // 회원가입과 정규식 처리
        binding.buttonRegisterRegisterButton.setOnClickListener {
            binding.apply {
                buttonRegisterRegisterButton.isEnabled = false
                val email = edittextRegisterEmailinput.text.toString().trim()
                val name = edittextRegisterNameInput.text.toString().trim()
                val password = edittextRegisterPasswordinput.text.toString().trim()
                val rePassword = edittextRegisterPasswordcheckinput.text.toString().trim()
                val phone = edittextPhoneInput.text.toString().trim()

                if (isUserInfoRegexMatch(email, name, password, rePassword, phone)) {
                    register(email, name, phone, password)
                }
            }
        }

        // 아이디 중복 확인
        binding.buttonRegisterDuplicatecheck.setOnClickListener {
            val email = binding.edittextRegisterEmailinput.text.toString().trim()
            if (isEmailRegexMatch(email)) {
                checkIsExistEmail(email)
            }
        }
    }

    private fun setObserver() {
        userViewModel.isExistEmail.observe(viewLifecycleOwner) {
            binding.progressbarRegisterLoading.visibility = View.GONE
            when (it) {
                is Resource.Success -> {
                    val dialog = AlertDialog.Builder(requireContext()).create()
                    if (it.data == true) {
                        dialog.setTitle(getString(R.string.toast_is_exist_email))
                        dialog.setMessage(getString(R.string.dialog_change_email_message))
                        dialog.setButton(Dialog.BUTTON_NEUTRAL, getString(R.string.yes)) { _, _ -> }
                    } else {
                        dialog.setTitle(getString(R.string.dialog_is_valid_email_title))
                        dialog.setMessage(getString(R.string.dialog_is_valid_email_message))
                        dialog.setButton(Dialog.BUTTON_POSITIVE, getString(R.string.yes)) { _, _ ->
                            binding.edittextRegisterEmailinput.isEnabled = false
                            binding.buttonRegisterDuplicatecheck.isEnabled = false
                        }
                        dialog.setButton(Dialog.BUTTON_NEGATIVE, getString(R.string.no)) { _, _ -> }
                    }
                    dialog.show()
                }
                is Resource.Error -> {

                }

                is Resource.Loading -> {
                    binding.progressbarRegisterLoading.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun isEmailRegexMatch(email: String): Boolean {
        Log.d(TAG,"RegisterFragment - isEmailRegexMatch() called")
        if (!RegexChecker.isValidEmail(email)) {
            Toast.makeText(requireActivity(),getString(R.string.toast_check_email),Toast.LENGTH_SHORT).show()
            binding.edittextRegisterEmailinput.requestFocus()
            val imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.edittextRegisterEmailinput,0)
            return false
        }

        return true
    }

    private fun checkIsExistEmail(email: String) {
        Log.d(TAG,"RegisterFragment - checkIsExistEmail() called")
        userViewModel.isExistEmail(email)
    }

    private fun isUserInfoRegexMatch(
        email: String,
        name: String,
        password: String,
        rePassword: String,
        phone: String
    ): Boolean {
        Log.d(TAG,"RegisterFragment - isUserInfoRegexMatch() called")
        binding.apply {
            var emptyView: View? = null

            if (!RegexChecker.isValidEmail(email)) {
                Toast.makeText(requireActivity(),getString(R.string.toast_check_email),Toast.LENGTH_SHORT).show()
                emptyView = edittextRegisterEmailinput
            } else if (name.isEmpty() || name.isBlank()) {
                Toast.makeText(requireActivity(),getString(R.string.register_name_input),Toast.LENGTH_SHORT).show()
                emptyView = edittextRegisterNameInput
            } else if (!RegexChecker.isValidPassword(password)) {
                val dialog = AlertDialog.Builder(requireContext()).create()
                dialog.setTitle(getString(R.string.dialog_re_check_password))
                dialog.setMessage(getString(R.string.dialog_regex_password))
                dialog.setButton(Dialog.BUTTON_POSITIVE, getString(R.string.yes)) { _, _ -> }
                dialog.show()
            } else if (rePassword.isEmpty()) {
                Toast.makeText(requireActivity(),getString(R.string.toast_check_recheck),Toast.LENGTH_SHORT).show()
                emptyView = edittextRegisterPasswordcheckinput
            } else if (password != rePassword) {
                Toast.makeText(requireActivity(),getString(R.string.toast_check_password_not_same),Toast.LENGTH_SHORT).show()
                emptyView = edittextRegisterPasswordinput
            }

            if (emptyView == null) {
                return true
            }

            emptyView.requestFocus()
            val imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(emptyView, 0)
            binding.buttonRegisterRegisterButton.isEnabled = true
            return false
        }
    }

    private fun register(email: String, name: String, phone: String, password: String) {
        Log.d(TAG,"RegisterFragment - register() called")
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

            withContext(Dispatchers.Main) {
                binding.buttonRegisterRegisterButton.isEnabled = true
            }
        }
    }
}