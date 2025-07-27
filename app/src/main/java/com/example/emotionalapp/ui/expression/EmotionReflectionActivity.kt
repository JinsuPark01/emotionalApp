package com.example.emotionalapp.ui.expression

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.R
import com.example.emotionalapp.databinding.ActivityTrainingFrameBinding

class EmotionReflectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrainingFrameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrainingFrameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()

        binding.btnBack.setOnClickListener { finish() }

        binding.navPage.btnNext.setOnClickListener {
            // contentBinding을 통해 입력값에 접근합니다.
            val clarifiedEmotion = binding.pageContainer.findViewById<android.widget.EditText>(R.id.edit_text_emotion_clarified).text.toString().trim()
            val moodChanged = binding.pageContainer.findViewById<android.widget.EditText>(R.id.edit_text_mood_changed).text.toString().trim()

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
            finish()
        }
    }

    private fun setupUI() {
        // 상단 바 설정
        binding.titleText.text = "감정 기록하기"
        binding.tabRecord.visibility = View.GONE
        binding.underlineRecord.visibility = View.GONE
        binding.tabPracticeContainer.layoutParams = (binding.tabPracticeContainer.layoutParams as LinearLayout.LayoutParams).apply {
            width = 0
            weight = 2f
        }

        // 하단 네비게이션 바 설정
        binding.navPage.indicatorContainer.visibility = View.GONE
        binding.navPage.btnNext.text = "완료"

        // 기록 페이지 레이아웃을 pageContainer에 추가
        layoutInflater.inflate(R.layout.content_emotion_reflection, binding.pageContainer, true)
    }
}