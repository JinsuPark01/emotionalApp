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

class ArcReportActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_arc_report)

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
        db.collection("user").document(userEmail).collection("emotionArc")
            .whereEqualTo("date", reportTimestamp).get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    val doc = snapshot.documents[0]
                    val timestamp: Timestamp = doc.getTimestamp("date") ?: return@addOnSuccessListener
                    val date = timestamp.toDate()
                    val dateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)

                    val consequences = doc.get("consequences") as? Map<*, *>
                    val shortConsequence = consequences?.get("short") as? String ?: ""
                    val longConsequence = consequences?.get("long") as? String ?: ""

                    val response = doc.get("response") as? String ?: ""
                    val antecedent = doc.get("antecedent") as? String ?: ""

                    findViewById<TextView>(R.id.arcReportTitleText).text = dateString
                    findViewById<TextView>(R.id.reportTextA).text = antecedent
                    findViewById<TextView>(R.id.reportTextR).text = response
                    findViewById<TextView>(R.id.reportTextSC).text = shortConsequence
                    findViewById<TextView>(R.id.reportTextLC).text = longConsequence
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "가져오기 실패: ${e.message}")
                Toast.makeText(this, "데이터를 불러오는 데 실패했어요.", Toast.LENGTH_SHORT).show()
            }
    }
}