package com.example.emotionalapp.ui.expression

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.R
import com.example.emotionalapp.ui.login_signup.LoginActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class AvoidanceReportActivity : AppCompatActivity() {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // --- XML 레이아웃의 View 참조 추가 ---
    private lateinit var tvHabitBehavior: TextView
    private lateinit var tvHabitImpact: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expression_avoidance_report)

        // --- View 초기화 ---
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val tvAvoid1 = findViewById<TextView>(R.id.tv_avoid1)
        val tvAvoid2 = findViewById<TextView>(R.id.tv_avoid2)
        val tvAnswer1 = findViewById<TextView>(R.id.tv_answer1)
        val tvAnswer2 = findViewById<TextView>(R.id.tv_answer2)
        val tvAnswer3 = findViewById<TextView>(R.id.tv_answer3)
        val tvResult4 = findViewById<TextView>(R.id.tv_result4)
        tvHabitBehavior = findViewById(R.id.tv_habit_behavior) // 새로 추가된 View
        tvHabitImpact = findViewById(R.id.tv_habit_impact)   // 새로 추가된 View

        btnBack.setOnClickListener { finish() }

        // --- docId를 직접 사용하여 문서를 불러오도록 로직 변경 ---
        val reportDocId = intent.getStringExtra("reportDocId")
        if (reportDocId == null) {
            Toast.makeText(this, "잘못된 보고서 정보입니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadReportData(reportDocId)
    }

    private fun loadReportData(docId: String) {
        val userEmail = auth.currentUser?.email
        if (userEmail == null) {
            Toast.makeText(this, "로그인 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        db.collection("user").document(userEmail)
            .collection("expressionAvoidance").document(docId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    populateUI(document)
                } else {
                    Toast.makeText(this, "해당 기록을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "기록을 불러오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun populateUI(document: DocumentSnapshot) {
        // --- View 참조를 지역 변수로 변경 ---
        val tvAvoid1 = findViewById<TextView>(R.id.tv_avoid1)
        val tvAvoid2 = findViewById<TextView>(R.id.tv_avoid2)
        val tvAnswer1 = findViewById<TextView>(R.id.tv_answer1)
        val tvAnswer2 = findViewById<TextView>(R.id.tv_answer2)
        val tvAnswer3 = findViewById<TextView>(R.id.tv_answer3)
        val tvResult4 = findViewById<TextView>(R.id.tv_result4)

        tvAvoid1.text = document.getString("avoid1") ?: "선택한 항목이 없습니다."
        tvAvoid2.text = document.getString("avoid2") ?: "입력한 내용이 없습니다."
        tvAnswer1.text = document.getString("situation") ?: "기록된 내용이 없습니다."
        tvAnswer2.text = document.getString("emotion") ?: "기록된 내용이 없습니다."
        tvAnswer3.text = document.getString("method") ?: "기록된 내용이 없습니다."
        tvResult4.text = document.getString("result") ?: "기록된 내용이 없습니다."

        // --- 여기가 핵심 수정 부분입니다 ---
        // 새로 추가된 필드(habit_behavior, habit_impact)의 데이터를 UI에 표시
        tvHabitBehavior.text = document.getString("habit_behavior") ?: "기록된 내용이 없습니다."
        tvHabitImpact.text = document.getString("habit_impact") ?: "기록된 내용이 없습니다."
    }
}