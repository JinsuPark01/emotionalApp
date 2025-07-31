package com.example.emotionalapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.emotionalapp.R
import com.example.emotionalapp.data.AlternativeActionItem

class AlternativeActionAdapter(
    private val items: List<AlternativeActionItem>,
    private val onItemClick: (AlternativeActionItem) -> Unit
) : RecyclerView.Adapter<AlternativeActionAdapter.ViewHolder>() {

    private var selectedPosition = -1

    class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alternative_action, parent, false) as TextView
        return ViewHolder(textView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.textView.text = item.actionText

        if (position == selectedPosition) {
            holder.textView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.purple_200))
        } else {
            holder.textView.setBackgroundResource(R.drawable.edit_text_background)
        }

        holder.itemView.setOnClickListener {
            onItemClick(item)

            // 이전에 선택된 아이템과 새로 선택된 아이템의 UI를 모두 갱신합니다.
            val previousPosition = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
        }
    }

    override fun getItemCount() = items.size
}