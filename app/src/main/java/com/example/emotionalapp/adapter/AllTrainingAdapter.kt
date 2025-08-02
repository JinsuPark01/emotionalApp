package com.example.emotionalapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.emotionalapp.R
import com.example.emotionalapp.data.TrainingItem
import java.util.regex.Pattern

class AllTrainingAdapter (
    private var trainingList: List<TrainingItem>,
    private val onItemClick: (TrainingItem) -> Unit
) : RecyclerView.Adapter<AllTrainingAdapter.TrainingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_representative_training_button, parent, false)
        return TrainingViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: TrainingViewHolder, position: Int) {
        val trainingItem = trainingList[position]
        holder.bind(trainingItem)
    }

    override fun getItemCount(): Int = trainingList.size

    fun updateData(newTrainingList: List<TrainingItem>) {
        trainingList = newTrainingList
        notifyDataSetChanged() // DiffUtil 사용 권장
    }

    class TrainingViewHolder(
        itemView: View,
        private val onItemClick: (TrainingItem) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.tv_training_title)
        private val subtitleTextView: TextView = itemView.findViewById(R.id.tv_training_subtitle)
        private val contentLayout: LinearLayout = itemView.findViewById(R.id.content_layout)
        private val circularProgressBar: com.google.android.material.progressindicator.CircularProgressIndicator =
            itemView.findViewById(R.id.item_circular_gauge_bar)
        private val percentageTextView: TextView =
            itemView.findViewById(R.id.tv_circular_gauge_percentage)

        fun bind(trainingItem: TrainingItem) {
            titleTextView.text = trainingItem.title
            subtitleTextView.text = trainingItem.subtitle

            val progressString = trainingItem.currentProgress
            percentageTextView.text = progressString

            val progressValue = when (progressString) {
                "잠김" -> 0
                "GO" -> 100
                else -> {
                    // 예: "30%" → 숫자만 파싱
                    val regex = Regex("""\d+""")
                    val match = regex.find(progressString)?.value?.toIntOrNull() ?: 0
                    match.coerceIn(0, 100)
                }
            }
            circularProgressBar.progress = progressValue

            if (trainingItem.backgroundColorResId != null) {
                contentLayout.setBackgroundColor(
                    ContextCompat.getColor(itemView.context, trainingItem.backgroundColorResId)
                )
            } else {
                contentLayout.setBackgroundColor(
                    ContextCompat.getColor(itemView.context, R.color.pink)
                )
            }

            itemView.setOnClickListener {
                onItemClick(trainingItem)
            }
        }
    }
}
