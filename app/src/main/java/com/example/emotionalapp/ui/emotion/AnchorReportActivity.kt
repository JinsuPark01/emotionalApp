package com.example.emotionalapp.ui.emotion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.R
import com.example.emotionalapp.ui.login_signup.LoginActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AnchorReportActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anchor_report)

        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }

        val reportMillis = intent?.getLongExtra("reportDateMillis", -1L) ?: -1L
        if (reportMillis == -1L) {
            Toast.makeText(this, "잘못된 보고서 정보입니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        val reportTimestamp = Timestamp(Date(reportMillis))

        val user = FirebaseAuth.getInstance().currentUser
        val userEmail = user?.email

        if (user == null || userEmail == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        val db = FirebaseFirestore.getInstance()
        db.collection("user").document(userEmail).collection("emotionAnchor")
            .whereEqualTo("date", reportTimestamp).get()
            .addOnSuccessListener { snapshot ->
                val doc = snapshot.documents.firstOrNull() ?: return@addOnSuccessListener

                val timestamp: Timestamp = doc.getTimestamp("date") ?: return@addOnSuccessListener
                val date = timestamp.toDate()
                val dateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)

                val selectedCue = doc.get("selectedCue") as? String ?: ""

                val elements = doc.get("elements") as? Map<*, *>
                val thought = elements?.get("thought") as? String ?: ""
                val sensation = elements?.get("sensation") as? String ?: ""
                val behavior = elements?.get("behavior") as? String ?: ""

                findViewById<TextView>(R.id.anchorReportTitleText).text = dateString
                findViewById<TextView>(R.id.reportTextC).text = selectedCue
                findViewById<TextView>(R.id.reportTextT).text = thought
                findViewById<TextView>(R.id.reportTextS).text = sensation
                findViewById<TextView>(R.id.reportTextB).text = behavior
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "가져오기 실패: ${e.message}")
                Toast.makeText(this, "데이터를 불러오는 데 실패했어요.", Toast.LENGTH_SHORT).show()
            }
    }
}