package com.example.emotionalapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.emotionalapp.R
import com.example.emotionalapp.data.ReportItem

class ReportAdapter(
    private val items: List<ReportItem>,
    private val onItemClick: (ReportItem) -> Unit
) : RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    inner class ReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateText: TextView = itemView.findViewById(R.id.report_time)
        private val nameText: TextView = itemView.findViewById(R.id.report_name)

        fun bind(item: ReportItem) {
            dateText.text = item.date
            nameText.text = item.name

            // 배경색 설정
            val contentLayout = itemView.findViewById<View>(R.id.content_layout)
            val colorResId = item.backgroundColorResId
            if (colorResId != null) {
                contentLayout.setBackgroundColor(
                    ContextCompat.getColor(itemView.context, colorResId)
                )
            } else {
                contentLayout.setBackgroundColor(
                    ContextCompat.getColor(itemView.context, R.color.pink)
                )
            }

            itemView.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_training_report_button, parent, false)
        return ReportViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
