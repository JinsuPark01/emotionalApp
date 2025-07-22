package com.example.emotionalapp.ui.expression

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.emotionalapp.R
import com.example.emotionalapp.adapter.AlternativeActionAdapter
import com.example.emotionalapp.data.AlternativeActionItem
import com.example.emotionalapp.databinding.ActivityAlternativeActionBinding

class AlternativeActionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlternativeActionBinding
    private var currentPage = 0
    private val totalPages = 3 // 기록, 선택, 결과 총 3페이지

    // 사용자가 입력한 데이터를 저장할 변수
    private var situation: String = ""
    private var feeling: String = ""
    private var actionTaken: String = ""
    private var selectedAlternative: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlternativeActionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updatePage()

        binding.btnBack.setOnClickListener { finish() }

        binding.navPage.btnNext.setOnClickListener {
            if (!handleNextButtonClick()) return@setOnClickListener

            if (currentPage < totalPages - 1) {
                currentPage++
                updatePage()
            } else {
                // 마지막 페이지에서 '완료' 버튼을 눌렀을 때
                // TODO: 기록된 모든 내용을 데이터베이스에 저장하는 로직 추가
                Toast.makeText(this, "훈련이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        binding.navPage.btnPrev.setOnClickListener {
            if (currentPage > 0) {
                currentPage--
                updatePage()
            }
        }
    }

    private fun handleNextButtonClick(): Boolean {
        when (currentPage) {
            0 -> { // 1페이지 (기록)
                val situationInput = findViewById<EditText>(R.id.edit_situation)?.text.toString().trim()
                val feelingInput = findViewById<EditText>(R.id.edit_feeling)?.text.toString().trim()
                val actionTakenInput = findViewById<EditText>(R.id.edit_action_taken)?.text.toString().trim()

                if (situationInput.isBlank() || feelingInput.isBlank() || actionTakenInput.isBlank()) {
                    Toast.makeText(this, "모든 항목을 입력해주세요.", Toast.LENGTH_SHORT).show()
                    return false
                }
                // 다음 페이지로 넘어가기 전에 데이터 저장
                situation = situationInput
                feeling = feelingInput
                actionTaken = actionTakenInput
            }
            1 -> { // 2페이지 (선택)
                if (selectedAlternative.isBlank()) {
                    Toast.makeText(this, "대안 행동을 선택해주세요.", Toast.LENGTH_SHORT).show()
                    return false
                }
            }
            2 -> { // 3페이지 (결과)
                val resultInput = findViewById<EditText>(R.id.edit_result)?.text.toString().trim()
                if (resultInput.isBlank()) {
                    Toast.makeText(this, "결과를 입력해주세요.", Toast.LENGTH_SHORT).show()
                    return false
                }
            }
        }
        return true
    }

    private fun updatePage() {
        val inflater = LayoutInflater.from(this)
        binding.pageContainer.removeAllViews()

        val pageView = when (currentPage) {
            0 -> inflater.inflate(R.layout.page_alternative_action_1_record, binding.pageContainer, false)
            1 -> {
                val view = inflater.inflate(R.layout.page_alternative_action_2_select, binding.pageContainer, false)
                setupAlternativeSelectionPage(view)
                view
            }
            2 -> inflater.inflate(R.layout.page_alternative_action_3_result, binding.pageContainer, false)
            else -> throw IllegalStateException("Invalid page number")
        }
        binding.pageContainer.addView(pageView)
        updateNavButtons()
    }

    private fun setupAlternativeSelectionPage(view: android.view.View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_alternatives)
        val titleView = view.findViewById<TextView>(R.id.tv_selection_title)

        titleView.text = "'$feeling'을 느낄 때, 이런 행동은 어때요?"

        // TODO: 실제로는 사용자가 입력한 감정에 따라 다른 목록을 DB나 다른 곳에서 가져와야 함
        val alternatives = when {
            feeling.contains("화") || feeling.contains("짜증") -> listOf(
                AlternativeActionItem("잠시 그 자리를 벗어나기"),
                AlternativeActionItem("숫자를 10까지 천천히 세기"),
                AlternativeActionItem("차가운 물로 손 씻기")
            )
            feeling.contains("불안") -> listOf(
                AlternativeActionItem("좋아하는 노래 듣기"),
                AlternativeActionItem("가벼운 스트레칭 하기"),
                AlternativeActionItem("친한 친구와 통화하기")
            )
            else -> listOf(
                AlternativeActionItem("따뜻한 차 마시기"),
                AlternativeActionItem("5분 산책하기"),
                AlternativeActionItem("좋아하는 책 읽기")
            )
        }

        val adapter = AlternativeActionAdapter(alternatives) { item ->
            selectedAlternative = item.actionText
            Toast.makeText(this, "'${item.actionText}' 선택됨", Toast.LENGTH_SHORT).show()
            // TODO: 선택된 아이템 UI에 표시 (예: 배경색 변경)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun updateNavButtons() {
        binding.navPage.btnPrev.isEnabled = currentPage > 0
        if (currentPage == totalPages - 1) {
            binding.navPage.btnNext.text = "완료"
        } else {
            binding.navPage.btnNext.text = "다음 →"
        }
    }
}