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

        // 분수 형태를 파싱하기 위한 정규식
        private val fractionPattern = Pattern.compile("^(\\d+)/(\\d+)$")

        fun bind(trainingItem: TrainingItem) {
            titleTextView.text = trainingItem.title
            subtitleTextView.text = trainingItem.subtitle

            val progressString = trainingItem.currentProgress // currentProgress는 String 타입이라고 가정
            var progressValue = 0 // CircularProgressIndicator에 설정할 최종 값
            var displayPercentageText = "" // TextView에 표시할 텍스트

            // 1. 일반 정수인지 시도
            val intValue = progressString.toIntOrNull()
            if (intValue != null) {
                progressValue = intValue
                displayPercentageText = "$progressValue%"
            } else {
                // 2. "숫자/숫자" 형태의 분수인지 시도
                val matcher = fractionPattern.matcher(progressString)
                if (matcher.matches()) {
                    try {
                        val numerator = matcher.group(1)?.toDoubleOrNull() // 첫 번째 그룹 (분자)
                        val denominator = matcher.group(2)?.toDoubleOrNull() // 두 번째 그룹 (분모)

                        if (numerator != null && denominator != null && denominator != 0.0) {
                            val fractionValue = (numerator / denominator * 100).toInt()
                            progressValue = fractionValue
                            displayPercentageText = progressString
                        } else {
                            // 분모가 0이거나, 분자/분모가 숫자가 아닌 경우
                            progressValue = 0
                            displayPercentageText = progressString // 원래 문자열 표시 (예: "진행중") 또는 "0%"
                        }
                    } catch (e: Exception) {
                        // 파싱 중 예외 발생 시
                        progressValue = 0
                        displayPercentageText = progressString // 또는 "오류" 등
                        Log.e("DetailTrainingAdapter", "Error parsing fraction: $progressString", e)
                    }
                } else {
                    // 3. 정수도 아니고, 분수 형태도 아닌 경우 (예: "진행중", "완료" 등)
                    progressValue = 0 // 프로그레스바는 0으로
                    displayPercentageText = progressString // TextView에는 원래 문자열 그대로 표시
                }
            }

            circularProgressBar.progress = progressValue
            percentageTextView.text = displayPercentageText

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
