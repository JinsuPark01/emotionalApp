package com.example.emotionalapp.ui.expression

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.emotionalapp.R
import com.example.emotionalapp.adapter.ReportAdapter
import com.example.emotionalapp.data.ReportItem
import com.example.emotionalapp.ui.alltraining.ExpressionActivity
import com.example.emotionalapp.ui.alltraining.MindActivity
import com.example.emotionalapp.ui.login_signup.LoginActivity
import com.example.emotionalapp.ui.mind.ArtReportActivity
import com.example.emotionalapp.ui.mind.AutoReportActivity
import com.example.emotionalapp.ui.mind.AutoTrapReportActivity
import com.example.emotionalapp.ui.mind.TrapReportActivity
import com.example.emotionalapp.ui.open.BottomNavActivity
import com.example.emotionalapp.ui.weekly.WeeklyReportActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ExpressionReportActivity : BottomNavActivity() {

    override val isAllTrainingPage: Boolean = true // 하단 네비게이션 비활성화 유지

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance() // db 연결

    private lateinit var trainingRecyclerView: RecyclerView
    private lateinit var adapter: ReportAdapter
    private val reportList = mutableListOf<ReportItem>() // 더미 데이터용 리스트

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training_report)

        trainingRecyclerView = findViewById(R.id.trainingRecyclerView)

        setupBottomNavigation()
        setupTabListeners()
        setupRecyclerView()
        loadReportsWithCoroutines() // 데이터 불러오기 및 화면 갱신
    }

    private fun setupRecyclerView() {
        trainingRecyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ReportAdapter(reportList) { reportItem ->
            val intent = when (reportItem.name) {
                "주간 점검 기록 보기" -> Intent(this, WeeklyReportActivity::class.java)
                "회피 일지 기록 보기" -> Intent(this, AvoidanceReportActivity::class.java)
                "반대 행동하기 기록 보기" -> Intent(this, OppositeReportActivity::class.java)
                "정서 머무르기 기록 보기" -> Intent(this, StayReportActivity::class.java)
                "대안 행동 찾기 기록 보기" -> Intent(this, AlternativeReportActivity::class.java)
                else -> null
            }
            reportItem.timeStamp?.let {
                intent?.putExtra("reportDateMillis", it.toDate().time)
            }
            intent?.let { startActivity(it) }
        }

        trainingRecyclerView.adapter = adapter
        reportList.clear()
        adapter.notifyDataSetChanged()
    }

    private fun loadReportsWithCoroutines() {
        val user = FirebaseAuth.getInstance().currentUser
        val userEmail = user?.email

        if (user == null || userEmail == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                reportList.clear()

                // weekly3 컬렉션에서 가장 오래된 2번째 문서만 가져오기
                val nthDoc = getNthOldestDoc(userEmail = userEmail, collectionName = "weekly3", n = 4)
                val avoidanceDocs = db.collection("user").document(userEmail).collection("emotionAvoidance").get().await()
                val stayDocs = db.collection("user").document(userEmail).collection("emotionStay").get().await()
                val oppositeDocs = db.collection("user").document(userEmail).collection("expressionOpposite").get().await()
                val alternativeDocs = db.collection("user").document(userEmail).collection("expressionAlternative").get().await()

                nthDoc?.let {
                    reportList.add(ReportItem(it.id.substringBefore('_'), "주간 점검 기록 보기", it.getTimestamp("date")))
                }
                avoidanceDocs.documents.forEach { doc ->
                    reportList.add(ReportItem(doc.id.substringBefore('_'), "회피 일지 기록 보기", doc.getTimestamp("date")))
                }
                stayDocs.documents.forEach { doc ->
                    reportList.add(ReportItem(doc.id.substringBefore('_'), "반대 행동하기 기록 보기", doc.getTimestamp("date")))
                }
                oppositeDocs.documents.forEach { doc ->
                    reportList.add(ReportItem(doc.id.substringBefore('_'), "정서 머무르기 기록 보기", doc.getTimestamp("date")))
                }
                alternativeDocs.documents.forEach { doc ->
                    reportList.add(ReportItem(doc.id.substringBefore('_'), "대안 행동 찾기 기록 보기", doc.getTimestamp("date")))
                }

                // 최신 날짜가 위로 오게 정렬
                reportList.sortBy { it.date }
                adapter.notifyDataSetChanged()

            } catch (e: Exception) {
                Log.e("Firestore", "데이터 불러오기 실패", e)
            }
        }
    }

    private fun setupTabListeners() {
        val tabAll = findViewById<TextView>(R.id.tabAll)
        val tabToday = findViewById<TextView>(R.id.tabToday)

        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }

        // 전체 훈련 탭 클릭 시 이동
        tabAll.setOnClickListener {
            val intent = Intent(this, ExpressionActivity::class.java)
            startActivity(intent)
            finish()
        }

        // 금일 훈련 탭은 현재 페이지이므로 클릭 시 아무 동작 없음
        tabToday.setOnClickListener {
            Log.d("TodayTrainingPage", "금일 훈련 탭 클릭됨 (현재 페이지)")
        }
    }

    private suspend fun getNthOldestDoc(userEmail: String, collectionName: String, n: Int): DocumentSnapshot? {
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