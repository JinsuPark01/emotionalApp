package com.example.emotionalapp.ui.emotion

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.example.emotionalapp.R
import com.example.emotionalapp.ui.alltraining.AllTrainingPageActivity

class ArcActivity : AppCompatActivity() {

    private lateinit var btnPrev: TextView
    private lateinit var btnNext: TextView
    private lateinit var indicatorContainer: LinearLayout
    private lateinit var pageContainer: FrameLayout
    private lateinit var titleText: TextView // 상단 타이틀 TextView

    private val totalPages = 4
    private var currentPage = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training_frame)

        btnPrev = findViewById(R.id.btnPrev)
        btnNext = findViewById(R.id.btnNext)
        indicatorContainer = findViewById(R.id.indicatorContainer)
        pageContainer = findViewById(R.id.pageContainer)
        titleText = findViewById(R.id.titleText)


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
            0 -> "ARC 정서 경험 기록"
            1 -> "A: 선행 상황 혹은 사건"
            2 -> "R: 내 신체와 감정의 반응"
            3 -> "C: 단기적 결과와 장기적 결과"
            else -> "ARC 정서 경험 기록"
        }

        // 현재 페이지에 맞는 레이아웃 inflate
        val pageView = when (currentPage) {
            0 -> inflater.inflate(R.layout.fragment_emotion_arc_training_0, pageContainer, false)
            1 -> inflater.inflate(R.layout.fragment_emotion_arc_training_1, pageContainer, false)
            2 -> inflater.inflate(R.layout.fragment_emotion_arc_training_2, pageContainer, false)
            3 -> inflater.inflate(R.layout.fragment_emotion_arc_training_3, pageContainer, false)
            else -> inflater.inflate(R.layout.fragment_emotion_arc_training_0, pageContainer, false)
        }

        pageContainer.addView(pageView)

        // 페이지별 동작 처리 - 여기서 작성
        if (currentPage == 1) {
            val editSituation = pageView.findViewById<EditText>(R.id.editSituationArcA)
            val btnSave = pageView.findViewById<Button>(R.id.btnSaveArcA)

            btnSave.setOnClickListener {
                val situationText = editSituation.text.toString().trim()

                if (situationText.isNotEmpty()) {
                    // 👉 입력값 저장 로직 (예: 로컬DB, 서버 전송)
                    Toast.makeText(this, "상황이 저장되었습니다.", Toast.LENGTH_SHORT).show()
                    btnNext.performClick()
                } else {
                    Toast.makeText(this, "상황을 입력해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
        } else if (currentPage == 2) {
            val editReaction = pageView.findViewById<EditText>(R.id.editReactionArcR)
            val btnSave = pageView.findViewById<Button>(R.id.btnSaveArcR)

            btnSave.setOnClickListener {
                val reactionText = editReaction.text.toString().trim()

                if (reactionText.isNotEmpty()) {
                    // 👉 입력값 저장 로직
                    Toast.makeText(this, "반응이 저장되었습니다.", Toast.LENGTH_SHORT).show()
                    btnNext.performClick()
                } else {
                    Toast.makeText(this, "반응을 입력해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
        } else if (currentPage == 3) {
            val editShortTerm = pageView.findViewById<EditText>(R.id.editShortTermArcC)
            val editLongTerm = pageView.findViewById<EditText>(R.id.editLongTermArcC)
            val btnSave = pageView.findViewById<Button>(R.id.btnSaveArcC)

            btnSave.setOnClickListener {
                val shortTermText = editShortTerm.text.toString().trim()
                val longTermText = editLongTerm.text.toString().trim()

                if (shortTermText.isNotEmpty() && longTermText.isNotEmpty()) {
                    // 👉 입력값 저장 로직
                    Toast.makeText(this, "결과가 저장되었습니다.", Toast.LENGTH_SHORT).show()
                    btnNext.performClick()
                } else {
                    Toast.makeText(this, "모든 결과를 입력해주세요.", Toast.LENGTH_SHORT).show()
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

}
