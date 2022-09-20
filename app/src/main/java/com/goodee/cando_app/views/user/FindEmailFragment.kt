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
import androidx.fragment.app.activityViewModels
import com.goodee.cando_app.R
import com.goodee.cando_app.databinding.FragmentFindIdBinding
import com.goodee.cando_app.util.RegexChecker
import com.goodee.cando_app.util.Resource
import com.goodee.cando_app.viewmodel.UserViewModel

class FindEmailFragment : Fragment() {
    companion object {
        private const val TAG: String = "로그"
    }
    private lateinit var binding: FragmentFindIdBinding
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG,"FindIdFragment - onCreateView() called")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_find_id, container, false)
        setEvent()
        setObserver()

        return binding.root
    }

    private fun setEvent() {
        Log.d(TAG,"FindEmailFragment - setEvent() called")
        binding.buttonFindidSubmit.setOnClickListener {
            findId()
        }
    }

    private fun setObserver() {
        Log.d(TAG,"FindEmailFragment - setObserver() called")
        userViewModel.listOfUserEmail.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    val alertDialog = AlertDialog.Builder(requireContext()).create()
                    alertDialog.apply {
                        setTitle(getString(R.string.alert_find_email_title))
                        setMessage(it.data?.get(0)?.get("email").toString())
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
            userViewModel.findUserEmail(phone = phone, name = name)
        }
    }
}