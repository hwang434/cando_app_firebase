package com.goodee.cando_app.views

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.goodee.cando_app.R
import com.goodee.cando_app.databinding.FragmentMainBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainFragment : Fragment() {
    private val TAG: String = "로그"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG,"MainFragment - onCreateView() called")
        val binding = DataBindingUtil.inflate<FragmentMainBinding>(inflater,R.layout.fragment_main,container,false)

        // 이벤트 등록
        binding.buttonMainRegisterbutton.setOnClickListener { view ->
            view.findNavController().navigate(R.id.action_mainFragment_to_registerFragment)
        }
        binding.buttonMainLoginbutton.setOnClickListener { view ->
            view.findNavController().navigate(R.id.action_mainFragment_to_loginFragment)
        }

        return binding.root
    }
}