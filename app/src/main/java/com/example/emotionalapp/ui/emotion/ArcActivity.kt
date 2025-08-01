package com.example.emotionalapp.ui.emotion

import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.emotionalapp.R
import com.example.emotionalapp.ui.alltraining.AllTrainingPageActivity
import com.example.emotionalapp.ui.login_signup.LoginActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.coroutines.resume

class ArcActivity : AppCompatActivity() {

    private lateinit var btnPrev: TextView
    private lateinit var btnNext: TextView
    private lateinit var indicatorContainer: LinearLayout
    private lateinit var pageContainer: FrameLayout
    private lateinit var titleText: TextView // 상단 타이틀 TextView

    private val totalPages = 4
    private var currentPage = 0

    private var userAntecedent: String = ""
    private var userResponse: String = ""
    private var userShortConsequence: String = ""
    private var userLongConsequence: String = ""
    private var saveJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training_frame)

        btnPrev = findViewById(R.id.btnPrev)
        btnNext = findViewById(R.id.btnNext)
        indicatorContainer = findViewById(R.id.indicatorContainer)
        pageContainer = findViewById(R.id.pageContainer)
        titleText = findViewById(R.id.titleText)


        val btnBack = findViewById<View>(R.id.btnBack)
        btnBack.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("훈련 종료")
                .setMessage("훈련을 종료하고 나가시겠어요?")
                .setPositiveButton("예") { _, _ ->
                    finish()
                }
                .setNegativeButton("아니오", null)
                .show()
        }

        setupIndicators(totalPages)
        updatePage()

        btnPrev.setOnClickListener {
            if (currentPage > 0) {
                currentPage--
                updatePage()
            }
        }

        btnNext.setOnClickListener {
            if (currentPage == 0) {
                currentPage++
                updatePage()
            } else if (currentPage == 1) {
                // pageContainer 내부 현재 페이지 뷰 찾기
                val pageView = pageContainer.getChildAt(0)
                val answer1 = pageView.findViewById<EditText>(R.id.editSituationArcA)

                // 전역변수에 저장
                userAntecedent = answer1.text.toString().trim()
                Log.d("ArcActivity", "A: $userAntecedent")

                // 입력 체크
                if (userAntecedent.isEmpty()) {
                    Toast.makeText(this, "모든 질문에 답변해주세요.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener // 저장 안 하고 넘어가지 않음
                }else{
                    currentPage++
                    updatePage()
                }
            } else if (currentPage == 2) {
                // pageContainer 내부 현재 페이지 뷰 찾기
                val pageView = pageContainer.getChildAt(0)
                val answer2 = pageView.findViewById<EditText>(R.id.editReactionArcR)

                // 전역변수에 저장
                userResponse = answer2.text.toString().trim()
                Log.d("ArcActivity", "R: $userResponse")

                // 입력 체크
                if (userResponse.isEmpty()) {
                    Toast.makeText(this, "모든 질문에 답변해주세요.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener // 저장 안 하고 넘어가지 않음
                }else{
                    currentPage++
                    updatePage()
                }
            } else if (currentPage == 3) {
                // 중복 저장 방지
                if (saveJob?.isActive == true) return@setOnClickListener
                btnNext.isEnabled = false

                // pageContainer 내부 현재 페이지 뷰 찾기
                val pageView = pageContainer.getChildAt(0)
                val answer3 = pageView.findViewById<EditText>(R.id.editShortTermArcC)
                val answer4 = pageView.findViewById<EditText>(R.id.editLongTermArcC)

                // 전역변수에 저장
                userShortConsequence = answer3.text.toString().trim()
                userLongConsequence = answer4.text.toString().trim()
                Log.d("ArcActivity", "C: $userShortConsequence, $userLongConsequence")

                // 입력 체크
                if (userShortConsequence.isEmpty() || userLongConsequence.isEmpty()) {
                    Toast.makeText(this, "모든 질문에 답변해주세요.", Toast.LENGTH_SHORT).show()
                    btnNext.isEnabled = true  // 다시 활성화
                    return@setOnClickListener // 저장 안 하고 넘어가지 않음
                } else {
                    // Firestore에 저장
                    saveJob = lifecycleScope.launch {
                        val success = withContext(Dispatchers.IO) {
                            saveArcDataToFirestore()
                        }
                        btnNext.isEnabled = true

                        if (success) {
                            Toast.makeText(this@ArcActivity, "ARC 훈련 기록이 저장되었어요.", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@ArcActivity, AllTrainingPageActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this@ArcActivity, "저장 실패. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

    }

    private suspend fun saveArcDataToFirestore(): Boolean = suspendCancellableCoroutine { continuation ->
        val user = FirebaseAuth.getInstance().currentUser
        val userEmail = user?.email

        if (user == null || userEmail == null) {
            continuation.resume(false)
            return@suspendCancellableCoroutine
        }

        val nowTimestamp = Timestamp.now()
        val nowDate = nowTimestamp.toDate()
        val today = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("Asia/Seoul")
        }.format(nowDate)

        val data = hashMapOf(
            "type" to "emotionArc",
            "date" to nowTimestamp,
            "antecedent" to userAntecedent,
            "response" to userResponse,
            "consequences" to hashMapOf(
                "short" to userShortConsequence,
                "long" to userLongConsequence
            )
        )

        val db = FirebaseFirestore.getInstance()
        db.collection("user")
            .document(userEmail)
            .collection("emotionArc")
            .document(today)
            .set(data)
            .addOnSuccessListener {
                db.collection("user")
                    .document(userEmail)
                    .update("countComplete.arc", FieldValue.increment(1))
                    .addOnSuccessListener {
                        continuation.resume(true)
                    }
                    .addOnFailureListener { e ->
                        Log.w("Firestore", "카운트 증가 실패", e)
                        continuation.resume(false)
                    }
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "저장 실패", e)
                continuation.resume(false)
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
            editSituation.setText(userAntecedent)
        } else if (currentPage == 2) {
            val editReaction = pageView.findViewById<EditText>(R.id.editReactionArcR)
            editReaction.setText(userResponse)
        } else if (currentPage == 3) {
            val editShortTerm = pageView.findViewById<EditText>(R.id.editShortTermArcC)
            val editLongTerm = pageView.findViewById<EditText>(R.id.editLongTermArcC)
            editShortTerm.setText(userShortConsequence)
            editLongTerm.setText(userLongConsequence)
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
