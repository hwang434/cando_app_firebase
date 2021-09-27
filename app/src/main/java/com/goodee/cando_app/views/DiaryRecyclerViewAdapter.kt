package com.goodee.cando_app.views

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.goodee.cando_app.R
import com.goodee.cando_app.databinding.FragmentDiaryListBinding
import com.goodee.cando_app.viewmodel.Diary
import java.text.SimpleDateFormat

class DiaryRecyclerViewAdapter() : RecyclerView.Adapter<DiaryRecyclerViewAdapter.ViewHolder>() {
    var list = mutableListOf<Diary>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<FragmentDiaryListBinding>(LayoutInflater.from(parent.context),
            R.layout.fragment_diary_list, parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val diary = list.get(position)
        holder.setDiary(diary)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(val binding: FragmentDiaryListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setDiary(diary: Diary) {
            binding.textviewDiarylistTitle.text = diary.title
            binding.textviewDiarylistViewcount.text = diary.readCnt.toString()
            binding.textviewDiarylistWriter.text = diary.writer

            val sdf = SimpleDateFormat("yyyy/MM/dd")
            binding.textviewDiarylistDate.text = sdf.format(diary.writedDate)
        }
    }
}