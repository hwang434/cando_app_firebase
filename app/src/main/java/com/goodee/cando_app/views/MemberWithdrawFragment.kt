package com.goodee.cando_app.views

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.goodee.cando_app.R
import com.goodee.cando_app.databinding.FragmentMemberWithdrawBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*

class MemberWithdrawFragment : Fragment() {
    private val TAG: String = "로그"
    private lateinit var binding: FragmentMemberWithdrawBinding

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
            withdrawMember()
        }
    }

    // 회원 삭제
    private fun withdrawMember() {
        Log.d(TAG,"MemberWithdrawFragment - withdrawMember() called")
        checkPassword()
    }

    private fun checkPassword() {
        Log.d(TAG,"MemberWithdrawFragment - checkPassword() called")
        val firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser != null && !binding.edittextMemberwithdrawPasswordinput.text.isNullOrBlank() && !binding.edittextMemberwithdrawPasswordinput.text.isEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(firebaseAuth.currentUser!!.email!!, binding.edittextMemberwithdrawPasswordinput.text.toString()).addOnCompleteListener { it ->
                if (it.isSuccessful) {
                    deleteUser()
                }
            }
        }
    }

    private fun deleteUser(): Boolean {
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.currentUser!!.delete().addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d(TAG,"MemberWithdrawFragment - deleteUser() 회원 탈퇴 성공")
                Toast.makeText(requireContext(), "회원 탈퇴 성공", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_memberWithdrawFragment_to_mainFragment)
            } else {
                Log.d(TAG,"MemberWithdrawFragment - deleteUser() 회원 탈퇴 실패")
                Toast.makeText(requireContext(), "회원 탈퇴 실패", Toast.LENGTH_SHORT).show()
            }
        }
        return true
    }


}