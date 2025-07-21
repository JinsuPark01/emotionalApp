package com.example.emotionalapp.ui.expression

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.R
import com.example.emotionalapp.databinding.ActivityOppositeActionBinding

class OppositeActionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOppositeActionBinding
    private var currentPage = 0
    private val totalPages = 3 // 가이드, 기록지, 마무리 총 3페이지

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOppositeActionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updatePage()

        binding.btnBack.setOnClickListener { finish() }

        binding.navPage.btnNext.setOnClickListener {
            if (currentPage == 1) { // 2페이지(기록지)에서 다음으로 넘어갈 때
                // 입력 내용 확인 (선택사항, 원하지 않으면 이 if문 제거 가능)
                val feeling = findViewById<EditText>(R.id.edit_feeling).text.toString()
                val impulsiveAction = findViewById<EditText>(R.id.edit_impulsive_action).text.toString()
                val oppositeAction = findViewById<EditText>(R.id.edit_opposite_action).text.toString()
                if (feeling.isBlank() || impulsiveAction.isBlank() || oppositeAction.isBlank()) {
                    Toast.makeText(this, "모든 항목을 입력해주세요.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener // 입력을 안했으면 넘어가지 않음
                }
            }

            if (currentPage < totalPages - 1) {
                currentPage++
                updatePage()
            } else {
                // 마지막 페이지에서 '완료' 버튼을 눌렀을 때
                // TODO: 기록된 내용을 데이터베이스에 저장하는 로직 추가
                Toast.makeText(this, "훈련이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                finish() // 액티비티 종료
            }
        }

        binding.navPage.btnPrev.setOnClickListener {
            if (currentPage > 0) {
                currentPage--
                updatePage()
            }
        }
    }

    private fun updatePage() {
        val inflater = LayoutInflater.from(this)
        binding.pageContainer.removeAllViews() // 이전 페이지 내용 삭제

        val pageView = when (currentPage) {
            0 -> inflater.inflate(R.layout.page_opposite_action_1_guide, binding.pageContainer, false)
            1 -> inflater.inflate(R.layout.page_opposite_action_2_record, binding.pageContainer, false)
            2 -> inflater.inflate(R.layout.page_opposite_action_3_final, binding.pageContainer, false)
            else -> throw IllegalStateException("Invalid page number")
        }
        binding.pageContainer.addView(pageView) // 새 페이지 내용 추가
        updateNavButtons()
    }

    private fun updateNavButtons() {
        // 이전 버튼 활성화/비활성화
        binding.navPage.btnPrev.isEnabled = currentPage > 0

        // 다음 버튼 텍스트 변경
        if (currentPage == totalPages - 1) {
            binding.navPage.btnNext.text = "완료"
        } else {
            binding.navPage.btnNext.text = "다음 →"
        }
    }
}