package com.example.emotionalapp.ui.expression

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.databinding.ActivityAvoidanceGuideBinding

class AvoidanceGuideActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAvoidanceGuideBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAvoidanceGuideBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnStartQuiz.setOnClickListener {
            startActivity(Intent(this, EmotionAvoidanceQuizActivity::class.java))
        }

        // --- 여기가 핵심 수정 부분입니다 ---
        // 스크롤 뷰에 스크롤 리스너 추가
        binding.guideScrollView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            // v를 ScrollView로 캐스팅하여 자식 뷰에 접근합니다.
            val scrollView = v as ScrollView
            val childView = scrollView.getChildAt(0)

            if (childView != null) {
                val diff = (childView.bottom - (scrollView.height + scrollView.scrollY))
                if (diff <= 0) {
                    // 스크롤이 맨 아래에 닿았을 때
                    binding.btnStartQuiz.visibility = View.VISIBLE
                } else {
                    // 스크롤이 맨 아래에 있지 않을 때
                    binding.btnStartQuiz.visibility = View.GONE
                }
            }
        }
    }
}