package com.example.emotionalapp.ui.mind

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.R
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class ArtReportActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var firstImage: ImageView
    private lateinit var secondImage: ImageView
    private lateinit var answerViews: List<TextView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mind_art_report)

        btnBack = findViewById(R.id.btnBack)
        firstImage = findViewById(R.id.firstImage)
        secondImage = findViewById(R.id.secondImage)

        btnBack.setOnClickListener { finish() }

        answerViews = listOf(
            findViewById(R.id.answer1),
            findViewById(R.id.answer2),
            findViewById(R.id.answer3),
            findViewById(R.id.answer4),
            findViewById(R.id.answer5),
            findViewById(R.id.answer6),
            findViewById(R.id.answer7),
            findViewById(R.id.answer8),
            findViewById(R.id.answer9),
            findViewById(R.id.answer10)
        )

        loadDataFromFirestore()
    }

    private fun loadDataFromFirestore() {
        val email = FirebaseAuth.getInstance().currentUser?.email ?: return
        val db = FirebaseFirestore.getInstance()

        val reportMillis = intent?.getLongExtra("reportDateMillis", -1L) ?: -1L
        if (reportMillis == -1L) {
            Toast.makeText(this, "보고서 날짜가 유효하지 않습니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val targetTimestamp = Timestamp(Date(reportMillis))

        db.collection("user").document(email)
            .collection("mindArt")
            .whereEqualTo("date", targetTimestamp)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.first()
                    bindDataToUI(document)
                } else {
                    Toast.makeText(this, "해당 날짜의 보고서를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "데이터 로드 실패: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun bindDataToUI(document: com.google.firebase.firestore.DocumentSnapshot) {
        val firstImageName = document.getString("firstImage")
        val secondImageName = document.getString("secondImage")

        firstImage.setImageResource(getResIdByImageName(firstImageName))
        secondImage.setImageResource(getResIdByImageName(secondImageName))

        // 1번 이미지 관련 답변
        for (i in 0 until 5) {
            answerViews[i].setText(document.getString("1art_${i + 1}") ?: "")
        }

        // 2번 이미지 관련 답변
        for (i in 0 until 5) {
            answerViews[i + 5].setText(document.getString("2art_${i + 1}") ?: "")
        }
    }

    private fun getResIdByImageName(name: String?): Int {
        if (name.isNullOrEmpty()) return 0
        return resources.getIdentifier(name, "drawable", packageName)
    }
}
