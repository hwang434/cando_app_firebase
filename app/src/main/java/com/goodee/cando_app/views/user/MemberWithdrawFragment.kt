package com.goodee.cando_app.views.user

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.goodee.cando_app.R
import com.goodee.cando_app.databinding.FragmentMemberWithdrawBinding
import com.goodee.cando_app.util.Resource
import com.goodee.cando_app.viewmodel.DiaryViewModel
import com.goodee.cando_app.viewmodel.UserViewModel

class MemberWithdrawFragment : Fragment() {
    companion object {
        private const val TAG: String = "로그"
    }
    private lateinit var binding: FragmentMemberWithdrawBinding
    private val userViewModel: UserViewModel by activityViewModels()
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

    private fun setObserver() {
        userViewModel.isWithdrawSuccess.observe(viewLifecycleOwner) { resouce ->
            when (resouce) {
                is Resource.Success -> {
                    Toast.makeText(context, "그동안 이용해주셔서 감사합니다.", Toast.LENGTH_LONG).show()
                    requireActivity().finish()
                }
                is Resource.Error -> {
                    Log.w(TAG, "setObserver: ", Throwable(message = resouce.message))
                    Toast.makeText(requireContext(), "${resouce.message}", Toast.LENGTH_SHORT).show()
                }
                else -> {

                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG,"MemberWithdrawFragment - onViewCreated() called")
        super.onViewCreated(view, savedInstanceState)
        diaryViewModel = DiaryViewModel(requireActivity().application)
        setObserver()
    }

    private fun setEvent() {
        Log.d(TAG,"MemberWithdrawFragment - setEvent() called")
        binding.buttonMemberwithdrawWithdrawbutton.setOnClickListener {
            it.isEnabled = false
            val password = binding.edittextMemberwithdrawPasswordinput.text.toString().trim()
            val email = userViewModel.userLiveData.value?.data?.email.toString().trim()

            if (password.isNotEmpty()) {
                withdrawMember(email, password)
            } else {
                Toast.makeText(requireContext(), "This is Invalid password.", Toast.LENGTH_SHORT).show()
            }
            it.isEnabled = true
        }
    }

    // 회원 삭제
    private fun withdrawMember(email: String, password: String) {
        Log.d(TAG,"MemberWithdrawFragment - withdrawMember() called")
        diaryViewModel.deleteAllDiary(email, password)
        userViewModel.withdrawUser(email, password)
    }
}