package com.example.emotionalapp.ui.expression

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.databinding.ActivityAvoidanceDiaryFormBinding
import com.example.emotionalapp.ui.alltraining.AllTrainingPageActivity

class AvoidanceDiaryFormActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAvoidanceDiaryFormBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAvoidanceDiaryFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        binding.btnSaveDiary.setOnClickListener {
            val situation = binding.etSituation.text.toString()
            val emotion = binding.etEmotion.text.toString()
            val method = binding.etMethod.text.toString()
            val result = binding.etResult.text.toString()

            if (situation.isNotBlank() && emotion.isNotBlank() && method.isNotBlank() && result.isNotBlank()) {
                Toast.makeText(this, "일지가 저장되었습니다.", Toast.LENGTH_SHORT).show()

                // --- 여기가 핵심 수정 부분입니다 ---
                // 새로운 메인 화면으로 이동하는 인텐트 생성
                val intent = Intent(this, AllTrainingPageActivity::class.java).apply {
                    // 이전에 열렸던 모든 액티비티를 스택에서 제거하고, 새로운 태스크를 시작
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                finish() // 현재 액티비티 종료

            } else {
                Toast.makeText(this, "모든 항목을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}