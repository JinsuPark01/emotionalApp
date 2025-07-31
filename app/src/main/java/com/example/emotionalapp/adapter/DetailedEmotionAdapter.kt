package com.example.emotionalapp.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.emotionalapp.R

class DetailedEmotionAdapter(
    private val items: List<String>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<DetailedEmotionAdapter.ViewHolder>() {

    private var selectedPosition = -1

    class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_detailed_emotion, parent, false) as TextView
        return ViewHolder(textView)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.textView.text = item

        // 선택 상태에 따라 배경색 변경
        if (position == selectedPosition) {
            holder.textView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.purple_200))
        } else {
            holder.textView.setBackgroundResource(R.drawable.edit_text_background)
        }

        holder.itemView.setOnClickListener {
            onItemClick(item)
            // 선택된 아이템의 위치를 저장하고 UI 갱신
            val previousPosition = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
        }
    }
    override fun getItemCount() = items.size
}