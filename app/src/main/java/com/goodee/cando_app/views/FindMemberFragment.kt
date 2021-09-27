package com.goodee.cando_app.views

import android.os.Bundle
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
    lateinit var binding: FragmentFindMemberBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_find_member, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = FindMemberAdapter(requireActivity())
        val fragmentTitle = listOf<String>(view.resources.getString(R.string.findid_header),view.resources.getString(R.string.findpassword_header))

        adapter.fragmentList = listOf(FindIdFragment(), FindPasswordFragment())
        binding.viewpagerFindmemberPager.adapter = adapter

        TabLayoutMediator(binding.tablayoutFindmemberTabmenu, binding.viewpagerFindmemberPager) { tab, positon ->
            tab.text = fragmentTitle[positon]
        }.attach()
    }
}