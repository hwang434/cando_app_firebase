package com.goodee.cando_app.views

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.goodee.cando_app.R
import com.goodee.cando_app.databinding.FragmentMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainFragment : Fragment() {
    private val TAG: String = "로그"
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth

        if (auth.currentUser != null) {
            Toast.makeText(requireActivity(), "${auth.currentUser!!.email}님 환영합니다.",Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_mainFragment_to_diaryFragment)
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        Log.d(TAG,"MainFragment - onCreateView() called")
        binding = DataBindingUtil.inflate<FragmentMainBinding>(inflater,R.layout.fragment_main,container,false)

        setEvent()
        return binding.root
    }

    private fun setEvent() {
        // 이벤트 등록
        binding.buttonMainRegisterbutton.setOnClickListener { view ->
            view.findNavController().navigate(R.id.action_mainFragment_to_registerFragment)
        }
        binding.buttonMainLoginbutton.setOnClickListener { view ->
            view.findNavController().navigate(R.id.action_mainFragment_to_loginFragment)
        }
    }
}