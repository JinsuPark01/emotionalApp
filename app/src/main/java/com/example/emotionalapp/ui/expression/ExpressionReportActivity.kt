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
import com.example.emotionalapp.ui.login_signup.LoginActivity
import com.example.emotionalapp.ui.open.BottomNavActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ExpressionReportActivity : BottomNavActivity() {

    override val isAllTrainingPage: Boolean = true

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private lateinit var trainingRecyclerView: RecyclerView
    private lateinit var adapter: ReportAdapter
    private val reportList = mutableListOf<ReportItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training_report)

        trainingRecyclerView = findViewById(R.id.trainingRecyclerView)

        setupBottomNavigation()
        setupTabListeners()
        setupRecyclerView()
        loadReportsWithCoroutines()
    }

    private fun setupRecyclerView() {
        trainingRecyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ReportAdapter(reportList) { reportItem ->
            if (reportItem.docId.isNullOrBlank()) {
                if (reportItem.name != "주간 점검 기록 보기") {
                    Toast.makeText(this, "기록 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
                    return@ReportAdapter
                }
            }

            val intent = when (reportItem.name) {
                "회피 일지 기록 보기" -> {
                    Intent(this, AvoidanceReportActivity::class.java).apply {
                        putExtra("docId", reportItem.docId)
                        putExtra("collectionName", "emotionAvoidance")
                    }
                }
                "반대 행동하기 기록 보기" -> {
                    Intent(this, OppositeReportActivity::class.java).apply {
                        putExtra("docId", reportItem.docId)
                        putExtra("collectionName", "expressionOpposite")
                    }
                }
                "정서 머무르기 기록 보기" -> {
                    Intent(this, StayReportActivity::class.java).apply {
                        putExtra("docId", reportItem.docId)
                        putExtra("collectionName", "emotionStay")
                    }
                }
                "대안 행동 찾기 기록 보기" -> {
                    // --- 여기를 수정했습니다 ---
                    Intent(this, AlternativeReportActivity::class.java).apply {
                        putExtra("docId", reportItem.docId)
                        putExtra("collectionName", "expressionAlternative")
                    }
                }
                "주간 점검 기록 보기" -> {
                    // TODO: WeeklyReportActivity 로직 확인
//                    Intent(this, WeeklyReportActivity::class.java).apply {
//                         putExtra("docId", reportItem.docId)
//                         putExtra("collectionName", "weekly4")
//                    }
                    Toast.makeText(this, "주간 점검 기록 보기 화면이 아직 구현되지 않았습니다.", Toast.LENGTH_SHORT).show()
                    null
                }
                else -> null
            }

            intent?.let { startActivity(it) }
        }

        trainingRecyclerView.adapter = adapter
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

                val weeklyDocs = db.collection("user").document(userEmail).collection("weekly4").get().await()
                val avoidanceDocs = db.collection("user").document(userEmail).collection("emotionAvoidance").get().await()
                val stayDocs = db.collection("user").document(userEmail).collection("emotionStay").get().await()
                val oppositeDocs = db.collection("user").document(userEmail).collection("expressionOpposite").get().await()
                val alternativeDocs = db.collection("user").document(userEmail).collection("expressionAlternative").get().await()

                weeklyDocs.documents.forEach { doc ->
                    reportList.add(ReportItem(doc.id.substringBefore('_'), "주간 점검 기록 보기", doc.getTimestamp("date"), doc.id))
                }
                avoidanceDocs.documents.forEach { doc ->
                    reportList.add(ReportItem(doc.id.substringBefore('_'), "회피 일지 기록 보기", doc.getTimestamp("date"), doc.id))
                }
                stayDocs.documents.forEach { doc ->
                    reportList.add(ReportItem(doc.id.substringBefore('_'), "정서 머무르기 기록 보기", doc.getTimestamp("date"), doc.id))
                }
                oppositeDocs.documents.forEach { doc ->
                    reportList.add(ReportItem(doc.id.substringBefore('_'), "반대 행동하기 기록 보기", doc.getTimestamp("date"), doc.id))
                }
                alternativeDocs.documents.forEach { doc ->
                    reportList.add(ReportItem(doc.id.substringBefore('_'), "대안 행동 찾기 기록 보기", doc.getTimestamp("date"), doc.id))
                }

                reportList.sortByDescending { it.timeStamp }
                adapter.notifyDataSetChanged()

            } catch (e: Exception) {
                Log.e("Firestore", "데이터 불러오기 실패", e)
                Toast.makeText(this@ExpressionReportActivity, "기록을 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupTabListeners() {
        val tabAll = findViewById<TextView>(R.id.tabAll)
        val tabToday = findViewById<TextView>(R.id.tabToday)

        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }

        tabAll.setOnClickListener {
            val intent = Intent(this, ExpressionActivity::class.java)
            startActivity(intent)
            finish()
        }

        tabToday.setOnClickListener {
            Log.d("TodayTrainingPage", "금일 훈련 탭 클릭됨 (현재 페이지)")
        }
    }
}