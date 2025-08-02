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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class OppositeReportActivity : AppCompatActivity() {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expression_opposite_report)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

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
            .collection("expressionOpposite").document(docId)
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
        findViewById<TextView>(R.id.answer1).text = document.getString("answer1") ?: "기록 없음"
        findViewById<TextView>(R.id.answer2).text = document.getString("answer2") ?: "기록 없음"
        findViewById<TextView>(R.id.answer3).text = document.getString("answer3") ?: "기록 없음"

        val practiced = document.getBoolean("practiced")
        findViewById<TextView>(R.id.tv_practiced).text = if (practiced == true) "실천했어요" else "실천하지 않았어요"

        findViewById<TextView>(R.id.answer5).text = document.getString("answer5") ?: "기록 없음"
    }
}