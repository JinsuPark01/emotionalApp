package com.example.emotionalapp.ui.expression

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.emotionalapp.R
import com.example.emotionalapp.adapter.DetailTrainingAdapter
import com.example.emotionalapp.data.DetailTrainingItem
import com.example.emotionalapp.data.TrainingType
import com.example.emotionalapp.databinding.ActivityDrivenActionBinding

class DrivenActionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDrivenActionBinding
    private lateinit var adapter: DetailTrainingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrivenActionBinding.inflate(layoutInflater)
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
                startActivity(Intent(this, targetClass))
            }
        }
        binding.trainingRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.trainingRecyclerView.adapter = adapter
    }

    private fun loadTrainingData() {
        val trainingList = listOf(
            DetailTrainingItem("guide", "교육", "정서-주도 행동에 대해 알아보기", TrainingType.EXPRESSION_ACTION_TRAINING, "내용 보기", R.color.purple_700, DrivenActionGuideActivity::class.java),
            DetailTrainingItem("opposite_action", "반대 행동 하기", "감정과 반대로 행동하는 연습", TrainingType.EXPRESSION_ACTION_TRAINING, "시작하기", R.color.purple_500, OppositeActionActivity::class.java),
            DetailTrainingItem("alternative_action", "대안 행동 찾기", "감정을 다루는 다른 방법 찾기", TrainingType.EXPRESSION_ACTION_TRAINING, "시작하기", R.color.purple_500, AlternativeActionActivity::class.java)
        )
        adapter.updateData(trainingList)
    }
}