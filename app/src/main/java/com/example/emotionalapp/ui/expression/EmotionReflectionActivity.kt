package com.example.emotionalapp.ui.expression

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.R
import com.example.emotionalapp.databinding.ActivityEmotionReflectionBinding // 바인딩 클래스 이름 변경

class EmotionReflectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmotionReflectionBinding
    private val totalPages = 3 // 타이머, 기록, 피드백 총 3단계
    private val currentPage = 1 // 이 화면은 2번째 페이지

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmotionReflectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupClickListeners()
    }

    private fun setupUI() {
        // 하단 네비게이션 바 설정
        binding.navPage.btnNext.text = "완료"
        setupIndicators()
        updateIndicators()
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.navPage.btnNext.setOnClickListener {
            val clarifiedEmotion = binding.editTextEmotionClarified.text.toString().trim()
            val moodChanged = binding.editTextMoodChanged.text.toString().trim()

            if (clarifiedEmotion.isBlank() || moodChanged.isBlank()){
                Toast.makeText(this, "모든 항목을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // TODO: clarifiedEmotion, moodChanged 변수를 DB에 저장

            Toast.makeText(this, "기록이 저장되었습니다.", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, EmotionFeedbackActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.navPage.btnPrev.setOnClickListener {
            finish() // 이전 화면으로 돌아감 (타이머 액티비티)
        }
    }

    private fun setupIndicators() {
        val indicatorContainer = binding.navPage.indicatorContainer
        indicatorContainer.removeAllViews()
        for (i in 0 until totalPages) {
            val dot = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(20, 20).apply {
                    setMargins(8, 0, 8, 0)
                }
                setBackgroundResource(R.drawable.ic_dot_circle_gray)
            }
            indicatorContainer.addView(dot)
        }
    }

    private fun updateIndicators() {
        val indicatorContainer = binding.navPage.indicatorContainer
        for (i in 0 until indicatorContainer.childCount) {
            val dot = indicatorContainer.getChildAt(i)
            dot.setBackgroundResource(
                if (i == currentPage) R.drawable.ic_dot_circle_black
                else R.drawable.ic_dot_circle_gray
            )
        }
    }
}