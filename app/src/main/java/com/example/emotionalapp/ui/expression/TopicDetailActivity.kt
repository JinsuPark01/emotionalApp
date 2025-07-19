package com.example.emotionalapp.ui.expression

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.databinding.ActivityExpressionTopicDetailBinding

class TopicDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExpressionTopicDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpressionTopicDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Intent로부터 토픽 제목과 내용 리소스 ID 받기
        val topicTitle = intent.getStringExtra("TOPIC_TITLE") ?: "상세 내용"
        val contentResId = intent.getIntExtra("TOPIC_CONTENT_RES_ID", 0)

        // 제목 설정
        binding.tvPageTitle.text = topicTitle

        // 내용 설정
        if (contentResId != 0) {
            binding.tvTopicContent.text = getText(contentResId)
        } else {
            binding.tvTopicContent.text = "상세 내용이 준비 중입니다."
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}