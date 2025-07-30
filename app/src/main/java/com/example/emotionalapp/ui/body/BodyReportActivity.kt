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
import com.example.emotionalapp.ui.body.BodyTrainingRecordViewActivity
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
                Intent(this, BodyTrainingRecordViewActivity::class.java)
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

                val snapshot = db.collection("users")
                    .document(userEmail)
                    .collection("bodyRecord")
                    .get()
                    .await()

                for (doc in snapshot.documents) {
                    val content = doc.getString("content") ?: "소감 없음"

                    // ✅ 다양한 형식의 date 처리
                    val timestamp = when (val rawDate = doc.get("date")) {
                        is Timestamp -> rawDate
                        is Long -> Timestamp(Date(rawDate))
                        is String -> {
                            try {
                                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                Timestamp(sdf.parse(rawDate)!!)
                            } catch (e: Exception) {
                                null
                            }
                        }
                        else -> null
                    }

                    val formattedDate = timestamp?.let {
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it.toDate())
                    } ?: "날짜 없음"

                    val title = "훈련 소감"

                    reportList.add(
                        ReportItem(
                            name = title,
                            date = formattedDate,
                            timeStamp = timestamp
                        )
                    )
                }

                reportList.sortByDescending { it.timeStamp }
                adapter.notifyDataSetChanged()

            } catch (e: Exception) {
                Log.e("Firestore", "기록 불러오기 실패: ${e.message}", e)
            }
        }
    }
}
