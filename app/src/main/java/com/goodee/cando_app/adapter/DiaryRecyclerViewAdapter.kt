package com.goodee.cando_app.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.navigation.Navigation
import com.goodee.cando_app.R
import com.goodee.cando_app.databinding.FragmentDiaryListBinding
import com.goodee.cando_app.dto.DiaryDto
import com.goodee.cando_app.views.diary.DiaryFragmentDirections
import java.text.SimpleDateFormat
import java.util.*

class DiaryRecyclerViewAdapter(private val list: LiveData<List<DiaryDto>>) : RecyclerView.Adapter<DiaryRecyclerViewAdapter.ViewHolder>() {
    private val sdf = SimpleDateFormat("yyyy-MM-dd a HH:mm", Locale.KOREA)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<FragmentDiaryListBinding>(LayoutInflater.from(parent.context), R.layout.fragment_diary_list, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val diary = list.value?.get(position)
        holder.setDiary(diary!!)
    }

    override fun getItemCount(): Int {
        return list.value!!.size
    }

    inner class ViewHolder(val binding: FragmentDiaryListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setDiary(diary: DiaryDto) {
            val date = sdf.format(diary.date)
            binding.textviewDiarylistTitle.text = diary.title
            binding.textviewDiarylistWriter.text = diary.author
            binding.textviewDiarylistDate.text = date.toString()
            binding.textviewDiarylistWriter.text = diary.author
            binding.root.setOnClickListener {
                Navigation.findNavController(binding.root).navigate(
                    DiaryFragmentDirections.actionDiaryFragmentToDiaryViewFragment(
                        diary.dno
                    )
                )
            }
        }
    }
}