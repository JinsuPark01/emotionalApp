package com.example.emotionalapp.ui.expression

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
            val situation = binding.etSituation.text.toString().trim()
            val emotion = binding.etEmotion.text.toString().trim()
            val method = binding.etMethod.text.toString().trim()
            val result = binding.etResult.text.toString().trim()

            if (situation.isNotBlank() && emotion.isNotBlank() && method.isNotBlank() && result.isNotBlank()) {
                // TODO: 여기에 수집된 데이터를 DB에 저장하는 로직을 추가해야 합니다.

                // --- 여기가 핵심 수정 부분입니다 ---
                // 토스트 메시지 대신 팝업을 띄웁니다.
                showCompletionDialog()

            } else {
                Toast.makeText(this, "모든 항목을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showCompletionDialog() {
        AlertDialog.Builder(this)
            .setTitle("기록 완료!")
            .setMessage("감정을 회피하는 습관을 돌아봤다는 것 자체가 이미 중요한 변화의 시작이에요. 스스로를 마주한 용기를 진심으로 응원해요!")
            .setPositiveButton("확인") { dialog, which ->
                // '확인' 버튼을 누르면 이전 화면으로 돌아갑니다.
                finish()
            }
            .setCancelable(false)
            .show()
    }
}