package com.example.emotionalapp.ui.expression

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.emotionalapp.R
import com.example.emotionalapp.adapter.DetailTrainingAdapter
import com.example.emotionalapp.data.DetailTrainingItem
import com.example.emotionalapp.data.TrainingType
// --- 여기가 핵심 수정 부분입니다 ---
import com.example.emotionalapp.databinding.ActivityExpressionAvoidanceBinding // 바인딩 클래스 이름 변경
import com.example.emotionalapp.ui.emotion.EmotionAvoidanceQuizActivity

class AvoidanceActivity : AppCompatActivity() {

    // --- 변수 타입 변경 ---
    private lateinit var binding: ActivityExpressionAvoidanceBinding
    private lateinit var adapter: DetailTrainingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // --- inflate하는 클래스 이름 변경 ---
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
                // TODO: 각 내용에 맞는 데이터를 Intent에 담아 전달하는 로직 추가 필요
                if (targetClass == TopicDetailActivity::class.java) {
                    when(item.id) {
                        "topic1" -> intent.putExtra("TOPIC_CONTENT_RES_ID", R.string.topic_content_what_is_avoidance)
                        "topic2" -> intent.putExtra("TOPIC_CONTENT_RES_ID", R.string.topic_content_emotions_to_avoid)
                        "topic3" -> intent.putExtra("TOPIC_CONTENT_RES_ID", R.string.topic_content_short_term_effects)
                        "topic4" -> intent.putExtra("TOPIC_CONTENT_RES_ID", R.string.topic_content_long_term_effects)
                    }
                    intent.putExtra("TOPIC_TITLE", item.title)
                }
                startActivity(intent)
            }
        }
        // --- RecyclerView ID 수정 ---
        binding.trainingRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.trainingRecyclerView.adapter = adapter
    }

    private fun loadTrainingData() {
        val trainingList = listOf(
            DetailTrainingItem("topic1", "정서 회피란?", "", TrainingType.EXPRESSION_ACTION_TRAINING, "내용 보기", R.color.purple_700, TopicDetailActivity::class.java),
            DetailTrainingItem("topic2", "회피해도 괜찮은 정서 vs ...", "", TrainingType.EXPRESSION_ACTION_TRAINING, "내용 보기", R.color.purple_700, TopicDetailActivity::class.java),
            DetailTrainingItem("topic3", "정서 회피의 단기적 효과", "", TrainingType.EXPRESSION_ACTION_TRAINING, "내용 보기", R.color.purple_700, TopicDetailActivity::class.java),
            DetailTrainingItem("topic4", "정서 회피의 장기적 결과", "", TrainingType.EXPRESSION_ACTION_TRAINING, "내용 보기", R.color.purple_700, TopicDetailActivity::class.java),
            DetailTrainingItem("quiz", "퀴즈", "", TrainingType.EXPRESSION_ACTION_TRAINING, "풀어보기", R.color.purple_500, EmotionAvoidanceQuizActivity::class.java),
            DetailTrainingItem("diary", "회피 일지 작성하기", "", TrainingType.EXPRESSION_ACTION_TRAINING, "작성하기", R.color.purple_500, AvoidanceChecklistActivity::class.java),
            DetailTrainingItem("timer", "정서 머무르기", "", TrainingType.EXPRESSION_ACTION_TRAINING, "시작하기", R.color.purple_500, EmotionSelectionActivity::class.java)
        )

        adapter.updateData(trainingList)
    }
}