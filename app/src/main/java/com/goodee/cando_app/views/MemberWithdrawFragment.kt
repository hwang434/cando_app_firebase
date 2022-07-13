package com.goodee.cando_app.views

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.goodee.cando_app.R
import com.goodee.cando_app.databinding.FragmentMemberWithdrawBinding
import com.goodee.cando_app.viewmodel.UserViewModel

class MemberWithdrawFragment : Fragment() {
    private val TAG: String = "로그"
    private lateinit var binding: FragmentMemberWithdrawBinding
    private val userViewModel: UserViewModel by lazy {
        ViewModelProvider(requireActivity(), object: ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return UserViewModel(requireActivity().application) as T
            }
        }).get(UserViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_member_withdraw, container, false)
        setEvent()

        return binding.root
    }

    private fun setEvent() {
        Log.d(TAG,"MemberWithdrawFragment - setEvent() called")
        binding.buttonMemberwithdrawWithdrawbutton.setOnClickListener {
            val password = binding.edittextMemberwithdrawPasswordinput.text.toString()
            userViewModel.userLiveData.value?.email?.let { email -> withdrawMember(email, password) }
        }
    }

    // 회원 삭제
    private fun withdrawMember(email: String, password: String) {
        Log.d(TAG,"MemberWithdrawFragment - withdrawMember() called")
        userViewModel.withdrawUser(email, password)
    }
}