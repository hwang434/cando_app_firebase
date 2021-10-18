package com.goodee.cando_app.views

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.goodee.cando_app.R
import com.goodee.cando_app.databinding.FragmentLoginBinding
import com.goodee.cando_app.listener.SingleClickListner
import com.goodee.cando_app.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {
    private val TAG: String = "로그"
    private lateinit var binding: FragmentLoginBinding
    private lateinit var userViewModelViewModel: UserViewModel
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Authenticate with Firebase
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG,"LoginFragment - onCreateView() called")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login,container, false)
        userViewModelViewModel = UserViewModel(requireActivity().application)
        userViewModelViewModel.userLiveData.observe(viewLifecycleOwner, Observer { firebaseUser ->
            if (firebaseUser == null) Toast.makeText(requireContext(), "로그인 실패", Toast.LENGTH_SHORT).show()
            else findNavController().navigate(R.id.action_loginFragment_to_diaryFragment)
            binding.progressbarLoginLoading.visibility = View.INVISIBLE
        })
        setEvent()

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser

        // if current user is signed in update UI
        if (currentUser != null) {
            findNavController().navigate(R.id.action_loginFragment_to_diaryFragment)
        }
    }

    private fun setEvent() {
        binding.buttonLoginFindmember.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_loginFragment_to_findMember)
        }
        // 로그인 버튼 이벤트처리
        binding.buttonLoginLoginbutton.setOnClickListener(object: SingleClickListner() {
            override fun onSingleClick(view: View?) {
                Log.d(TAG,"LoginFragment - loginButton is activated")
                val imm: InputMethodManager?

                // 아이디가 비었거나 Blank거나 null일 때
                if (binding.edittextLoginEmailinput.text.isNullOrBlank() || binding.edittextLoginEmailinput.text.isEmpty()) {
                    Toast.makeText(requireActivity(),"아이디를 확인해주세요.",Toast.LENGTH_SHORT).show()

                    binding.edittextLoginEmailinput.requestFocus()
                    imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(binding.edittextLoginEmailinput,0)
                } else if (binding.edittextLoginPasswordinput.text.isNullOrBlank() || binding.edittextLoginPasswordinput.text.isEmpty()) {
                    Toast.makeText(requireActivity(),"비밀번호를 확인해주세요.",Toast.LENGTH_SHORT).show()

                    binding.edittextLoginPasswordinput.requestFocus()
                    imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(binding.edittextLoginPasswordinput,0)
                } else {
                    // 정규식을 만족했으므로 존재하는 데이터인지 확인.
                    binding.progressbarLoginLoading.visibility = View.VISIBLE
                    val email = binding.edittextLoginEmailinput.text.toString()
                    val password = binding.edittextLoginPasswordinput.text.toString()

                    userViewModelViewModel.login(email, password)
                }
            }
        })

        // 키보드 엔터 누르면 클릭 이벤트 실행
        binding.edittextLoginPasswordinput.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                binding.edittextLoginPasswordinput.callOnClick()
                true
            }
            false
        }

        // 회원가입 페이지 이동
        binding.buttonLoginRegisterbutton.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }
}