package com.example.emotionalapp.ui.expression

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.databinding.ActivityDrivenActionGuideBinding

class DrivenActionGuideActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDrivenActionGuideBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrivenActionGuideBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnStartQuiz.setOnClickListener {
            startActivity(Intent(this, DrivenActionQuizActivity::class.java))
        }

        binding.guideScrollView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            val scrollView = v as ScrollView
            val childView = scrollView.getChildAt(0)

            if (childView != null) {
                val diff = (childView.bottom - (scrollView.height + scrollView.scrollY))
                if (diff <= 0) {
                    binding.btnStartQuiz.text = "퀴즈 풀어보기" // 텍스트를 "퀴즈 풀어보기"로 설정
                    binding.btnStartQuiz.visibility = View.VISIBLE
                } else {
                    binding.btnStartQuiz.visibility = View.GONE
                }
            }
        }
    }

}