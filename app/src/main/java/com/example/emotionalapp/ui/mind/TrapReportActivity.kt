package com.example.emotionalapp.ui.mind

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
import java.util.TimeZone

class TrapReportActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trap_report)

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
        db.collection("user").document(userEmail).collection("mindTrap")
            .whereEqualTo("date", reportTimestamp).get()
            .addOnSuccessListener { snapshot ->
                val doc = snapshot.documents.firstOrNull() ?: return@addOnSuccessListener

                val timestamp: Timestamp = doc.getTimestamp("date") ?: return@addOnSuccessListener
                val date = timestamp.toDate()
                val dateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
                    timeZone = TimeZone.getTimeZone("Asia/Seoul")
                }.format(date)

                val situation = doc.get("situation") as? String ?: ""
                val thought = doc.get("thought") as? String ?: ""
                val trap = doc.get("trap") as? String ?: ""
                val alternative = doc.get("alternative") as? String ?: ""

                findViewById<TextView>(R.id.trapReportTitleText).text = dateString
                findViewById<TextView>(R.id.reportSituation).text = situation
                findViewById<TextView>(R.id.reportThought).text = thought
                findViewById<TextView>(R.id.reportTrap).text = trap
                findViewById<TextView>(R.id.reportAlternative).text = alternative

                val validityContainer = findViewById<View>(R.id.validityContainer)
                val validityMap = doc.get("validity") as? Map<*, *>
                if (!isSectionEmpty(validityMap)) {
                    validityContainer.visibility = View.VISIBLE

                    findViewById<TextView>(R.id.validityAnswer1).text =
                        validityMap?.get("answer1") as? String ?: ""
                    findViewById<TextView>(R.id.validityAnswer2).text =
                        validityMap?.get("answer2") as? String ?: ""
                    findViewById<TextView>(R.id.validityAnswer3).text =
                        validityMap?.get("answer3") as? String ?: ""
                    findViewById<TextView>(R.id.validityAnswer4).text =
                        validityMap?.get("answer4") as? String ?: ""
                } else {
                    validityContainer.visibility = View.GONE
                }

                val assumptionContainer = findViewById<View>(R.id.assumptionContainer)
                val assumptions = doc.get("assumption") as? Map<*, *>
                if (!isSectionEmpty(assumptions)) {
                    assumptionContainer.visibility = View.VISIBLE

                    findViewById<TextView>(R.id.assumptionAnswer1).text =
                        assumptions?.get("answer1") as? String ?: ""
                    findViewById<TextView>(R.id.assumptionAnswer2).text =
                        assumptions?.get("answer2") as? String ?: ""
                    findViewById<TextView>(R.id.assumptionAnswer3).text =
                        assumptions?.get("answer3") as? String ?: ""
                    findViewById<TextView>(R.id.assumptionAnswer4).text =
                        assumptions?.get("answer4") as? String ?: ""
                } else {
                    assumptionContainer.visibility = View.GONE
                }

                val perspectiveContainer = findViewById<View>(R.id.perspectiveContainer)
                val perspectives = doc.get("perspective") as? Map<*, *>
                if (!isSectionEmpty(perspectives)) {
                    perspectiveContainer.visibility = View.VISIBLE

                    findViewById<TextView>(R.id.perspectiveAnswer1).text =
                        perspectives?.get("answer1") as? String ?: ""
                    findViewById<TextView>(R.id.perspectiveAnswer2).text =
                        perspectives?.get("answer2") as? String ?: ""
                    findViewById<TextView>(R.id.perspectiveAnswer3).text =
                        perspectives?.get("answer3") as? String ?: ""
                } else {
                    perspectiveContainer.visibility = View.GONE
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "가져오기 실패: ${e.message}")
                Toast.makeText(this, "데이터를 불러오는 데 실패했어요.", Toast.LENGTH_SHORT).show()
            }
    }
    private fun isSectionEmpty(section: Map<*, *>?): Boolean {
        if (section == null) return true
        return section.values.all { (it as? String)?.isBlank() != false }
    }
}