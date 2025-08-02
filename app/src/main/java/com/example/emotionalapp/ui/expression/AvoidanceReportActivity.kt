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

class AvoidanceReportActivity : AppCompatActivity() {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // XML 레이아웃의 View 참조
    private lateinit var btnBack: ImageView
    private lateinit var tvAvoid1: TextView
    private lateinit var tvAvoid2: TextView
    private lateinit var tvAnswer1: TextView
    private lateinit var tvAnswer2: TextView
    private lateinit var tvAnswer3: TextView
    private lateinit var tvResult4: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 제공해주신 XML 레이아웃 파일 이름을 설정합니다.
        setContentView(R.layout.activity_expression_avoidance_report)

        // View 초기화
        btnBack = findViewById(R.id.btnBack)
        tvAvoid1 = findViewById(R.id.tv_avoid1)
        tvAvoid2 = findViewById(R.id.tv_avoid2)
        tvAnswer1 = findViewById(R.id.tv_answer1)
        tvAnswer2 = findViewById(R.id.tv_answer2)
        tvAnswer3 = findViewById(R.id.tv_answer3)
        tvResult4 = findViewById(R.id.tv_result4)

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
        db.collection("user").document(userEmail).collection("expressionAvoidance")
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
     * docId를 사용하여 Firestore에서 특정 문서를 직접 가져오는 함수
     */
    private fun loadReportData(collectionName: String, docId: String) {
        val userEmail = auth.currentUser?.email
        if (userEmail == null) {
            Toast.makeText(this, "로그인 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // 문서를 직접 지정하여 가져오기
        db.collection("user").document(userEmail)
            .collection(collectionName).document(docId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    Log.d("Firestore", "Document found: ${document.data}")
                    populateUI(document)
                } else {
                    Log.d("Firestore", "No such document with id: $docId")
                    Toast.makeText(this, "해당 기록을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error getting document: ", exception)
                Toast.makeText(this, "기록을 불러오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Firestore 문서의 데이터로 UI를 채우는 함수
     */
    private fun populateUI(document: DocumentSnapshot) {
        // XML의 ID와 설명을 보고 추정한 필드 이름입니다. 실제와 다를 수 있습니다.

        // .getString()은 필드가 없거나 타입이 다르면 null을 반환합니다.
        // ?: "기록된 내용이 없습니다." 는 null일 경우 기본 텍스트를 설정해줍니다.
        tvAvoid1.text = document.getString("avoid1") ?: "선택한 항목이 없습니다."
        tvAvoid2.text = document.getString("avoid2") ?: "입력한 내용이 없습니다."
        tvAnswer1.text = document.getString("answer1") ?: "기록된 내용이 없습니다."
        tvAnswer2.text = document.getString("answer2") ?: "기록된 내용이 없습니다."
        tvAnswer3.text = document.getString("answer3") ?: "기록된 내용이 없습니다."
        tvResult4.text = document.getString("result4") ?: "기록된 내용이 없습니다."
    }
}