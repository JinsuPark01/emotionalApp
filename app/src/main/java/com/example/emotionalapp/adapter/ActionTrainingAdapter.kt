package com.example.emotionalapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.emotionalapp.R
import com.example.emotionalapp.data.ActionTrainingItem
import com.google.android.material.progressindicator.CircularProgressIndicator

class ActionTrainingAdapter(
    private val onItemClick: (ActionTrainingItem) -> Unit
) : RecyclerView.Adapter<ActionTrainingAdapter.ActionViewHolder>() {
    private var trainingList: List<ActionTrainingItem> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActionViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_action_training_button, parent, false)
        return ActionViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ActionViewHolder, position: Int) {
        holder.bind(trainingList[position], onItemClick)
    }

    override fun getItemCount() = trainingList.size

    fun submitList(newList: List<ActionTrainingItem>) {
        trainingList = newList
        notifyDataSetChanged()
    }

    class ActionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val iconImageView: ImageView = itemView.findViewById(R.id.iv_action_icon)
        private val titleTextView: TextView = itemView.findViewById(R.id.tv_action_title)
        private val subTitleTextView: TextView = itemView.findViewById(R.id.tv_action_subtitle)
        private val contentLayout: LinearLayout = itemView.findViewById(R.id.content_layout)
        // --- 프로그레스 바 관련 뷰를 찾도록 수정 ---
        private val circularProgressBar: CircularProgressIndicator = itemView.findViewById(R.id.item_circular_gauge_bar)
        private val percentageTextView: TextView = itemView.findViewById(R.id.tv_circular_gauge_percentage)

        fun bind(trainingItem: ActionTrainingItem, onItemClick: (ActionTrainingItem) -> Unit) {
            iconImageView.setImageResource(trainingItem.iconResId)
            titleTextView.text = trainingItem.title
            subTitleTextView.text = trainingItem.subtitle
            contentLayout.setBackgroundColor(ContextCompat.getColor(itemView.context, trainingItem.backgroundColorResId))
            itemView.setOnClickListener { onItemClick(trainingItem) }

            // --- 프로그레스 바를 제어하는 로직 추가 ---
            val progress = trainingItem.progressText.toIntOrNull()
            if (progress != null) {
                circularProgressBar.progress = progress
                percentageTextView.text = "$progress%"
            } else {
                circularProgressBar.progress = 0
                percentageTextView.text = trainingItem.progressText
            }
        }
    }
}