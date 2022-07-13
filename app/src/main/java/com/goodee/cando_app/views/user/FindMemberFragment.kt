package com.goodee.cando_app.views.user

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.goodee.cando_app.R
import com.goodee.cando_app.adapter.FindMemberAdapter
import com.goodee.cando_app.databinding.FragmentFindMemberBinding
import com.google.android.material.tabs.TabLayoutMediator

class FindMemberFragment : Fragment() {
    companion object {
        private const val TAG = "로그"
    }
    private lateinit var binding: FragmentFindMemberBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG,"FindMemberFragment - onCreateView() called")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_find_member, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG,"FindMemberFragment - onViewCreated() called")
        val adapter = FindMemberAdapter(requireActivity())
        val fragmentTitle = listOf(view.resources.getString(R.string.findid_header),view.resources.getString(R.string.findpassword_header))

        adapter.fragmentList = listOf(FindIdFragment(), FindPasswordFragment())
        binding.viewpagerFindmemberPager.adapter = adapter

        TabLayoutMediator(binding.tablayoutFindmemberTabmenu, binding.viewpagerFindmemberPager) { tab, position ->
            tab.text = fragmentTitle[position]
        }.attach()
    }
}