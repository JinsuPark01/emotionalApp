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
import com.example.emotionalapp.ui.login_signup.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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
            val intent = Intent(this, BodyTrainingRecordViewActivity::class.java)
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

                val snapshot = db.collection("users")
                    .document(userEmail)
                    .collection("bodyRecord")
                    .get()
                    .await()

                snapshot.documents.forEach { doc ->
                    val content = doc.getString("content") ?: "소감 없음"

                    val timestamp: Timestamp? = try {
                        doc.getTimestamp("date")
                    } catch (e: Exception) {
                        Log.e("TimestampParse", "date 필드 파싱 오류: ${e.message}")
                        null
                    }

                    val formattedDate = timestamp?.toDate()?.toString() ?: "날짜 없음"

                    reportList.add(
                        ReportItem(
                            name = content.take(10) + "...",
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
