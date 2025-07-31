package com.example.emotionalapp.ui.body

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.R
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class BodyTrainingReportDetailActivity : AppCompatActivity() {

    private fun getTrainingNameById(id: String): String {
        return when (id) {
            "bt_detail_002" -> "전체 몸 스캔 인식하기"
            "bt_detail_003" -> "먹기 명상 (음식의 오감 알아차리기)"
            "bt_detail_004" -> "감정-신체 연결 인식"
            "bt_detail_005" -> "특정 감각 집중하기"
            "bt_detail_006" -> "바디 스캔 (감각 알아차리기)"
            "bt_detail_007" -> "바디 스캔 (미세한 감각 변화 알아차리기)"
            "bt_detail_008" -> "먹기 명상 (감정과 신체 연결 알아차리기)"
            else -> "알 수 없는 훈련"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_body_report_detail)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        val millis = intent.getLongExtra("reportDateMillis", -1L)
        val trainingIdFromIntent = intent.getStringExtra("trainingId") ?: ""

        if (millis == -1L || trainingIdFromIntent.isBlank()) {
            Toast.makeText(this, "잘못된 접근입니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val reportDate = Date(millis)

        val user = FirebaseAuth.getInstance().currentUser
        val userEmail = user?.email

        if (user == null || userEmail == null) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val calendar = Calendar.getInstance().apply {
            time = reportDate
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfDay = Timestamp(calendar.time)

        calendar.add(Calendar.DATE, 1)
        val startOfNextDay = Timestamp(calendar.time)

        val db = FirebaseFirestore.getInstance()
        db.collection("user")
            .document(userEmail)
            .collection("bodyRecord")
            .whereGreaterThanOrEqualTo("date", startOfDay)
            .whereLessThan("date", startOfNextDay)
            .get()
            .addOnSuccessListener { snapshot ->
                val doc = snapshot.documents.firstOrNull {
                    it.getString("trainingId") == trainingIdFromIntent
                }

                if (doc == null) {
                    Toast.makeText(this, "기록을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                    finish()
                    return@addOnSuccessListener
                }

                val trainingId = doc.getString("trainingId") ?: ""
                val content = doc.getString("content") ?: ""
                val date = doc.getTimestamp("date")?.toDate()

                val formattedDate = date?.let {
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        .apply { timeZone = TimeZone.getTimeZone("Asia/Seoul") }
                        .format(it)
                } ?: ""

                findViewById<TextView>(R.id.tvTrainingTitle).text = getTrainingNameById(trainingId)
                findViewById<TextView>(R.id.tvReportDate).text = formattedDate
                findViewById<TextView>(R.id.tvContent).text = content
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "불러오기 실패: ${e.message}")
                Toast.makeText(this, "기록을 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
    }
}
