package com.example.emotionalapp.ui.expression

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
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

        // --- 1. 인디케이터 점들을 생성합니다 ---
        setupIndicators()
        updatePage()

        binding.btnBack.setOnClickListener { finish() }

        binding.navPage.btnNext.setOnClickListener {
            if (currentPage == 1) {
                // 이 부분은 findViewById를 사용하면, 현재 화면에 없는 뷰를 찾으려다 앱이 종료될 수 있습니다.
                // updatePage 함수 안에서 뷰를 찾는 것이 더 안전합니다.
                // 따라서 여기서는 다음 페이지로 넘어가기만 합니다.
            }

            if (currentPage < totalPages - 1) {
                currentPage++
                updatePage()
            } else {
                // 마지막 페이지에서 '완료' 버튼을 눌렀을 때
                // TODO: 기록된 내용을 데이터베이스에 저장하는 로직 추가
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

    // --- 2. 페이지 수만큼 회색 점을 만드는 함수 ---
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

    private fun updatePage() {
        val inflater = LayoutInflater.from(this)
        binding.pageContainer.removeAllViews()

        val pageView = when (currentPage) {
            0 -> inflater.inflate(R.layout.page_opposite_action_1_guide, binding.pageContainer, false)
            1 -> inflater.inflate(R.layout.page_opposite_action_2_record, binding.pageContainer, false)
            2 -> inflater.inflate(R.layout.page_opposite_action_3_final, binding.pageContainer, false)
            else -> throw IllegalStateException("Invalid page number")
        }
        binding.pageContainer.addView(pageView)
        updateNavButtons()
        updateIndicators() // --- 3. 페이지가 바뀔 때마다 인디케이터 색을 업데이트합니다 ---
    }

    // --- 4. 현재 페이지에 맞는 점만 검은색으로 바꾸는 함수 ---
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

    private fun updateNavButtons() {
        binding.navPage.btnPrev.isEnabled = currentPage > 0
        if (currentPage == totalPages - 1) {
            binding.navPage.btnNext.text = "완료"
        } else {
            binding.navPage.btnNext.text = "다음 →"
        }
    }
}