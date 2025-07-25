package com.example.emotionalapp.ui.weekly

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.R
import com.example.emotionalapp.ui.alltraining.AllTrainingPageActivity

class WeeklyActivity : AppCompatActivity() {

    private lateinit var btnPrev: TextView
    private lateinit var btnNext: TextView
    private lateinit var indicatorContainer: LinearLayout
    private lateinit var pageContainer: FrameLayout
    private lateinit var titleText: TextView // 상단 타이틀 TextView

    private lateinit var tabPractice: TextView
    private lateinit var tabRecord: TextView
    private lateinit var underlinePractice: View
    private lateinit var underlineRecord: View

    private val totalPages = 4
    private var currentPage = 0

    private lateinit var phq9ButtonGroups: List<List<LinearLayout>>
    private var phq9Selections = IntArray(9) { -1 } // 9개의 질문, 초기값 -1 (미선택)
    private var phq9Sum = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training_frame)

        btnPrev = findViewById(R.id.btnPrev)
        btnNext = findViewById(R.id.btnNext)
        indicatorContainer = findViewById(R.id.indicatorContainer)
        pageContainer = findViewById(R.id.pageContainer)
        titleText = findViewById(R.id.titleText)

        tabPractice       = findViewById(R.id.tabPractice)
        tabRecord         = findViewById(R.id.tabRecord)
        underlinePractice = findViewById(R.id.underlinePractice)
        underlineRecord   = findViewById(R.id.underlineRecord)


        val btnBack = findViewById<View>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        setupIndicators(totalPages)
        updatePage()

        btnPrev.setOnClickListener {
            if (currentPage > 0) {
                currentPage--
                updatePage()
            }
        }

        btnNext.setOnClickListener {

            // 페이지 0: PHQ-9 설문 유효성 검사
            if (currentPage == 0) {
                val unanswered = phq9Selections.indexOfFirst { it == -1 }
                if (unanswered != -1) {
                    Toast.makeText(this, "${unanswered + 1}번 질문에 답해주세요.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                phq9Sum = phq9Selections.sum()
                Log.d("PHQ-9", "PHQ-9 Sum: $phq9Sum")
            }

            if (currentPage < totalPages - 1) {
                currentPage++
                updatePage()
            } else {
                // 마지막 페이지에서 완료 시 다른 액티비티 이동
                val intent = Intent(this, AllTrainingPageActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        // 탭 리스너 & 초기 탭
        tabPractice.setOnClickListener { selectTab(true) }
        tabRecord  .setOnClickListener { selectTab(false) }
        selectTab(true)
    }

    private fun setupIndicators(count: Int) {
        indicatorContainer.removeAllViews()
        repeat(count) {
            val dot = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(20, 20).apply {
                    marginStart = 8
                    marginEnd = 8
                }
                setBackgroundResource(R.drawable.ic_dot_circle_gray)
            }
            indicatorContainer.addView(dot)
        }
    }

    private fun updatePage() {
        val inflater = LayoutInflater.from(this)
        pageContainer.removeAllViews() // 기존 페이지 제거

        // 현재 페이지에 맞는 제목 설정
        titleText.text = when (currentPage) {
            0 -> "PHQ-9"
            1 -> "GAD-7"
            2 -> "PANAS"
            3 -> "결과"
            else -> "사전 테스트"
        }

        // 현재 페이지에 맞는 레이아웃 inflate
        val pageView = when (currentPage) {
            0 -> inflater.inflate(R.layout.fragment_phq9_training, pageContainer, false)
//            1 -> inflater.inflate(R.layout.fragment_gad7_training, pageContainer, false)
//            2 -> inflater.inflate(R.layout.fragment_panas_training, pageContainer, false)
            else -> inflater.inflate(R.layout.fragment_phq9_training, pageContainer, false)
        }

        pageContainer.addView(pageView)

        // 페이지별 동작 처리 - 여기서 작성
        if (currentPage == 0) {
            // 1. 버튼 그룹 수집
            phq9ButtonGroups = List(9) { questionIndex ->
                List(4) { optionIndex ->
                    val resId = resources.getIdentifier("btn${questionIndex}_${optionIndex}", "id", packageName)
                    pageView.findViewById<LinearLayout>(resId)
                }
            }

            // 2. 클릭 리스너 연결
            phq9ButtonGroups.forEachIndexed { questionIndex, buttonGroup ->
                buttonGroup.forEachIndexed { optionIndex, button ->
                    button.setOnClickListener {
                        phq9Selections[questionIndex] = optionIndex
                        updatePHQ9ButtonStates(questionIndex)
                    }
                }
            }
        }



        // 이전 버튼 상태
        btnPrev.isEnabled = currentPage != 0
        btnPrev.backgroundTintList = if (currentPage == 0)
            ColorStateList.valueOf(Color.parseColor("#D9D9D9"))
        else
            ColorStateList.valueOf(Color.parseColor("#3CB371"))

        // 다음 버튼 텍스트
        btnNext.text = if (currentPage == totalPages - 1) "완료 →" else "다음 →"

        // 인디케이터 업데이트
        for (i in 0 until indicatorContainer.childCount) {
            val dot = indicatorContainer.getChildAt(i)
            dot.setBackgroundResource(
                if (i == currentPage) R.drawable.ic_dot_circle_black else R.drawable.ic_dot_circle_gray
            )
        }
    }

    private fun updatePHQ9ButtonStates(questionIndex: Int) {
        val selected = phq9Selections[questionIndex]
        phq9ButtonGroups[questionIndex].forEachIndexed { index, btn ->
            btn.alpha = if (index == selected) 1.0f else 0.3f
        }
    }


    // 선택된 탭에 따른 동작 여기에 작성해야함
    private fun selectTab(practice: Boolean) {
        tabPractice.setTextColor(
            resources.getColor(if (practice) R.color.black else R.color.gray, null)
        )
        tabRecord.setTextColor(
            resources.getColor(if (practice) R.color.gray else R.color.black, null)
        )
        underlinePractice.visibility = if (practice) View.VISIBLE else View.GONE
        underlineRecord.visibility = if (practice) View.GONE    else View.VISIBLE
    }
}
