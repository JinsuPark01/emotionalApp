package com.example.emotionalapp.ui.expression

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.R
import com.example.emotionalapp.databinding.ActivityDrivenActionBinding

class DrivenActionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDrivenActionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrivenActionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnTopic1.setOnClickListener { navigateToDetail("정서-주도 행동이란?", R.string.topic_content_what_is_driven_action) }
        binding.btnTopic2.setOnClickListener { navigateToDetail("적응적 행동", R.string.topic_content_adaptive_actions) }
        binding.btnTopic3.setOnClickListener { navigateToDetail("부적응적 행동", R.string.topic_content_maladaptive_actions) }
        binding.btnTopic4.setOnClickListener { navigateToDetail("정리", R.string.topic_content_driven_action_summary) }

        binding.btnQuiz.setOnClickListener {
            startActivity(Intent(this, DrivenActionQuizActivity::class.java))
        }

        // --- 여기가 핵심 수정 부분입니다 ---
        binding.btnOppositeAction.setOnClickListener {
            startActivity(Intent(this, OppositeActionActivity::class.java))
        }
    }

    private fun navigateToDetail(topicTitle: String, contentResId: Int) {
        val intent = Intent(this, TopicDetailActivity::class.java).apply {
            putExtra("TOPIC_TITLE", topicTitle)
            putExtra("TOPIC_CONTENT_RES_ID", contentResId)
        }
        startActivity(intent)
    }
}