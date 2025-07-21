package com.example.emotionalapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.emotionalapp.R


class BodyScanRecordAdapter(
    private var dates: List<String>
) : RecyclerView.Adapter<BodyScanRecordAdapter.VH>() {

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate: TextView = itemView.findViewById(R.id.tvRecordDate)
        val btnCheck: TextView = itemView.findViewById(R.id.btnCheck)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(
            LayoutInflater.from(parent.context)
            .inflate(R.layout.item_body_scan_record, parent, false))

    override fun getItemCount() = dates.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.tvDate.text = dates[position]
        holder.btnCheck.setOnClickListener {
            //기록 상세 보기 기능
        }
    }
    fun updateData(newDates: List<String>) {
        dates = newDates
        notifyDataSetChanged()
    }
}
