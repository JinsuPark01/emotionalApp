package com.example.emotionalapp.ui.expression
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.R
import com.example.emotionalapp.databinding.ActivityExpressionAvoidanceBinding
import com.example.emotionalapp.ui.emotion.EmotionAvoidanceQuizActivity

class AvoidanceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExpressionAvoidanceBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpressionAvoidanceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { finish() }
        binding.btnTopic1.setOnClickListener { navigateToDetail("정서 회피란?", R.string.topic_content_what_is_avoidance) }
        binding.btnTopic2.setOnClickListener { navigateToDetail("회피해도 괜찮은 정서 vs 회피하면 안 되는 정서", R.string.topic_content_emotions_to_avoid) }
        binding.btnTopic3.setOnClickListener { navigateToDetail("정서 회피의 단기적 효과", R.string.topic_content_short_term_effects) }
        binding.btnTopic4.setOnClickListener { navigateToDetail("정서 회피의 장기적 결과", R.string.topic_content_long_term_effects) }
        binding.btnWriteDiary.setOnClickListener { startActivity(Intent(this, AvoidanceChecklistActivity::class.java)) }
        binding.btnTopic6.setOnClickListener { startActivity(Intent(this, EmotionAvoidanceQuizActivity::class.java)) }
    }

    private fun navigateToDetail(topicTitle: String, contentResId: Int) {
        if (contentResId != 0) {
            val intent = Intent(this, TopicDetailActivity::class.java).apply {
                putExtra("TOPIC_TITLE", topicTitle)
                putExtra("TOPIC_CONTENT_RES_ID", contentResId)
            }
            startActivity(intent)
        }
    }
}