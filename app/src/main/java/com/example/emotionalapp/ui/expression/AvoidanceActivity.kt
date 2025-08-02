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

    private var selectedAvoidances = mutableListOf<String>()
    private var customAvoidance: String = ""
    private var situation: String = ""
    private var emotion: String = ""
    private var method: String = ""
    private var result: String = ""
    private var habitBehavior: String = ""
    private var habitImpact: String = ""
    private var isSaving = false

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
            saveCurrentPageData()
            if (currentPage > 0) {
                currentPage--
                updatePage()
            }
        }

        btnNext.setOnClickListener {
            if (isSaving) return@setOnClickListener
            if (!validateCurrentPage()) return@setOnClickListener
            saveCurrentPageData()

            if (currentPage < totalPages - 1) {
                currentPage++
                updatePage()
            } else {
                saveToFirestore()
            }
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

        loadPageContent(pageView)
        updateNavButtonsAndIndicators()
    }

    private fun loadPageContent(view: View) {
        if (currentPage == 0) {
            getCheckBoxes(view).forEach { cb ->
                if (selectedAvoidances.contains(cb.text.toString())) {
                    cb.isChecked = true
                }
            }
            view.findViewById<EditText>(R.id.et_custom_avoidance).setText(customAvoidance)
        } else if (currentPage == 1) {
            view.findViewById<EditText>(R.id.et_situation).setText(situation)
            view.findViewById<EditText>(R.id.et_emotion).setText(emotion)
            view.findViewById<EditText>(R.id.et_method).setText(method)
            view.findViewById<EditText>(R.id.et_result).setText(result)
            view.findViewById<EditText>(R.id.et_habit_behavior).setText(habitBehavior)
            view.findViewById<EditText>(R.id.et_habit_impact).setText(habitImpact)
        }
    }

    private fun saveCurrentPageData() {
        val pageView = pageContainer.getChildAt(0) ?: return
        if (currentPage == 0) {
            selectedAvoidances = getCheckBoxes(pageView).filter { it.isChecked }.map { it.text.toString() }.toMutableList()
            customAvoidance = pageView.findViewById<EditText>(R.id.et_custom_avoidance).text.toString().trim()
        } else if (currentPage == 1) {
            situation = pageView.findViewById<EditText>(R.id.et_situation).text.toString().trim()
            emotion = pageView.findViewById<EditText>(R.id.et_emotion).text.toString().trim()
            method = pageView.findViewById<EditText>(R.id.et_method).text.toString().trim()
            result = pageView.findViewById<EditText>(R.id.et_result).text.toString().trim()
            habitBehavior = pageView.findViewById<EditText>(R.id.et_habit_behavior).text.toString().trim()
            habitImpact = pageView.findViewById<EditText>(R.id.et_habit_impact).text.toString().trim()
        }
    }

    private fun validateCurrentPage(): Boolean {
        val pageView = pageContainer.getChildAt(0) ?: return false
        return if (currentPage == 0) {
            val isChecked = getCheckBoxes(pageView).any { it.isChecked }
            val customText = pageView.findViewById<EditText>(R.id.et_custom_avoidance).text.toString().trim()
            if (!isChecked && customText.isEmpty()) {
                Toast.makeText(this, "하나 이상의 회피 행동을 선택하거나 입력해주세요.", Toast.LENGTH_SHORT).show()
                false
            } else true
        } else {
            val fields = listOf(R.id.et_situation, R.id.et_emotion, R.id.et_method, R.id.et_result, R.id.et_habit_behavior, R.id.et_habit_impact)
            val allFilled = fields.all { pageView.findViewById<EditText>(it).text.isNotBlank() }
            if (!allFilled) {
                Toast.makeText(this, "모든 질문에 답변해주세요.", Toast.LENGTH_SHORT).show()
            }
            allFilled
        }
    }

    private fun saveToFirestore() {
        isSaving = true
        btnNext.isEnabled = false

        val user = FirebaseAuth.getInstance().currentUser
        val userEmail = user?.email
        if (user == null || userEmail == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        val now = Timestamp.now()
        val dateString = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault()).format(now.toDate())

        val data = hashMapOf(
            "date" to now,
            "selected_avoidances" to selectedAvoidances,
            "custom_avoidance" to customAvoidance,
            "situation" to situation,
            "emotion" to emotion,
            "method" to method,
            "result" to result,
            "habit_behavior" to habitBehavior,
            "habit_impact" to habitImpact
        )

        val db = FirebaseFirestore.getInstance()
        db.collection("user").document(userEmail).collection("expressionAvoidance").document(dateString).set(data)
            .addOnSuccessListener {
                db.collection("user").document(userEmail).update("countComplete.avoidance", FieldValue.increment(1))
                    .addOnCompleteListener { showCompletionDialog() }
            }
            .addOnFailureListener {
                Toast.makeText(this, "저장 실패. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                isSaving = false
                btnNext.isEnabled = true
            }
    }

    private fun showCompletionDialog() {
        AlertDialog.Builder(this)
            .setTitle("기록 완료!")
            .setMessage("감정을 회피하는 습관을 돌아봤다는 것 자체가 이미 중요한 변화의 시작이에요. 스스로를 마주한 용기를 진심으로 응원해요!")
            .setPositiveButton("확인") { _, _ ->
                val intent = Intent(this, AllTrainingPageActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun getCheckBoxes(view: View): List<CheckBox> {
        return listOf(
            view.findViewById(R.id.cb_avoid1), view.findViewById(R.id.cb_avoid2),
            view.findViewById(R.id.cb_avoid3), view.findViewById(R.id.cb_avoid4),
            view.findViewById(R.id.cb_avoid5), view.findViewById(R.id.cb_avoid6),
            view.findViewById(R.id.cb_avoid7), view.findViewById(R.id.cb_avoid8)
        )
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

    private fun updateNavButtonsAndIndicators() {
        btnPrev.isEnabled = currentPage != 0
        btnPrev.backgroundTintList = if (currentPage == 0) ColorStateList.valueOf(Color.parseColor("#D9D9D9")) else ColorStateList.valueOf(Color.parseColor("#3CB371"))
        btnNext.text = if (currentPage == totalPages - 1) "완료" else "다음 →"

        for (i in 0 until indicatorContainer.childCount) {
            val dot = indicatorContainer.getChildAt(i)
            dot.setBackgroundResource(if (i == currentPage) R.drawable.ic_dot_circle_black else R.drawable.ic_dot_circle_gray)
        }
    }
}