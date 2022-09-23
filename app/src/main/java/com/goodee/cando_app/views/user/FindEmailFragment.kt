package com.goodee.cando_app.views.user

import android.app.AlertDialog
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
import com.goodee.cando_app.databinding.FragmentFindIdBinding
import com.goodee.cando_app.util.RegexChecker
import com.goodee.cando_app.util.Resource
import com.goodee.cando_app.viewmodel.FindEmailViewModel

class FindEmailFragment : Fragment() {

    companion object {
        private const val TAG: String = "로그"
    }
    private lateinit var binding: FragmentFindIdBinding
    private val findEmailViewModel: FindEmailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG,"FindIdFragment - onCreateView() called")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_find_id, container, false)
        binding.apply {
            edittextFindidNameinput.setText(findEmailViewModel.userName.value ?: "")
            edittextFindidPhoneinput.setText(findEmailViewModel.userPhoneNumber.value ?: "")
        }
        setEvent()
        setObserver()

        return binding.root
    }

    private fun setEvent() {
        Log.d(TAG,"FindEmailFragment - setEvent() called")
        binding.buttonFindidSubmit.setOnClickListener {
            val name = binding.edittextFindidNameinput.text.toString()
            setViewModelName(name)
            val phone = binding.edittextFindidPhoneinput.text.toString()
            setViewModelPhone(phone)
            findId()
        }
    }

    private fun setViewModelName(name: String) {
        Log.d(TAG,"FindEmailFragment - setViewModelName() called")
        findEmailViewModel.setName(name)
    }

    private fun setViewModelPhone(phone: String) {
        Log.d(TAG,"FindEmailFragment - setViewModelPhone() called")
        findEmailViewModel.setPhone(phone)
    }

    private fun setObserver() {
        Log.d(TAG,"FindEmailFragment - setObserver() called")
        findEmailViewModel.listOfUserEmail.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    val alertDialog = AlertDialog.Builder(requireContext()).create()
                    alertDialog.apply {
                        setTitle(getString(R.string.alert_find_email_title))
                        setMessage(it.data?.get(0)?.get("email").toString())
                        setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.yes)) { _, _ ->
                            // Just Close the alertDialog. So don't have to type some logic.
                        }
                    }
                    alertDialog.show()
                }

                is Resource.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }

                else -> {

                }
            }
        }
    }

    private fun findId() {
        Log.d(TAG,"FindEmailFragment - findId() called")
        val phone = binding.edittextFindidPhoneinput.text.toString().trim().replace("-","")
        val name = binding.edittextFindidNameinput.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(requireActivity(), getString(R.string.register_name_input), Toast.LENGTH_SHORT).show()
        } else if (phone.isEmpty() || !RegexChecker.isValidPhone(phone)) {
            Toast.makeText(requireActivity(), getString(R.string.toast_find_id_check_phone), Toast.LENGTH_SHORT).show()
        } else {
            findEmailViewModel.findUserEmail(phone = phone, name = name)
        }
    }
}