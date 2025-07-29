package com.example.emotionalapp.ui.emotion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.emotionalapp.R
import com.example.emotionalapp.adapter.ReportAdapter
import com.example.emotionalapp.data.ReportItem
import com.example.emotionalapp.ui.alltraining.AllTrainingPageActivity
import com.example.emotionalapp.ui.alltraining.EmotionActivity
import com.example.emotionalapp.ui.login_signup.LoginActivity
import com.example.emotionalapp.ui.open.BottomNavActivity
import com.example.emotionalapp.ui.weekly.WeeklyReportActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class EmotionReportActivity : BottomNavActivity() {

    override val isAllTrainingPage: Boolean = true // 하단 네비게이션 비활성화 유지

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
                "닻 내리기 기록 보기" -> Intent(this, AnchorReportActivity::class.java)
                "ARC 정서 경험 기록 보기" -> Intent(this, ArcReportActivity::class.java)
                "주간 점검 기록 보기" -> Intent(this, WeeklyReportActivity::class.java)
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

        val db = FirebaseFirestore.getInstance()

        CoroutineScope(Dispatchers.Main).launch {
            try {
                reportList.clear()

                // weekly3 컬렉션에서 가장 오래된 1개 문서만 가져오기
                val weekly3Docs = db.collection("user").document(userEmail).collection("weekly3").orderBy("date", Query.Direction.ASCENDING).limit(1).get().await()
                val emotionArcDocs = db.collection("user").document(userEmail).collection("emotionArc").get().await()
                val emotionAnchorDocs = db.collection("user").document(userEmail).collection("emotionAnchor").get().await()

                if (!weekly3Docs.isEmpty) {
                    val oldestDoc = weekly3Docs.documents[0]
                    reportList.add(ReportItem(oldestDoc.id.substringBefore('_'), "주간 점검 기록 보기", oldestDoc.getTimestamp("date")))
                }
                emotionArcDocs.documents.forEach { doc ->
                    reportList.add(ReportItem(doc.id.substringBefore('_'), "ARC 정서 경험 기록 보기", doc.getTimestamp("date")))
                }
                emotionAnchorDocs.documents.forEach { doc ->
                    reportList.add(ReportItem(doc.id.substringBefore('_'), "닻 내리기 기록 보기", doc.getTimestamp("date")))
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

        // 전체 훈련 탭 클릭 시 이동
        tabAll.setOnClickListener {
            val intent = Intent(this, EmotionActivity::class.java)
            startActivity(intent)
            finish()
        }

        // 금일 훈련 탭은 현재 페이지이므로 클릭 시 아무 동작 없음
        tabToday.setOnClickListener {
            Log.d("TodayTrainingPage", "금일 훈련 탭 클릭됨 (현재 페이지)")
        }

        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }
    }
}
