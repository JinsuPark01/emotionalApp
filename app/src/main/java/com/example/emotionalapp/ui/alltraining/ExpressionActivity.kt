package com.example.emotionalapp.ui.alltraining

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.emotionalapp.R
import com.example.emotionalapp.adapter.DetailTrainingAdapter
import com.example.emotionalapp.data.DetailTrainingItem
import com.example.emotionalapp.data.TrainingType
import com.example.emotionalapp.databinding.ActivityExpressionActionTrainingBinding
import com.example.emotionalapp.ui.expression.AvoidanceActivity
import com.example.emotionalapp.ui.expression.DrivenActionActivity
import com.example.emotionalapp.ui.weekly.WeeklyActivity

class ExpressionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExpressionActionTrainingBinding
    private lateinit var adapter: DetailTrainingAdapter

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
        adapter = DetailTrainingAdapter(emptyList()) { item ->
            item.targetActivityClass?.let { targetClass ->
                val intent = Intent(this, targetClass)
                startActivity(intent)
            }
        }
        binding.actionTrainingRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.actionTrainingRecyclerView.adapter = adapter
    }

    private fun loadTrainingData() {
        val trainingList = listOf(
            DetailTrainingItem(
                id = "weekly_training",
                title = "주차별 점검",
                subtitle = "질문지를 통한 마음 돌아보기",
                trainingType = TrainingType.EXPRESSION_ACTION_TRAINING,
                currentProgress = "진행하기",
                backgroundColorResId = R.color.purple_700,
                targetActivityClass = WeeklyActivity::class.java
            ),
            DetailTrainingItem(
                id = "avoidance_training",
                title = "정서회피 훈련",
                subtitle = "정서와 관련된 신체 감각 찾기",
                trainingType = TrainingType.EXPRESSION_ACTION_TRAINING,
                currentProgress = "0/3",
                backgroundColorResId = R.color.purple_700,
                targetActivityClass = AvoidanceActivity::class.java
            ),
            DetailTrainingItem(
                id = "driven_action_training",
                title = "정서-주도 행동 훈련",
                subtitle = "특별한 경험을 기록하기",
                trainingType = TrainingType.EXPRESSION_ACTION_TRAINING,
                currentProgress = "50",
                backgroundColorResId = R.color.purple_500,
                targetActivityClass = DrivenActionActivity::class.java
            )
        )
        adapter.updateData(trainingList)
    }
}