package com.goodee.cando_app.views

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.goodee.cando_app.R
import com.goodee.cando_app.databinding.FragmentMainBinding
import com.goodee.cando_app.viewmodel.UserViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainFragment : Fragment() {
    companion object {
        private const val TAG: String = "로그"
    }
    private lateinit var binding: FragmentMainBinding
    private val auth by lazy { Firebase.auth }
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        Log.d(TAG,"MainFragment - onCreateView() called")
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_main,container,false)
        // 로그인 상태면 바로 다이어리 화면으로 이동
        auth.currentUser?.let { user ->
            userViewModel.autoSignIn()

            Toast.makeText(requireActivity(), "${user.email}님 환영합니다.",Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_mainFragment_to_diaryFragment)
        }

        setEvent()

        return binding.root
    }

    private fun setEvent() {
        // 회원가입 페이지 이동
        binding.buttonMainRegisterbutton.setOnClickListener { view ->
            view.findNavController().navigate(R.id.action_mainFragment_to_registerFragment)
        }
        // 로그인 페이지 이동
        binding.buttonMainLoginbutton.setOnClickListener { view ->
            view.findNavController().navigate(R.id.action_mainFragment_to_loginFragment)
        }
    }
}