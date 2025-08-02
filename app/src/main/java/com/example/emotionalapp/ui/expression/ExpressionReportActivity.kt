package com.example.emotionalapp.ui.expression

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.emotionalapp.R
import com.example.emotionalapp.adapter.ReportAdapter
import com.example.emotionalapp.data.ReportItem
import com.example.emotionalapp.ui.alltraining.ExpressionActivity
import com.example.emotionalapp.ui.login_signup.LoginActivity
import com.example.emotionalapp.ui.open.BottomNavActivity
import com.example.emotionalapp.ui.weekly.WeeklyReportActivity
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
            val intent = when (reportItem.name) {
                "주간 점검 기록 보기" -> Intent(this, WeeklyReportActivity::class.java)
                "회피 일지 기록 보기" -> Intent(this, AvoidanceReportActivity::class.java)
                "정서 머무르기 기록 보기" -> Intent(this, StayReportActivity::class.java)
                "반대 행동하기 기록 보기" -> Intent(this, OppositeReportActivity::class.java)
                "대안 행동 찾기 기록 보기" -> Intent(this, AlternativeReportActivity::class.java)
                else -> null
            }

            // --- 여기가 핵심 수정 부분입니다 ---
            // docId를 전달하기 위해, ReportItem의 trainingId 필드를 사용합니다.
            intent?.putExtra("reportDocId", reportItem.trainingId)
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
                val avoidanceDocs = db.collection("user").document(userEmail).collection("expressionAvoidance").get().await()
                val stayDocs = db.collection("user").document(userEmail).collection("emotionStay").get().await()
                val oppositeDocs = db.collection("user").document(userEmail).collection("expressionOpposite").get().await()
                val alternativeDocs = db.collection("user").document(userEmail).collection("expressionAlternative").get().await()

                weeklyDocs.documents.forEach { doc ->
                    reportList.add(ReportItem(doc.id.substringBefore('_'), "주간 점검 기록 보기", doc.getTimestamp("date"), doc.id, R.color.button_color_expression))
                }
                avoidanceDocs.documents.forEach { doc ->
                    reportList.add(ReportItem(doc.id.substringBefore('_'), "회피 일지 기록 보기", doc.getTimestamp("date"), doc.id, R.color.button_color_expression))
                }
                stayDocs.documents.forEach { doc ->
                    reportList.add(ReportItem(doc.id.substringBefore('_'), "정서 머무르기 기록 보기", doc.getTimestamp("date"), doc.id, R.color.button_color_expression))
                }
                oppositeDocs.documents.forEach { doc ->
                    reportList.add(ReportItem(doc.id.substringBefore('_'), "반대 행동하기 기록 보기", doc.getTimestamp("date"), doc.id, R.color.button_color_expression))
                }
                alternativeDocs.documents.forEach { doc ->
                    reportList.add(ReportItem(doc.id.substringBefore('_'), "대안 행동 찾기 기록 보기", doc.getTimestamp("date"), doc.id, R.color.button_color_expression))
                }

                reportList.sortByDescending { it.timeStamp }
                adapter.notifyDataSetChanged()

            } catch (e: Exception) {
                Log.e("Firestore", "4주차 데이터 불러오기 실패", e)
            }
        }
    }

    private fun setupTabListeners() {
        val tabAll = findViewById<TextView>(R.id.tabAll)
        val tabToday = findViewById<TextView>(R.id.tabToday)
        val titleText = findViewById<TextView>(R.id.titleText)

        titleText.text = "4주차 기록보기"
        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }

        tabAll.setOnClickListener {
            val intent = Intent(this, ExpressionActivity::class.java)
            startActivity(intent)
            finish()
        }

        tabToday.setOnClickListener {
            Log.d("ExpressionReport", "금일 훈련 탭 클릭됨 (현재 페이지)")
        }
    }
}