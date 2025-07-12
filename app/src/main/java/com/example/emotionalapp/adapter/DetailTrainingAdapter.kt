package com.example.emotionalapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.emotionalapp.R
import com.example.emotionalapp.data.DetailTrainingItem

class DetailTrainingAdapter (
    private var trainingList: List<DetailTrainingItem>,
    private val onItemClick: (DetailTrainingItem) -> Unit // 항목 클릭 리스너
) : RecyclerView.Adapter<DetailTrainingAdapter.TrainingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainingViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_detail_training_button, parent, false) // 위에서 만든 항목 레이아웃
        return TrainingViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TrainingViewHolder, position: Int) {
        val currentItem = trainingList[position]
        holder.bind(currentItem, onItemClick)
    }

    override fun getItemCount() = trainingList.size

    // 데이터 업데이트 함수 (선택 사항이지만 유용함)
    fun updateData(newTrainingList: List<DetailTrainingItem>) {
        trainingList = newTrainingList
        notifyDataSetChanged() // DiffUtil 사용을 권장하지만, 간단히 notifyDataSetChanged 사용
    }

    class TrainingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.tv_training_title)
        private val subTitleTextView: TextView = itemView.findViewById(R.id.tv_training_subtitle)
        private val contentLayout: LinearLayout = itemView.findViewById(R.id.content_layout)
        val circularProgressBar: com.google.android.material.progressindicator.CircularProgressIndicator = itemView.findViewById(R.id.item_circular_gauge_bar)
        val percentageTextView: TextView = itemView.findViewById(R.id.tv_circular_gauge_percentage)

        fun bind(trainingItem: DetailTrainingItem, onItemClick: (DetailTrainingItem) -> Unit) {
            titleTextView.text = trainingItem.title
            subTitleTextView.text = trainingItem.subtitle
            circularProgressBar.progress = trainingItem.currentProgress
            percentageTextView.text = "${trainingItem.currentProgress}%"

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

            // 항목 전체에 대한 클릭 리스너 설정
            itemView.setOnClickListener {
                onItemClick(trainingItem)
            }
        }
    }
}