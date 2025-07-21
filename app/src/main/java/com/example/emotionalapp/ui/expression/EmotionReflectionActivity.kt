package com.example.emotionalapp.ui.expression

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.databinding.ActivityEmotionReflectionBinding
import com.example.emotionalapp.ui.alltraining.ExpressionActivity

class EmotionReflectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmotionReflectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmotionReflectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnComplete.setOnClickListener {
            // TODO: 입력된 텍스트를 데이터베이스나 다른 곳에 저장하는 로직 추가
            val clarifiedEmotion = binding.editTextEmotionClarified.text.toString().trim()
            val moodChanged = binding.editTextMoodChanged.text.toString().trim()

            Toast.makeText(this, "기록이 저장되었습니다.", Toast.LENGTH_SHORT).show()

            // --- 여기가 핵심 수정 부분입니다 ---
            // 완료 후 피드백 화면으로 이동
            val intent = Intent(this, EmotionFeedbackActivity::class.java)
            startActivity(intent)
            finish() // 현재 감정 기록 화면은 종료
        }
    }

    // --- 기존 returnToTrainingList() 함수는 이제 피드백 액티비티가 담당합니다 ---
}