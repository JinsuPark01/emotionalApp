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

    // 저장할 변수들
    private var avoid1: String = ""
    private var avoid2: String = ""
    private var situation: String = ""
    private var emotion: String = ""
    private var method: String = ""
    private var result: String = ""
    private var habitBehavior: String = ""
    private var habitImpact: String = ""

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
            if (!validateCurrentPage()) return@setOnClickListener

            saveCurrentPageData()
            if (currentPage < totalPages - 1) {
                currentPage++
                updatePage()
            } else {
                btnNext.isEnabled = false
                saveToFirestore()
            }
        }
    }

    private fun saveToFirestore() {
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
            "avoid1" to avoid1,
            "avoid2" to avoid2,
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
                    .addOnSuccessListener {
                        showCompletionDialog()
                    }
                    .addOnFailureListener { e ->
                        Log.w("Firestore", "카운트 증가 실패", e)
                        showCompletionDialog() // 저장 자체는 성공했으므로 팝업은 띄워줌
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "저장 실패: ${e.message}", Toast.LENGTH_SHORT).show()
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

    private fun saveCurrentPageData() {
        val pageView = pageContainer.getChildAt(0) ?: return
        if (currentPage == 0) {
            val checkBoxes = listOf(
                R.id.cb_avoid1, R.id.cb_avoid2, R.id.cb_avoid3, R.id.cb_avoid4,
                R.id.cb_avoid5, R.id.cb_avoid6, R.id.cb_avoid7, R.id.cb_avoid8
            )
            val checkedText = checkBoxes.mapNotNull { id ->
                val cb = pageView.findViewById<CheckBox>(id)
                if (cb.isChecked) cb.text.toString() else null
            }
            avoid1 = checkedText.joinToString(", ") // 여러 개 선택 시 쉼표로 구분
            avoid2 = pageView.findViewById<EditText>(R.id.et_custom_avoidance)?.text.toString().trim()
        } else {
            situation = pageView.findViewById<EditText>(R.id.et_situation)?.text.toString().trim()
            emotion = pageView.findViewById<EditText>(R.id.et_emotion)?.text.toString().trim()
            method = pageView.findViewById<EditText>(R.id.et_method)?.text.toString().trim()
            result = pageView.findViewById<EditText>(R.id.et_result)?.text.toString().trim()
            habitBehavior = pageView.findViewById<EditText>(R.id.et_habit_behavior)?.text.toString().trim()
            habitImpact = pageView.findViewById<EditText>(R.id.et_habit_impact)?.text.toString().trim()
        }
    }

    private fun validateCurrentPage(): Boolean {
        val pageView = pageContainer.getChildAt(0) ?: return false
        return if (currentPage == 0) {
            val checkedText = getCheckedTexts(pageView)
            val customText = pageView.findViewById<EditText>(R.id.et_custom_avoidance)?.text.toString().trim()
            if (checkedText.isEmpty() && customText.isEmpty()) {
                Toast.makeText(this, "하나 이상의 회피 행동을 선택하거나 입력해주세요.", Toast.LENGTH_SHORT).show()
                false
            } else true
        } else {
            val situationVal = pageView.findViewById<EditText>(R.id.et_situation)?.text.toString().trim()
            val emotionVal = pageView.findViewById<EditText>(R.id.et_emotion)?.text.toString().trim()
            val methodVal = pageView.findViewById<EditText>(R.id.et_method)?.text.toString().trim()
            val resultVal = pageView.findViewById<EditText>(R.id.et_result)?.text.toString().trim()
            val habitBehaviorVal = pageView.findViewById<EditText>(R.id.et_habit_behavior)?.text.toString().trim()
            val habitImpactVal = pageView.findViewById<EditText>(R.id.et_habit_impact)?.text.toString().trim()

            if (situationVal.isEmpty() || emotionVal.isEmpty() || methodVal.isEmpty() || resultVal.isEmpty() || habitBehaviorVal.isEmpty() || habitImpactVal.isEmpty()) {
                Toast.makeText(this, "모든 질문에 답변해주세요.", Toast.LENGTH_SHORT).show()
                false
            } else true
        }
    }

    private fun getCheckedTexts(view: View): List<String> {
        val checkBoxes = listOf( R.id.cb_avoid1, R.id.cb_avoid2, R.id.cb_avoid3, R.id.cb_avoid4, R.id.cb_avoid5, R.id.cb_avoid6, R.id.cb_avoid7, R.id.cb_avoid8)
        return checkBoxes.mapNotNull { id ->
            val cb = view.findViewById<CheckBox>(id)
            if (cb.isChecked) cb.text.toString() else null
        }
    }

    private fun setupIndicators(count: Int) {
        // ... (이전과 동일) ...
    }

    private fun updatePage() {
        // ... (이전과 동일) ...
    }
}