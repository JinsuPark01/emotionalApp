package com.example.emotionalapp.ui.weekly

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
import java.util.*

class WeeklyReportActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weekly_report)

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
        db.collection("user").document(userEmail).collection("weekly3")
            .whereEqualTo("date", reportTimestamp).get()
            .addOnSuccessListener { snapshot ->
                val doc = snapshot.documents.firstOrNull() ?: return@addOnSuccessListener

                val timestamp: Timestamp = doc.getTimestamp("date") ?: return@addOnSuccessListener
                val date = timestamp.toDate()
                val dateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
                    timeZone = TimeZone.getTimeZone("Asia/Seoul")
                }.format(date)

                val gad7Map = doc.get("gad7") as? Map<*, *>
                val gad7Sum = (gad7Map?.get("sum") as? Number)?.toInt() ?: 0

                val panasMap = doc.get("panas") as? Map<*, *>
                val positiveSum = (panasMap?.get("positiveSum") as? Number)?.toInt() ?: 0
                val negativeSum = (panasMap?.get("negativeSum") as? Number)?.toInt() ?: 0

                val phq9Map = doc.get("phq9") as? Map<*, *>
                val phq9Sum = (phq9Map?.get("sum") as? Number)?.toInt() ?: 0

                findViewById<TextView>(R.id.weeklyReportTitleText).text = dateString
                if (phq9Sum == -1 || gad7Sum == -1 || positiveSum == -1 || negativeSum == -1){
                    findViewById<TextView>(R.id.phq9Score).text = "점수: 기록되지 않음"
                    findViewById<TextView>(R.id.phq9Interpretation).text = interpretPhq9(phq9Sum)

                    findViewById<TextView>(R.id.gad7Score).text = "점수: 기록되지 않음"
                    findViewById<TextView>(R.id.gad7Interpretation).text = interpretGad7(gad7Sum)

                    findViewById<TextView>(R.id.panasPositiveScore).text =
                        "긍정 점수: 기록되지 않음"
                    findViewById<TextView>(R.id.panasNegativeScore).text =
                        "부정 점수: 기록되지 않음"
                    findViewById<TextView>(R.id.panasInterpretation).text =
                        interpretPanas(positiveSum, negativeSum)
                }else{
                    findViewById<TextView>(R.id.phq9Score).text = "점수: ${phq9Sum}점"
                    findViewById<TextView>(R.id.phq9Interpretation).text = interpretPhq9(phq9Sum)

                    findViewById<TextView>(R.id.gad7Score).text = "점수: ${gad7Sum}점"
                    findViewById<TextView>(R.id.gad7Interpretation).text = interpretGad7(gad7Sum)

                    findViewById<TextView>(R.id.panasPositiveScore).text =
                        "긍정 점수: ${positiveSum} (평균: 29 ~ 34)"
                    findViewById<TextView>(R.id.panasNegativeScore).text =
                        "부정 점수: ${negativeSum} (평균: 26 ~ 30)"
                    findViewById<TextView>(R.id.panasInterpretation).text =
                        interpretPanas(positiveSum, negativeSum)
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "가져오기 실패: ${e.message}")
                Toast.makeText(this, "데이터를 불러오는 데 실패했어요.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun interpretPhq9(score: Int): String = when {
        score < 0 -> "해당 주차에 대한 정보가 없습니다."
        score <= 4 -> "정상입니다. 적응 상 어려움을 초래할만한 우울관련 증상을 거의 보고하지 않았습니다."
        score <= 9 -> "경미한 수준입니다. 약간의 우울감이 있으나 일상생활에 지장을 줄 정도는 아닙니다."
        score <= 14 -> "중간 수준의 우울감입니다. 2주 연속 지속될 경우 일상생활(직업적, 사회적)에 다소 영향을 미칠 수 있어 관심이 필요합니다."
        score <= 19 -> "약간 심한 수준의 우울감입니다. 2주 연속 지속되며 일상생활(직업적, 사회적)에 영향을 미칠 경우, 정신건강전문가의 도움을 받아보세요."
        else -> "심한 수준의 우울감입니다. 2주 연속 지속되며 일상생활(직업적, 사회적)의 다양한 영역에서 어려움을 겪을 경우, 추가적인 평가나 정신건강전문가의 도움을 받아보시기 바랍니다."
    }

    private fun interpretGad7(score: Int): String = when {
        score < 0 -> "해당 주차에 대한 정보가 없습니다."
        score <= 4 -> "정상입니다. 주의가 필요할 정도의 불안을 보고하지 않았습니다."
        score <= 9 -> "다소 경미한 수준의 걱정과 불안을 경험하는 것으로 보입니다."
        score <= 14 -> "주의가 필요한 수준의 과도한 걱정과 불안을 보고하였습니다. 2주 연속 지속될 경우 정신건강전문가의 도움을 받아보세요."
        else -> "과도하고 심한 걱정과 불안을 보고하였습니다. 2주 연속 지속되며 일상생활에서 어려움을 겪을 경우, 추가적인 평가나 정신건강전문가의 도움을 받아보시기 바랍니다."
    }

    private fun interpretPanas(pa: Int, na: Int): String = when {
        pa < 0 && na < 0 -> "해당 주차에 대한 정보가 없습니다."
        pa > na -> "긍정 감정 우세"
        pa < na -> "부정 감정 우세"
        else -> "긍·부정 감정 균형"
    }
}