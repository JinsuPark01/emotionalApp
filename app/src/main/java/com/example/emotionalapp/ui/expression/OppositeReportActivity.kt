package com.example.emotionalapp.ui.expression

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.R
import com.example.emotionalapp.ui.login_signup.LoginActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class OppositeReportActivity : AppCompatActivity() {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // XML 레이아웃의 View 참조
    private lateinit var btnBack: ImageView
    private lateinit var answer1: TextView
    private lateinit var answer2: TextView
    private lateinit var answer3: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 제공해주신 XML 레이아웃 파일을 설정합니다.
        setContentView(R.layout.activity_expression_opposite_report)

        // View 초기화
        btnBack = findViewById(R.id.btnBack)
        answer1 = findViewById(R.id.answer1)
        answer2 = findViewById(R.id.answer2)
        answer3 = findViewById(R.id.answer3)

        btnBack.setOnClickListener { finish() }

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
        db.collection("user").document(userEmail).collection("expressionOpposite")
            .whereEqualTo("date", reportTimestamp).get()
            .addOnSuccessListener { snapshot ->
                val doc = snapshot.documents.firstOrNull() ?: return@addOnSuccessListener
                populateUI(doc)
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "가져오기 실패: ${e.message}")
                Toast.makeText(this, "데이터를 불러오는 데 실패했어요.", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Firestore 문서의 데이터로 UI를 채우는 함수
     */
    private fun populateUI(document: DocumentSnapshot) {
        // TODO: 아래 "필드이름"은 실제 Firestore 문서에 저장된 필드명(Key)으로 정확하게 변경해야 합니다.
        // XML의 ID를 보고 추정한 필드 이름입니다. 실제와 다를 수 있습니다.

        // .getString()은 필드가 없거나 타입이 다르면 null을 반환합니다.
        // ?: "기본값" 은 null일 경우 기본 텍스트를 설정해줍니다.
        answer1.text = document.getString("answer1") ?: "기록된 감정이 없습니다."
        answer2.text = document.getString("answer2") ?: "기록된 행동이 없습니다."
        answer3.text = document.getString("answer3") ?: "기록된 반대 행동이 없습니다."
    }
}