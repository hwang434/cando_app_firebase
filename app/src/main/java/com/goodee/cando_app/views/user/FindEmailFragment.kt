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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.goodee.cando_app.R
import com.goodee.cando_app.databinding.FragmentFindIdBinding
import com.goodee.cando_app.util.RegexChecker
import com.goodee.cando_app.viewmodel.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FindEmailFragment : Fragment() {
    companion object {
        private const val TAG: String = "로그"
    }
    private lateinit var binding: FragmentFindIdBinding
    private val userViewModel by lazy {
        ViewModelProvider(requireActivity(), object: ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return UserViewModel(requireActivity().application) as T
            }
        }).get(UserViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG,"FindIdFragment - onCreateView() called")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_find_id, container, false)
        setEvent()

        return binding.root
    }

    private fun setEvent() {
        Log.d(TAG,"FindEmailFragment - setEvent() called")
        binding.buttonFindidSubmit.setOnClickListener {
            findId()
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
            lifecycleScope.launch(Dispatchers.IO) {
                val qResult = userViewModel.findUserEmail(phone = phone, name = name)
                withContext(Dispatchers.Main) {
                    if (qResult.isEmpty) {
                        Toast.makeText(requireContext(), getString(R.string.toast_find_id_not_exist_info), Toast.LENGTH_SHORT).show()
                    } else {
                        val alertDialogBuilder = AlertDialog.Builder(requireContext()).create()
                        // if : 입력한 정보와 일치하는 회원이 여러 명
                        if (qResult.documents.size == 1) {
                            alertDialogBuilder.apply {
                                setTitle(getString(R.string.alert_find_email_title))
                                setMessage(qResult.documents[0].get("email").toString())
                            }
                        } else if (qResult.documents.size > 1) {
                            alertDialogBuilder.apply {
                                setTitle(getString(R.string.error_title))
                                setMessage(getString(R.string.error_message))
                            }
                        }
                        alertDialogBuilder.show()
                    }
                }
            }
        }
    }
}