package com.goodee.cando_app.views.user

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import com.goodee.cando_app.R
import com.goodee.cando_app.databinding.FragmentFindPasswordBinding
import com.goodee.cando_app.util.RegexChecker
import com.goodee.cando_app.viewmodel.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FindPasswordFragment : Fragment() {
    companion object {
        private const val TAG: String = "로그"
    }
    private lateinit var binding: FragmentFindPasswordBinding
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG,"FindPasswordFragment - onCreateView() called")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_find_password, container, false)
        binding.apply {
            buttonFindpasswordEmailbutton.setOnClickListener {
                findPasswordByEmail()
            }
        }

        return binding.root
    }

    private fun findPasswordByEmail() {
        Log.d(TAG,"FindPasswordFragment - findPasswordByEmail() called")
        binding.apply {
            val name = edittextFindpasswordNameInput.text.toString().trim()
            val email = edittextFindpasswordEmailinput.text.toString().trim()
            if (name.isEmpty()) {
                Toast.makeText(requireActivity(), getString(R.string.register_id_hint), Toast.LENGTH_SHORT).show()
                return
            } else if (email.isEmpty()
                || !RegexChecker.isValidEmail(email)
            ) {
                Toast.makeText(requireActivity(), R.string.toast_register_is_wrong_email, Toast.LENGTH_SHORT).show()
                return
            }

            val alertDialogBuilder = AlertDialog.Builder(requireContext()).create()
            alertDialogBuilder.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.confirm)) { _, _ -> }
            userViewModel.viewModelScope.launch(Dispatchers.IO) {
                // if : 존재하는 이름과 이메일이라면
                if (userViewModel.isExistNameAndEmail(name = name, email = email)) {
                    // 비밀번호 리셋용 이메일 전송
                    val isEmailSend = userViewModel.sendPasswordResetEmail(email)
                    withContext(Dispatchers.Main) {
                        if (isEmailSend) {
                            alertDialogBuilder.run {
                                setTitle(getString(R.string.alert_find_password_email_send_title))
                                setMessage(getString(R.string.alert_find_password_email_send_message))
                            }
                        } else {
                            alertDialogBuilder.run {
                                setTitle(getString(R.string.error_title))
                                setMessage(getString(R.string.error_message))
                            }
                        }
                        alertDialogBuilder.show()
                    }
                } else {
                    // 이름과 이메일이 일치하는 회원이 존재하지 않음.
                    withContext(Dispatchers.Main) {
                        alertDialogBuilder.run {
                            setTitle(getString(R.string.toast_find_id_not_exist_info))
                            setMessage("입력하신 정보와 일치하는 회원이 존재하지 않습니다.\n입력하신 정보를 확인해주세요.")
                            show()
                        }
                    }
                }
            }
        }
    }
}