package com.example.emotionalapp.ui.body

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.emotionalapp.R
import com.example.emotionalapp.adapter.ReportAdapter
import com.example.emotionalapp.data.ReportItem
import com.example.emotionalapp.ui.alltraining.BodyActivity
import com.example.emotionalapp.ui.weekly.WeeklyReportActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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
            val intent = if (reportItem.name.contains("주간 점검 기록 보기")) {
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

        setupTabListeners()
        loadReports()
    }

    private fun setupTabListeners() {
        val tabAll = findViewById<TextView>(R.id.tabAll)
        val tabToday = findViewById<TextView>(R.id.tabToday)

        tabAll.setOnClickListener {
            val intent = Intent(this, BodyActivity::class.java)
            startActivity(intent)
            finish()
        }

        // "기록 보기" 탭은 현재 화면 → 아무 동작 없음
        tabToday.setOnClickListener {
            Log.d("Tab", "기록 보기 탭 클릭됨 (현재 화면)")
        }
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
                            trainingId = trainingId,
                            backgroundColorResId = R.color.pink
                        )
                    )
                }

                // ✅ 2. weekly3 로드 (2주차 주간 점검용)
                val nthDoc = getNthOldestDoc(userEmail = userEmail, collectionName = "weekly3", n = 2, db)
                nthDoc?.let {
                    reportList.add(ReportItem(it.id.substringBefore('_'), "주간 점검 기록 보기", it.getTimestamp("date")))
                }

                // 최신순 정렬
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

    private suspend fun getNthOldestDoc(
        userEmail: String,
        collectionName: String,
        n: Int,
        db: FirebaseFirestore
    ): DocumentSnapshot? {
        if (n < 1) return null // 1부터 시작하는 인덱스

        var lastDoc: DocumentSnapshot? = null

        // (n - 1)번 반복해서 앞 문서를 순차적으로 탐색
        for (i in 1 until n) {
            val query = db.collection("user")
                .document(userEmail)
                .collection(collectionName)
                .orderBy("date", Query.Direction.ASCENDING)
                .let { if (lastDoc != null) it.startAfter(lastDoc) else it }
                .limit(1)
                .get()
                .await()

            lastDoc = query.documents.firstOrNull() ?: return null // 앞 문서가 없다면 n번째는 없음
        }

        // n번째 문서
        val nthQuery = db.collection("user")
            .document(userEmail)
            .collection(collectionName)
            .orderBy("date", Query.Direction.ASCENDING)
            .let { if (lastDoc != null) it.startAfter(lastDoc) else it }
            .limit(1)
            .get()
            .await()

        return nthQuery.documents.firstOrNull()
    }
}
