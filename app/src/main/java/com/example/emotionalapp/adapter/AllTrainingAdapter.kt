package com.example.emotionalapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.emotionalapp.R
import com.example.emotionalapp.data.TrainingItem

class AllTrainingAdapter(
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
        // private val arrowImageView: ImageView = itemView.findViewById(R.id.img_arrow_indicator) // 필요하다면 참조

        fun bind(trainingItem: TrainingItem) {
            titleTextView.text = trainingItem.title
            subtitleTextView.text = trainingItem.subtitle

            // 서브타이틀이 비어있거나 null이면 숨기기 (선택적 UI 개선)
            if (trainingItem.subtitle.isNullOrEmpty()) {
                subtitleTextView.visibility = View.GONE
            } else {
                subtitleTextView.visibility = View.VISIBLE
            }

            itemView.setOnClickListener {
                onItemClick(trainingItem)
            }
        }
    }
}
