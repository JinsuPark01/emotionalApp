package com.example.emotionalapp.ui.expression

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class AlternativeReportActivity : AppCompatActivity() {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // XML 레이아웃의 View 참조
    private lateinit var btnBack: ImageView
    private lateinit var answer1: TextView
    private lateinit var answer2: TextView
    private lateinit var answer3: TextView
    private lateinit var answer4: TextView
    private lateinit var answer5: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 제공해주신 XML 레이아웃 파일을 설정합니다.
        setContentView(R.layout.activity_expression_alternative_report)

        // View 초기화
        btnBack = findViewById(R.id.btnBack)
        answer1 = findViewById(R.id.answer1)
        answer2 = findViewById(R.id.answer2)
        answer3 = findViewById(R.id.answer3)
        answer4 = findViewById(R.id.answer4)
        answer5 = findViewById(R.id.answer5)

        btnBack.setOnClickListener { finish() }

        // Intent에서 docId와 collectionName 가져오기
        val docId = intent.getStringExtra("docId")
        val collectionName = intent.getStringExtra("collectionName")

        // docId 또는 collectionName이 없으면 오류 처리 후 액티비티 종료
        if (docId.isNullOrBlank() || collectionName.isNullOrBlank()) {
            Toast.makeText(this, "오류: 기록 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadReportData(collectionName, docId)
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

        answer1.text = document.getString("answer1") ?: "기록된 내용이 없습니다."
        answer2.text = document.getString("answer2") ?: "기록된 내용이 없습니다."
        answer3.text = document.getString("answer3") ?: "기록된 내용이 없습니다."
        answer4.text = document.getString("answer4") ?: "기록된 내용이 없습니다."
        answer5.text = document.getString("answer5") ?: "기록된 내용이 없습니다."
    }
}