package com.example.emotionalapp.ui.expression

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.emotionalapp.R
import com.example.emotionalapp.adapter.AlternativeActionAdapter
import com.example.emotionalapp.data.AlternativeActionItem
import com.example.emotionalapp.databinding.ActivityAlternativeActionBinding

class AlternativeActionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlternativeActionBinding
    private var currentPage = 0
    private val totalPages = 3 // 1: 상황 기록, 2: 대안 선택, 3: 결과 확인

    private lateinit var alternativeAdapter: AlternativeActionAdapter
    private val alternativeActionItems = mutableListOf<AlternativeActionItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlternativeActionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updatePage()
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }

        // 다음 버튼
        binding.navPage.btnNext.setOnClickListener {
            when (currentPage) {
                0 -> { // 상황 기록 페이지
                    // 필수 필드가 입력되었는지 확인
                    val situation = findViewById<android.widget.EditText>(R.id.edit_situation).text.toString().trim()
                    val feeling = findViewById<android.widget.EditText>(R.id.edit_feeling).text.toString().trim()
                    val action = findViewById<android.widget.EditText>(R.id.edit_impulsive_action).text.toString().trim()

                    if (situation.isEmpty() || feeling.isEmpty() || action.isEmpty()) {
                        Toast.makeText(this, "모든 항목을 입력해주세요.", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    currentPage++
                    updatePage()
                }
                1 -> { // 대안 선택 페이지
                    // 선택이 되었는지 확인 (이 로직은 어댑터에서 처리해야 함)
                    // TODO: 어댑터에서 선택된 아이템이 있는지 확인하는 로직 추가
                    // 임시로 바로 다음 페이지로 넘어가도록 설정
                    currentPage++
                    updatePage()
                }
                2 -> { // 결과 페이지
                    // TODO: 입력된 결과 내용을 저장하는 로직 추가
                    Toast.makeText(this, "훈련이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

        // 이전 버튼
        binding.navPage.btnPrev.setOnClickListener {
            if (currentPage > 0) {
                currentPage--
                updatePage()
            }
        }
    }

    private fun updatePage() {
        val inflater = LayoutInflater.from(this)
        binding.pageContainer.removeAllViews()

        val pageView = when (currentPage) {
            0 -> inflater.inflate(R.layout.page_alternative_action_1_record, binding.pageContainer, false)
            1 -> {
                val view = inflater.inflate(R.layout.page_alternative_action_2_select, binding.pageContainer, false)
                setupAlternativeList(view) // 대안 목록 설정
                view
            }
            2 -> inflater.inflate(R.layout.page_alternative_action_3_result, binding.pageContainer, false)
            else -> throw IllegalStateException("Invalid page number")
        }
        binding.pageContainer.addView(pageView)
        updateNavButtons()
    }

    private fun setupAlternativeList(view: View) {
        // 예시 대안 목록 (나중에 서버나 DB에서 불러올 수 있습니다.)
        val alternatives = listOf(
            "가벼운 산책하기",
            "친구에게 먼저 연락하기",
            "좋아하는 노래 듣기",
            "명상하기",
            "심호흡 5번 하기"
        ).map { AlternativeActionItem(it) }

        alternativeAdapter = AlternativeActionAdapter(alternatives) { selectedItem ->
            // TODO: 여기서 선택된 대안 행동을 저장하거나 다음 단계에 활용하는 로직 추가
            Toast.makeText(this, "${selectedItem.actionText} 선택됨", Toast.LENGTH_SHORT).show()
        }
        view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recycler_view_alternatives).apply {
            layoutManager = LinearLayoutManager(this@AlternativeActionActivity)
            adapter = alternativeAdapter
        }
    }

    private fun updateNavButtons() {
        binding.navPage.btnPrev.isEnabled = currentPage > 0
        binding.navPage.btnNext.text = if (currentPage == totalPages - 1) "완료" else "다음 →"
    }
}