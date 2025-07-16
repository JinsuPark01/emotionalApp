package com.example.emotionalapp.ui.alltraining

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.emotionalapp.R
import com.example.emotionalapp.adapter.ActionTrainingAdapter
import com.example.emotionalapp.data.ActionTrainingItem
import com.example.emotionalapp.databinding.ActivityExpressionActionTrainingBinding

class ExpressionActionTrainingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExpressionActionTrainingBinding
    private lateinit var adapter: ActionTrainingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpressionActionTrainingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        loadTrainingData()

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        adapter = ActionTrainingAdapter { item ->
            val intent = Intent(this, item.targetActivityClass)
            startActivity(intent)
        }
        binding.actionTrainingRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.actionTrainingRecyclerView.adapter = adapter
    }

    private fun loadTrainingData() {
        val trainingList = listOf(
            ActionTrainingItem(
                id = "avoidance_training",
                iconResId = R.drawable.outline_person_24,
                title = "정서회피 훈련",
                subtitle = "정서와 관련된 신체 감각 찾기",
                progressText = "",
                backgroundColorResId = R.color.purple_700,
                targetActivityClass = com.example.emotionalapp.ui.emotion.EmotionAvoidanceActivity::class.java
            ),
            ActionTrainingItem(
                id = "driven_action_training",
                iconResId = R.drawable.ic_chat,
                title = "정서-주도 행동 훈련",
                subtitle = "특별한 경험을 기록하기",
                progressText = "",
                backgroundColorResId = R.color.purple_500,
                targetActivityClass = ExpressionActionActivity::class.java
            )
        )
        adapter.submitList(trainingList)
    }
}