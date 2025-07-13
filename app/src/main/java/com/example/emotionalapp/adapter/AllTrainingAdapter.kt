package com.example.emotionalapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
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
        private val contentLayout: LinearLayout = itemView.findViewById(R.id.content_layout)
        private val circularProgressBar: com.google.android.material.progressindicator.CircularProgressIndicator = itemView.findViewById(R.id.item_circular_gauge_bar)
        private val percentageTextView: TextView = itemView.findViewById(R.id.tv_circular_gauge_percentage)

        fun bind(trainingItem: TrainingItem) {
            titleTextView.text = trainingItem.title
            subtitleTextView.text = trainingItem.subtitle
            // currentProgress가 String 타입이라고 가정
            val progressValue = trainingItem.currentProgress.toIntOrNull() ?: 0
            circularProgressBar.progress = progressValue

            // 만약 currentProgress가 "진행중" 같은 텍스트라면 그대로, 숫자라면 숫자% 형태로 표시
            if (trainingItem.currentProgress.toIntOrNull() != null) {
                // 숫자 형태의 문자열이면 숫자% 로 표시
                percentageTextView.text = "${trainingItem.currentProgress}%"
            } else {
                // 숫자 형태가 아닌 문자열(예: "진행중", "완료")이면 해당 문자열을 그대로 표시
                percentageTextView.text = trainingItem.currentProgress
            }

            // 배경색 설정
            if (trainingItem.backgroundColorResId != null) {
                contentLayout.setBackgroundColor(
                    ContextCompat.getColor(itemView.context, trainingItem.backgroundColorResId)
                )
            } else {
                // 기본 배경색 설정 (예: XML에 정의된 기본색 또는 colors.xml의 다른 색)
                contentLayout.setBackgroundColor(
                    ContextCompat.getColor(itemView.context, R.color.pink) // 예시 기본색
                )
            }

            itemView.setOnClickListener {
                onItemClick(trainingItem)
            }
        }
    }
}
