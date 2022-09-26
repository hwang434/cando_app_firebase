package com.goodee.cando_app.views.user

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.goodee.cando_app.R
import com.goodee.cando_app.databinding.FragmentFindPasswordBinding
import com.goodee.cando_app.util.RegexChecker
import com.goodee.cando_app.util.Resource
import com.goodee.cando_app.viewmodel.FindPasswordViewModel

class FindPasswordFragment : Fragment() {

    companion object {
        private const val TAG: String = "로그"
    }

    private lateinit var binding: FragmentFindPasswordBinding
    private val userViewModel: FindPasswordViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG,"FindPasswordFragment - onCreateView() called")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_find_password, container, false)
        setEvent()
        setObserver()

        return binding.root
    }

    private fun setEvent() {
        Log.d(TAG,"FindPasswordFragment - setEvent() called")
        binding.apply {
            buttonFindpasswordEmailbutton.setOnClickListener {
                findPasswordByEmail()
            }
        }
    }

    private fun setObserver() {
        Log.d(TAG,"FindPasswordFragment - setObserver() called")
        userViewModel.isExistNameAndEmail.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    userViewModel.sendPasswordResetEmail(email = it.data.toString())
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {

                }
            }
        }

        userViewModel.isPasswordResetEmailSent.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    Log.d(TAG,"FindPasswordFragment - password reset message sent called")
                    Toast.makeText(requireContext(), "Password Reset email is sent to ${it.data}.", Toast.LENGTH_LONG).show()
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
                is Resource.Loading -> {

                }
            }
        }
    }

    private fun findPasswordByEmail() {
        Log.d(TAG,"FindPasswordFragment - findPasswordByEmail() called")
        binding.apply {
            val name = edittextFindpasswordNameInput.text.toString().trim()
            val email = edittextFindpasswordEmailinput.text.toString().trim()
            if (name.isEmpty()) {
                Toast.makeText(requireActivity(), getString(R.string.register_id_hint), Toast.LENGTH_SHORT).show()
                return
            } else if (email.isEmpty() || !RegexChecker.isValidEmail(email)) {
                Toast.makeText(requireActivity(), R.string.toast_register_is_wrong_email, Toast.LENGTH_SHORT).show()
                return
            }

            userViewModel.isExistNameAndEmail(name = name, email)
        }
    }
}