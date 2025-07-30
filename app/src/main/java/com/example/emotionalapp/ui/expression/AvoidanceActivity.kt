package com.example.emotionalapp.ui.expression

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.emotionalapp.R
import com.example.emotionalapp.adapter.DetailTrainingAdapter
import com.example.emotionalapp.data.DetailTrainingItem
import com.example.emotionalapp.data.TrainingType
import com.example.emotionalapp.databinding.ActivityExpressionAvoidanceBinding
import com.example.emotionalapp.ui.emotion.EmotionAvoidanceQuizActivity

class AvoidanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExpressionAvoidanceBinding
    private lateinit var adapter: DetailTrainingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpressionAvoidanceBinding.inflate(layoutInflater)
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
        binding.trainingRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.trainingRecyclerView.adapter = adapter
    }

    private fun loadTrainingData() {
        val trainingList = listOf(
            DetailTrainingItem("guide", "교육", "정서회피 훈련에 대해 알아보기", TrainingType.EXPRESSION_ACTION_TRAINING, "1", "1","내용 보기", R.color.purple_700, AvoidanceGuideActivity::class.java),

            DetailTrainingItem("diary", "회피 일지 작성하기", "나의 회피 습관을 기록하고 관찰하기", TrainingType.EXPRESSION_ACTION_TRAINING, "1", "1","작성하기", R.color.purple_500, AvoidanceChecklistActivity::class.java),
            DetailTrainingItem("timer", "정서 머무르기", "감정을 피하지 않고 느껴보는 연습", TrainingType.EXPRESSION_ACTION_TRAINING, "1", "1","시작하기", R.color.purple_500, EmotionSelectionActivity::class.java)
        )
        adapter.updateData(trainingList)
    }
}