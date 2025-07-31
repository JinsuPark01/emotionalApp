package com.example.emotionalapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.emotionalapp.R

class DetailedEmotionAdapter(
    private val items: List<String>,
    private val initialSelectedPosition: Int,
    private val onItemClick: (Int, String) -> Unit
) : RecyclerView.Adapter<DetailedEmotionAdapter.ViewHolder>() {

    private var selectedPosition = initialSelectedPosition

    class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_detailed_emotion, parent, false) as TextView
        return ViewHolder(textView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.textView.text = item

        if (position == selectedPosition) {
            holder.textView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.purple_200))
        } else {
            holder.textView.setBackgroundResource(R.drawable.edit_text_background)
        }

        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
            onItemClick(selectedPosition, item)
        }
    }

    override fun getItemCount() = items.size
}