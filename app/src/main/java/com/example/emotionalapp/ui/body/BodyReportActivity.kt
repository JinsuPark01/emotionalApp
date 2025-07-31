package com.example.emotionalapp.ui.body

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.emotionalapp.R
import com.example.emotionalapp.adapter.ReportAdapter
import com.example.emotionalapp.data.ReportItem
import com.example.emotionalapp.ui.weekly.WeeklyReportActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class BodyReportActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReportAdapter
    private val reportList = ArrayList<ReportItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_body_report)

        recyclerView = findViewById(R.id.recyclerViewBodyRecords)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ReportAdapter(reportList) { reportItem ->
            val intent = if (reportItem.name.contains("주간 점검")) {
                Intent(this, WeeklyReportActivity::class.java)
            } else {
                Intent(this, BodyTrainingReportDetailActivity::class.java).apply {
                    putExtra("trainingId", reportItem.trainingId)
                }
            }

            reportItem.timeStamp?.let {
                intent.putExtra("reportDateMillis", it.toDate().time)
            }

            startActivity(intent)
        }

        recyclerView.adapter = adapter

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        loadReports()
    }

    private fun loadReports() {
        val user = FirebaseAuth.getInstance().currentUser
        val userEmail = user?.email ?: return

        val db = FirebaseFirestore.getInstance()

        CoroutineScope(Dispatchers.Main).launch {
            try {
                reportList.clear()

                // ✅ 1. bodyRecord 로드
                val snapshot = db.collection("user")
                    .document(userEmail)
                    .collection("bodyRecord")
                    .get()
                    .await()

                for (doc in snapshot.documents) {
                    val content = doc.getString("content") ?: "소감 없음"
                    val timestamp = extractTimestamp(doc.get("date"))
                    val formattedDate = formatDate(timestamp)
                    val trainingId = doc.getString("trainingId") ?: ""

                    val title = when (trainingId) {
                        "bt_detail_002" -> "전체 몸 스캔 인식하기"
                        "bt_detail_003" -> "먹기 명상(음식의 오감 알아차리기)"
                        "bt_detail_004" -> "감정-신체 연결 인식"
                        "bt_detail_005" -> "특정 감각 집중하기"
                        "bt_detail_006" -> "바디 스캔(감각 알아차리기)"
                        "bt_detail_007" -> "바디 스캔(미세한 감각 변화 알아차리기)"
                        "bt_detail_008" -> "먹기 명상(감정과 신체 연결 알아차리기)"
                        else -> "훈련 소감"
                    }

                    reportList.add(
                        ReportItem(
                            name = title,
                            date = formattedDate,
                            timeStamp = timestamp,
                            trainingId = trainingId
                        )
                    )
                }

                // ✅ 2. weekly3 로드 (2주차 주간 점검용)
                val weeklySnapshot = db.collection("user")
                    .document(userEmail)
                    .collection("weekly3")
                    .get()
                    .await()

                for (doc in weeklySnapshot.documents) {
                    val timestamp = doc.getTimestamp("date")
                    val formattedDate = formatDate(timestamp)

                    reportList.add(
                        ReportItem(
                            name = "주간 점검",
                            date = formattedDate,
                            timeStamp = timestamp,
                            trainingId = "weekly_check"
                        )
                    )
                }

                reportList.sortBy { it.timeStamp }
                adapter.notifyDataSetChanged()

            } catch (e: Exception) {
                Log.e("Firestore", "기록 불러오기 실패: ${e.message}", e)
            }
        }
    }

    private fun extractTimestamp(dateField: Any?): Timestamp? {
        return when (dateField) {
            is Timestamp -> dateField
            is Long -> Timestamp(Date(dateField))
            is String -> {
                try {
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    Timestamp(sdf.parse(dateField)!!)
                } catch (e: Exception) {
                    null
                }
            }
            else -> null
        }
    }

    private fun formatDate(ts: Timestamp?): String {
        return ts?.let {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it.toDate())
        } ?: "날짜 없음"
    }
}
