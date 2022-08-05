package com.goodee.cando_app.views.user

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.goodee.cando_app.R
import com.goodee.cando_app.databinding.FragmentMemberWithdrawBinding
import com.goodee.cando_app.util.RegexChecker
import com.goodee.cando_app.viewmodel.DiaryViewModel
import com.goodee.cando_app.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class MemberWithdrawFragment : Fragment() {
    companion object {
        private const val TAG: String = "로그"
    }
    private lateinit var binding: FragmentMemberWithdrawBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var diaryViewModel: DiaryViewModel
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG,"MemberWithdrawFragment - onCreateView() called")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_member_withdraw, container, false)
        setEvent()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG,"MemberWithdrawFragment - onViewCreated() called")
        super.onViewCreated(view, savedInstanceState)
        userViewModel = UserViewModel(requireActivity().application)
        diaryViewModel = DiaryViewModel(requireActivity().application)
    }

    private fun setEvent() {
        Log.d(TAG,"MemberWithdrawFragment - setEvent() called")
        binding.buttonMemberwithdrawWithdrawbutton.setOnClickListener {
            it.isEnabled = false
            val password = binding.edittextMemberwithdrawPasswordinput.text.toString().trim()
            val email = userViewModel.userLiveData.value?.email.toString().trim()
            if (RegexChecker.isValidEmail(email) && password.isNotEmpty()) {
                withdrawMember(email, password)
            }
        }
    }

    // 회원 삭제
    private fun withdrawMember(email: String, password: String) {
        Log.d(TAG,"MemberWithdrawFragment - withdrawMember() called")
        lifecycleScope.launch(Dispatchers.IO) {
            var isWithdrawSuccess = false
            try {
                if (!diaryViewModel.deleteAllDiary()) {
                    return@launch
                }
                isWithdrawSuccess = userViewModel.withdrawUser(email, password)
            } catch (e: FirebaseAuthException) {
                Log.w(TAG, "withdrawMember: firebase auth Exception", e)
            } catch (e: Exception) {
                Log.w(TAG, "withdrawMember: fail", e)
            }

            withContext(Dispatchers.Main) {
                if (isWithdrawSuccess) {
                    // 회원 탈퇴 성공
                    Toast.makeText(context, "그동안 이용해주셔서 감사합니다.", Toast.LENGTH_LONG).show()
                    requireActivity().finish()
                } else {
                    Toast.makeText(context, "회원 탈퇴에 실패했습니다.", Toast.LENGTH_LONG).show()
                }
                binding.buttonMemberwithdrawWithdrawbutton.isEnabled = true
            }
        }
    }
}