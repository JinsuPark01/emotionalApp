package com.example.emotionalapp.ui.emotion

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.R
import com.example.emotionalapp.databinding.ActivityEmotionAvoidanceBinding

class EmotionAvoidanceActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityEmotionAvoidanceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmotionAvoidanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        // 모든 버튼에 클릭 리스너 설정
        binding.btnTopic1.setOnClickListener(this)
        binding.btnTopic2.setOnClickListener(this)
        binding.btnTopic3.setOnClickListener(this)
        binding.btnTopic4.setOnClickListener(this)
        // '정리' 버튼에 대한 리스너 설정을 삭제했습니다.
        binding.btnTopic6.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        // 클릭된 버튼에 따라 (제목, 내용 리소스 ID) 쌍을 가져옴
        val (title, contentResId) = when (v?.id) {
            binding.btnTopic1.id -> Pair("정서 회피란?", R.string.topic_content_what_is_avoidance)
            binding.btnTopic2.id -> Pair("회피해도 괜찮은 정서 vs 회피하면 안 되는 정서", R.string.topic_content_emotions_to_avoid)
            binding.btnTopic3.id -> Pair("정서 회피의 단기적 효과", R.string.topic_content_short_term_effects)
            binding.btnTopic4.id -> Pair("정서 회피의 장기적 결과", R.string.topic_content_long_term_effects)
            // '정리' 버튼에 대한 case를 삭제했습니다.
            binding.btnTopic6.id -> Pair("퀴즈", 0)
            else -> Pair("", 0)
        }
        navigateToDetail(title, contentResId)
    }

    private fun navigateToDetail(topicTitle: String, contentResId: Int) {
        if (topicTitle.isNotEmpty()) {
            val intent = Intent(this, TopicDetailActivity::class.java).apply {
                putExtra("TOPIC_TITLE", topicTitle)
                putExtra("TOPIC_CONTENT_RES_ID", contentResId)
            }
            startActivity(intent)
        }
    }
}