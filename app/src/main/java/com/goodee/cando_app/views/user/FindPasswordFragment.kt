package com.goodee.cando_app.views.user

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.goodee.cando_app.R
import com.goodee.cando_app.databinding.FragmentFindPasswordBinding
import com.goodee.cando_app.util.RegexChecker
import com.goodee.cando_app.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FindPasswordFragment : Fragment() {
    companion object {
        private const val TAG: String = "로그"
    }
    private lateinit var binding: FragmentFindPasswordBinding
    private val userViewModel by lazy { UserViewModel(application = requireActivity().application) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_find_password, container, false)
        binding.buttonFindpasswordEmailbutton.setOnClickListener {
            if (binding.edittextFindpasswordNameInput.text.isNullOrEmpty() ||
                binding.edittextFindpasswordNameInput.text.isBlank()) {
                Toast.makeText(requireActivity(), "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else if (binding.edittextFindpasswordEmailinput.text.isNullOrEmpty()
                || binding.edittextFindpasswordEmailinput.text.isBlank()
                || !RegexChecker.isValidEmail(binding.edittextFindpasswordEmailinput.text.toString())) {
                Toast.makeText(requireActivity(), "이메일을 확인해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                val name = binding.edittextFindpasswordNameInput.text.toString()
                val email = binding.edittextFindpasswordEmailinput.text.toString()

                lifecycleScope.launch(Dispatchers.IO) {
                    // if : 존재하는 이름과 이메일이라면
                    if (userViewModel.isExistNameAndEmail(name = name, email = email)) {
                        // 비밀번호 리셋용 이메일 전송
                        val isEmailSend = userViewModel.sendPasswordResetEmail(email)
                        withContext(Dispatchers.Main) {
                            when (isEmailSend) {
                                true -> {
                                    val alertDialogBuilder = AlertDialog.Builder(requireContext()).create()
                                    alertDialogBuilder.run {
                                        setTitle("이메일을 송신했습니다.")
                                        setMessage("해당 이메일로 비밀번호 초기화 메일을 보냈습니다.")
                                        getButton(AlertDialog.BUTTON_NEUTRAL)
                                        show()
                                    }
                                }
                                false -> {
                                    val alertDialogBuilder = AlertDialog.Builder(requireContext()).create()
                                    alertDialogBuilder.run {
                                        setTitle("서버에 상태가 좋지 않습니다.")
                                        setMessage("나중에 다시 시도해주세요.")
                                        getButton(AlertDialog.BUTTON_NEUTRAL)
                                        show()
                                    }
                                }
                            }
                        }
                    } else {
                        // 이름과 이메일이 일치하는 회원이 존재하지 않음.
                        val alertDialogBuilder = AlertDialog.Builder(requireContext()).create()
                        alertDialogBuilder.run {
                            setTitle("해당 정보와 일치하는 회원이 없습니다..")
                            setMessage("입력하신 정보와 일치하는 회원이 존재하지 않습니다.\n입력하신 정보를 확인해주세요.")
                            getButton(AlertDialog.BUTTON_NEUTRAL)
                            show()
                        }
                    }
                }
            }
        }

        return binding.root
    }
}