package com.goodee.cando_app.views

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.goodee.cando_app.R
import com.goodee.cando_app.databinding.FragmentLoginBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    private val TAG: String = "로그"
    private lateinit var binding: FragmentLoginBinding
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG,"LoginFragment - onCreate() called")
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG,"LoginFragment - onCreateView() called")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login,container, false)

        // 이벤트 처리
        binding.loginButton.setOnClickListener {
            Log.d(TAG,"LoginFragment - loginButton is activated")
            var imm: InputMethodManager?

            if (binding.idInput.text.isNullOrBlank() || binding.idInput.text.isEmpty()) {
                Toast.makeText(requireActivity(),"아이디를 확인해주세요.",Toast.LENGTH_SHORT).show()

                binding.idInput.requestFocus()
                imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(binding.idInput,0)
                false
            } else if (binding.passwordInput.text.isNullOrBlank() || binding.passwordInput.text.isEmpty()) {
                Toast.makeText(requireActivity(),"비밀번호를 확인해주세요.",Toast.LENGTH_SHORT).show()

                binding.passwordInput.requestFocus()
                imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(binding.passwordInput,0)
                false
            } else {
                // 로그인 로직을 처리할 공간.
                Toast.makeText(requireActivity(),"아이디와 비밀번호가 모두 존재합니다.",Toast.LENGTH_SHORT).show()
                true
            }
        }

        binding.passwordInput.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                binding.loginButton.callOnClick()
                true
            }
            false
        }

        return binding.root
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}