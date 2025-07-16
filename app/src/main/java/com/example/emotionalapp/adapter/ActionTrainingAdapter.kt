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

class ActionTrainingAdapter(
    private val onItemClick: (ActionTrainingItem) -> Unit
) : RecyclerView.Adapter<ActionTrainingAdapter.ActionViewHolder>() {

    // 리스트를 비공개로 만들고, 외부에서 업데이트할 함수를 제공합니다.
    private var trainingList: List<ActionTrainingItem> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActionViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_action_training_button, parent, false)
        return ActionViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ActionViewHolder, position: Int) {
        val currentItem = trainingList[position]
        holder.bind(currentItem, onItemClick)
    }

    override fun getItemCount() = trainingList.size

    // 데이터를 업데이트하기 위한 공개 함수
    fun submitList(newList: List<ActionTrainingItem>) {
        trainingList = newList
        notifyDataSetChanged() // 데이터가 변경되었음을 어댑터에 알립니다.
    }

    class ActionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val iconImageView: ImageView = itemView.findViewById(R.id.iv_action_icon)
        private val titleTextView: TextView = itemView.findViewById(R.id.tv_action_title)
        private val subTitleTextView: TextView = itemView.findViewById(R.id.tv_action_subtitle)
        private val progressTextView: TextView = itemView.findViewById(R.id.tv_action_progress_text)
        private val contentLayout: LinearLayout = itemView.findViewById(R.id.content_layout)

        fun bind(trainingItem: ActionTrainingItem, onItemClick: (ActionTrainingItem) -> Unit) {
            iconImageView.setImageResource(trainingItem.iconResId)
            titleTextView.text = trainingItem.title
            subTitleTextView.text = trainingItem.subtitle
            progressTextView.text = trainingItem.progressText

            contentLayout.setBackgroundColor(
                ContextCompat.getColor(itemView.context, trainingItem.backgroundColorResId)
            )

            itemView.setOnClickListener {
                onItemClick(trainingItem)
            }
        }
    }
}