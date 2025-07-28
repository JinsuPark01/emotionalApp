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
import com.example.emotionalapp.ui.login.LoginActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeeklyActivity : AppCompatActivity() {

    private lateinit var btnPrev: TextView
    private lateinit var btnNext: TextView
    private lateinit var indicatorContainer: LinearLayout
    private lateinit var pageContainer: FrameLayout
    private lateinit var titleText: TextView // 상단 타이틀 TextView

    private val totalPages = 4
    private var currentPage = 0

    private lateinit var phq9ButtonGroups: List<List<LinearLayout>>
    private var phq9Selections = IntArray(9) { -1 } // 9개의 질문, 초기값 -1 (미선택)
    private var phq9Sum = 0
    private lateinit var gad7ButtonGroups: List<List<LinearLayout>>
    private var gad7Selections = IntArray(7) { -1 } // 9개의 질문, 초기값 -1 (미선택)
    private var gad7Sum = 0
    private lateinit var panasButtonGroups: List<List<LinearLayout>>
    private var panasSelections = IntArray(20) { -1 } // 20개의 질문, 초기값 -1 (미선택)
    private var panasNegativeSum = 0
    private var panasPositiveSum = 0


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

            // 페이지 1: GAD-7 설문 유효성 검사
            if (currentPage == 1) {
                val unanswered = gad7Selections.indexOfFirst { it == -1 }
                if (unanswered != -1) {
                    Toast.makeText(this, "${unanswered + 1}번 질문에 답해주세요.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                gad7Sum = gad7Selections.sum()
                Log.d("GAD-7", "GAD-7 Sum: $gad7Sum")
            }

            // 페이지 2: PANAS 설문 유효성 검사
            if (currentPage == 2) {
                val unanswered = panasSelections.indexOfFirst { it == -1 }
                if (unanswered != -1) {
                    Toast.makeText(this, "${unanswered + 1}번 질문에 답해주세요.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val positiveIndices = listOf(0, 3, 4, 7, 8, 11, 13, 16, 17, 18)
                val negativeIndices = listOf(1, 2, 5, 6, 9, 10, 12, 14, 15, 19)
                panasPositiveSum = positiveIndices.sumOf { panasSelections[it] + 1 }
                panasNegativeSum = negativeIndices.sumOf { panasSelections[it] + 1 }
                Log.d("PANAS", "Positive Sum: $panasPositiveSum")
                Log.d("PANAS", "Negative Sum: $panasNegativeSum")

                // Firestore에 저장
                val user = FirebaseAuth.getInstance().currentUser
                val userEmail = user?.email

                if (user == null || userEmail == null) {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                    return@setOnClickListener
                }

                val today =
                    SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS", Locale.getDefault()).format(Date())
                val data = hashMapOf(
                    "type" to "weekly3",
                    "date" to Timestamp.now(),
                    "phq9" to hashMapOf(
                        "answers" to phq9Selections.toList(),
                        "sum" to phq9Sum
                    ),
                    "gad7" to hashMapOf(
                        "answers" to gad7Selections.toList(),
                        "sum" to gad7Sum
                    ),
                    "panas" to hashMapOf(
                        "answers" to panasSelections.toList(),
                        "positiveSum" to panasPositiveSum,
                        "negativeSum" to panasNegativeSum
                    )
                )

                val db = FirebaseFirestore.getInstance()
                db.collection("user")
                    .document(userEmail)
                    .collection("weekly3")
                    .document(today)
                    .set(data)
                    .addOnSuccessListener {
                        Log.d("Firestore", "데이터 저장 성공")
                        moveToNextPage()
                    }
                    .addOnFailureListener { e ->
                        Log.w("Firestore", "저장 실패", e)
                        Toast.makeText(this, "저장 실패. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                        return@addOnFailureListener
                    }
            } else {
                moveToNextPage()
            }
        }
    }

    private fun moveToNextPage() {
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
            1 -> inflater.inflate(R.layout.fragment_gad7_training, pageContainer, false)
            2 -> inflater.inflate(R.layout.fragment_panas_training, pageContainer, false)
            3 -> inflater.inflate(R.layout.fragment_weekly_result, pageContainer, false)
            else -> inflater.inflate(R.layout.fragment_phq9_training, pageContainer, false)
        }

        pageContainer.addView(pageView)

        // 페이지별 동작 처리 - 여기서 작성
        if (currentPage == 0) {
            // 1. 버튼 그룹 수집
            phq9ButtonGroups = List(9) { questionIndex ->
                List(4) { optionIndex ->
                    val resId = resources.getIdentifier(
                        "btn${questionIndex}_${optionIndex}",
                        "id",
                        packageName
                    )
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
        } else if (currentPage == 1) {
            // GAD-7 버튼 그룹 수집
            gad7ButtonGroups = List(7) { questionIndex ->
                List(4) { optionIndex ->
                    val resId = resources.getIdentifier(
                        "btnG${questionIndex}_${optionIndex}",
                        "id",
                        packageName
                    )
                    pageView.findViewById<LinearLayout>(resId)
                }
            }

            // 클릭 리스너 설정
            gad7ButtonGroups.forEachIndexed { questionIndex, buttonGroup ->
                buttonGroup.forEachIndexed { optionIndex, button ->
                    button.setOnClickListener {
                        gad7Selections[questionIndex] = optionIndex
                        updateGAD7ButtonStates(questionIndex)
                    }
                }
            }
        } else if (currentPage == 2) {
            // PANAS 버튼 그룹 수집
            panasButtonGroups = List(20) { questionIndex ->
                List(5) { optionIndex ->
                    val resId = resources.getIdentifier(
                        "btnP${questionIndex}_${optionIndex}",
                        "id",
                        packageName
                    )
                    pageView.findViewById<LinearLayout>(resId)
                }
            }

            // 클릭 리스너 설정
            panasButtonGroups.forEachIndexed { questionIndex, buttonGroup ->
                buttonGroup.forEachIndexed { optionIndex, button ->
                    button.setOnClickListener {
                        panasSelections[questionIndex] = optionIndex
                        updatePanasButtonStates(questionIndex)
                    }
                }
            }
        } else if (currentPage == 3) {
            findViewById<TextView>(R.id.phq9Score).text = "점수: ${phq9Sum}점"
            findViewById<TextView>(R.id.phq9Interpretation).text = interpretPhq9(phq9Sum)

            findViewById<TextView>(R.id.gad7Score).text = "점수: ${gad7Sum}점"
            findViewById<TextView>(R.id.gad7Interpretation).text = interpretGad7(gad7Sum)

            findViewById<TextView>(R.id.panasPositiveScore).text =
                "긍정 점수: ${panasPositiveSum} (평균: 29 ~ 34)"
            findViewById<TextView>(R.id.panasNegativeScore).text =
                "부정 점수: ${panasNegativeSum} (평균: 26 ~ 30)"
            findViewById<TextView>(R.id.panasInterpretation).text =
                interpretPanas(panasPositiveSum, panasNegativeSum)

        }

        // 이전 버튼 상태
        btnPrev.isEnabled = false
        btnPrev.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D9D9D9"))
//        btnPrev.isEnabled = currentPage != 0
//        btnPrev.backgroundTintList = if (currentPage == 0)
//            ColorStateList.valueOf(Color.parseColor("#D9D9D9"))
//        else
//            ColorStateList.valueOf(Color.parseColor("#3CB371"))

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

    private fun updateGAD7ButtonStates(questionIndex: Int) {
        val selected = gad7Selections[questionIndex]
        gad7ButtonGroups[questionIndex].forEachIndexed { index, btn ->
            btn.alpha = if (index == selected) 1.0f else 0.3f
        }
    }

    private fun updatePanasButtonStates(questionIndex: Int) {
        val selected = panasSelections[questionIndex]
        panasButtonGroups[questionIndex].forEachIndexed { index, btn ->
            btn.alpha = if (index == selected) 1.0f else 0.3f
        }
    }

    fun interpretPhq9(score: Int): String = when {
        score <= 4 -> "정상입니다. 적응 상 어려움을 초래할만한 우울관련 증상을 거의 보고하지 않았습니다."
        score <= 9 -> "경미한 수준입니다. 약간의 우울감이 있으나 일상생활에 지장을 줄 정도는 아닙니다."
        score <= 14 -> "중간 수준의 우울감입니다. 2주 연속 지속될 경우 일상생활(직업적, 사회적)에 다소 영향을 미칠 수 있어 관심이 필요합니다."
        score <= 19 -> "약간 심한 수준의 우울감입니다. 2주 연속 지속되며 일상생활(직업적, 사회적)에 영향을 미칠 경우, 정신건강전문가의 도움을 받아보세요."
        else -> "심한 수준의 우울감입니다. 2주 연속 지속되며 일상생활(직업적, 사회적)의 다양한 영역에서 어려움을 겪을 경우, 추가적인 평가나 정신건강전문가의 도움을 받아보시기 바랍니다."
    }

    fun interpretGad7(score: Int): String = when {
        score <= 4 -> "정상입니다. 주의가 필요할 정도의 불안을 보고하지 않았습니다."
        score <= 9 -> "다소 경미한 수준의 걱정과 불안을 경험하는 것으로 보입니다."
        score <= 14 -> "주의가 필요한 수준의 과도한 걱정과 불안을 보고하였습니다. 2주 연속 지속될 경우 정신건강전문가의 도움을 받아보세요."
        else -> "과도하고 심한 걱정과 불안을 보고하였습니다. 2주 연속 지속되며 일상생활에서 어려움을 겪을 경우, 추가적인 평가나 정신건강전문가의 도움을 받아보시기 바랍니다."
    }

    fun interpretPanas(pa: Int, na: Int): String = when {
        pa > na -> "긍정 감정 우세"
        pa < na -> "부정 감정 우세"
        else -> "긍·부정 감정 균형"
    }
}
