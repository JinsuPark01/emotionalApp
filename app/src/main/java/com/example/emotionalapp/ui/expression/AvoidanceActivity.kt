package com.example.emotionalapp.ui.expression

import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.R
import com.example.emotionalapp.ui.alltraining.AllTrainingPageActivity
import com.example.emotionalapp.ui.login_signup.LoginActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class AvoidanceActivity : AppCompatActivity() {

    private lateinit var btnPrev: TextView
    private lateinit var btnNext: TextView
    private lateinit var indicatorContainer: LinearLayout
    private lateinit var pageContainer: FrameLayout
    private lateinit var titleText: TextView

    private val totalPages = 2
    private var currentPage = 0

    private var avoid1: String = ""
    private var avoid2: String = ""
    private var situation: String = ""
    private var emotion: String = ""
    private var method: String = ""
    private var result: String = ""
    private var effect: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training_frame)

        btnPrev = findViewById(R.id.btnPrev)
        btnNext = findViewById(R.id.btnNext)
        indicatorContainer = findViewById(R.id.indicatorContainer)
        pageContainer = findViewById(R.id.pageContainer)
        titleText = findViewById(R.id.titleText)

        findViewById<View>(R.id.btnBack).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("훈련 종료")
                .setMessage("훈련을 종료하고 나가시겠어요?")
                .setPositiveButton("예") { _, _ -> finish() }
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
            val pageView = pageContainer.getChildAt(0)

            if (currentPage == 0) {
                val checkBoxes = listOf(
                    R.id.cb_avoid1, R.id.cb_avoid2, R.id.cb_avoid3, R.id.cb_avoid4,
                    R.id.cb_avoid5, R.id.cb_avoid6, R.id.cb_avoid7, R.id.cb_avoid8
                )

                val checkedText = checkBoxes.mapNotNull { id ->
                    val cb = pageView.findViewById<CheckBox>(id)
                    if (cb.isChecked) cb.text.toString() else null
                }

                val customText = pageView.findViewById<EditText>(R.id.et_custom_avoidance)?.text?.toString()?.trim() ?: ""
                val effectText = pageView.findViewById<EditText>(R.id.et_effect)?.text?.toString()?.trim() ?: ""


                if (checkedText.isEmpty() && customText.isEmpty() && effectText.isEmpty()) {
                    Toast.makeText(this, "모든 질문에 답해주세요", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                avoid1 = checkedText.firstOrNull() ?: ""
                avoid2 = customText
                effect = effectText

            } else if (currentPage == 1) {
                situation = pageView.findViewById<EditText>(R.id.et_situation)?.text?.toString()?.trim() ?: ""
                emotion = pageView.findViewById<EditText>(R.id.et_emotion)?.text?.toString()?.trim() ?: ""
                method = pageView.findViewById<EditText>(R.id.et_method)?.text?.toString()?.trim() ?: ""
                result = pageView.findViewById<EditText>(R.id.et_result)?.text?.toString()?.trim() ?: ""

                if (situation.isEmpty() || emotion.isEmpty() || method.isEmpty() || result.isEmpty()) {
                    Toast.makeText(this, "모든 질문에 답변해주세요.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            if (currentPage < totalPages - 1) {
                currentPage++
                updatePage()
            } else {
                btnNext.isEnabled = false

                val user = FirebaseAuth.getInstance().currentUser
                val userEmail = user?.email

                if (user == null || userEmail == null) {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                    return@setOnClickListener
                }

                val now = Timestamp.now()
                val dateString = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS", Locale.getDefault())
                    .apply { timeZone = TimeZone.getTimeZone("Asia/Seoul") }
                    .format(now.toDate())

                val data = hashMapOf(
                    "date" to now,
                    "avoid1" to avoid1,
                    "avoid2" to avoid2,
                    "answer1" to situation,
                    "answer2" to emotion,
                    "answer3" to method,
                    "result4" to result,
                    "effect" to effect
                )

                val db = FirebaseFirestore.getInstance()
                db.collection("user")
                    .document(userEmail)
                    .collection("expressionAvoidance")
                    .document(dateString)
                    .set(data)
                    .addOnSuccessListener {
                        Toast.makeText(this@AvoidanceActivity, "기록 완료.", Toast.LENGTH_SHORT).show()
                        db.collection("user")
                            .document(userEmail)
                            .update("countComplete.avoidance", FieldValue.increment(1))
                            .addOnSuccessListener {
                                Log.d("Firestore", "카운트 증가 성공")
                                val intent = Intent(this, AllTrainingPageActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Log.w("Firestore", "카운트 증가 실패", e)
                                btnNext.isEnabled = true
                            }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "저장 실패. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                        btnNext.isEnabled = true
                    }
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
        pageContainer.removeAllViews()

        titleText.text = when (currentPage) {
            0 -> "나의 회피 행동 체크하기"
            1 -> "회피 일지 작성"
            else -> "나의 회피 행동 체크하기"
        }

        val layoutId = when (currentPage) {
            0 -> R.layout.fragment_expression_avoidance_training_0
            1 -> R.layout.fragment_expression_avoidance_training_1
            else -> R.layout.fragment_expression_avoidance_training_0
        }

        val pageView = inflater.inflate(layoutId, pageContainer, false)
        pageContainer.addView(pageView)

        // ✅ [복원 처리]
        if (currentPage == 0) {
            val checkBoxes = listOf(
                R.id.cb_avoid1 to "회피 행동 1",
                R.id.cb_avoid2 to "회피 행동 2",
                R.id.cb_avoid3 to "회피 행동 3",
                R.id.cb_avoid4 to "회피 행동 4",
                R.id.cb_avoid5 to "회피 행동 5",
                R.id.cb_avoid6 to "회피 행동 6",
                R.id.cb_avoid7 to "회피 행동 7",
                R.id.cb_avoid8 to "회피 행동 8"
            )

            for ((id, text) in checkBoxes) {
                val cb = pageView.findViewById<CheckBox>(id)
                if (avoid1 == cb.text.toString()) {
                    cb.isChecked = true
                }
            }

            val etCustom = pageView.findViewById<EditText>(R.id.et_custom_avoidance)
            etCustom.setText(avoid2)

            val etEffect = pageView.findViewById<EditText>(R.id.et_effect)
            etEffect.setText(effect)

        } else if (currentPage == 1) {
            pageView.findViewById<EditText>(R.id.et_situation)?.setText(situation)
            pageView.findViewById<EditText>(R.id.et_emotion)?.setText(emotion)
            pageView.findViewById<EditText>(R.id.et_method)?.setText(method)
            pageView.findViewById<EditText>(R.id.et_result)?.setText(result)
        }

        // ✅ 버튼 상태 및 인디케이터 업데이트
        btnPrev.isEnabled = currentPage != 0
        btnPrev.backgroundTintList = if (currentPage == 0)
            ColorStateList.valueOf(Color.parseColor("#D9D9D9"))
        else
            ColorStateList.valueOf(Color.parseColor("#3CB371"))

        btnNext.text = if (currentPage == totalPages - 1) "완료 →" else "다음 →"

        for (i in 0 until indicatorContainer.childCount) {
            val dot = indicatorContainer.getChildAt(i)
            dot.setBackgroundResource(
                if (i == currentPage) R.drawable.ic_dot_circle_black else R.drawable.ic_dot_circle_gray
            )
        }
    }

}
