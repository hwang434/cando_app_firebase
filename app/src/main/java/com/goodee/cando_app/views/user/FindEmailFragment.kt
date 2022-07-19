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
        binding.buttonFindidSubmit.setOnClickListener {
            val phone = binding.edittextFindidPhoneinput.text.toString().trim().replace("-","")
            val name = binding.edittextFindidNameinput.text.toString()

            if (name.isEmpty() || name.isBlank()) {
                Toast.makeText(requireActivity(), "이름을 입력해주세요", Toast.LENGTH_SHORT).show()
            } else if (phone.isEmpty() || phone.isBlank() || !RegexChecker.isValidPhone(phone)) {
                Toast.makeText(requireActivity(), "전화번호를 확인해주세요", Toast.LENGTH_SHORT).show()
            } else {
                lifecycleScope.launch(Dispatchers.IO) {
                    val qResult = userViewModel.findUserEmail(phone = phone, name = name)
                    if (qResult.isEmpty) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "존재하지 않는 회원입니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            // if : 입력한 정보와 일치하는 회원이 여러 명
                            if (qResult.documents.size > 1) {

                            } else if (qResult.documents.isNotEmpty()){
                                val alertDialogBuilder = AlertDialog.Builder(requireContext()).create()
                                alertDialogBuilder.setTitle("찾으시는 이메일")
                                alertDialogBuilder.setMessage(qResult.documents[0].get("email").toString())
                                alertDialogBuilder.show()
                            }
                        }
                    }
                }
            }
        }
    }
}