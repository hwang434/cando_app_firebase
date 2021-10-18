package com.goodee.cando_app.views

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import com.goodee.cando_app.R
import com.goodee.cando_app.databinding.FragmentDiaryListBinding
import com.goodee.cando_app.dto.DiaryDto

class DiaryRecyclerViewAdapter(val list: LiveData<List<DiaryDto>>) : RecyclerView.Adapter<DiaryRecyclerViewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<FragmentDiaryListBinding>(LayoutInflater.from(parent.context),
            R.layout.fragment_diary_list, parent, false)

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
            binding.textviewDiarylistTitle.text = diary.title
            binding.textviewDiarylistWriter.text = diary.author
//            val sdf = SimpleDateFormat("yyyy/MM/dd")
//            binding.textviewDiarylistDate.text = sdf.format(diary.writedDate)
        }
    }
}