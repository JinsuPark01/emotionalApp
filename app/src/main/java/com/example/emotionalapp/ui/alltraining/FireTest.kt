package com.example.emotionalapp.ui.alltraining

import android.graphics.Typeface
import android.os.Bundle
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class FireTest : AppCompatActivity() {

    private lateinit var btnSave: Button
    private lateinit var btnLoad: Button
    private lateinit var txtResult: TextView

    private val db = FirebaseFirestore.getInstance()
    private val testUserId = "testUser123"  // 하드코딩된 사용자 ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 레이아웃 구성
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 48, 48, 48)
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        }

        btnSave = Button(this).apply {
            text = "저장하기"
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        }

        btnLoad = Button(this).apply {
            text = "불러오기"
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        }

        txtResult = TextView(this).apply {
            text = "결과 출력 영역"
            textSize = 16f
            setTypeface(null, Typeface.BOLD)
            setPadding(0, 40, 0, 0)
        }

        layout.addView(btnSave)
        layout.addView(btnLoad)
        layout.addView(txtResult)

        setContentView(layout)

        val userDoc = db.collection("user").document(testUserId)

        btnSave.setOnClickListener {
            val data = mapOf(
                "name" to "진수",
                "age" to 22,
                "joinedAt" to System.currentTimeMillis()
            )

            userDoc.set(data)
                .addOnSuccessListener {
                    txtResult.text = "✅ 저장 성공!"
                }
                .addOnFailureListener {
                    txtResult.text = "❌ 저장 실패: ${it.message}"
                }
        }

        btnLoad.setOnClickListener {
            userDoc.get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        val name = doc.getString("name") ?: "N/A"
                        val age = doc.getLong("age") ?: -1
                        txtResult.text = "이름: $name\n나이: $age"
                    } else {
                        txtResult.text = "🚫 문서 없음"
                    }
                }
                .addOnFailureListener {
                    txtResult.text = "❌ 불러오기 실패: ${it.message}"
                }
        }
    }
}
