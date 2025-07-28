package com.example.emotionalapp.ui.emotion

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class SelectActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var btnSelect: TextView

    private lateinit var mindButtons: List<LinearLayout>
    private lateinit var bodyButtons: List<LinearLayout>
    private var selectedMind = -1
    private var selectedBody = -1

    private lateinit var accordionWhatIs: LinearLayout
    private lateinit var tvWhatIsDesc: TextView
    private lateinit var iconArrow: ImageView

    private lateinit var accordionHowTo: LinearLayout
    private lateinit var layoutHowToDesc: LinearLayout
    private lateinit var iconArrowHowTo: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emotion_select)

        btnBack = findViewById(R.id.btnBack)
        btnSelect = findViewById(R.id.btnSelect)

        btnBack.setOnClickListener { finish() }

        mindButtons = listOf(
            findViewById(R.id.btnMind1),
            findViewById(R.id.btnMind2),
            findViewById(R.id.btnMind3),
            findViewById(R.id.btnMind4),
            findViewById(R.id.btnMind5),
        )
        bodyButtons = listOf(
            findViewById(R.id.btnBody1),
            findViewById(R.id.btnBody2),
            findViewById(R.id.btnBody3),
            findViewById(R.id.btnBody4),
            findViewById(R.id.btnBody5),
        )

        setupFeelingButtons()

        btnSelect.setOnClickListener {
            if (selectedMind == -1 || selectedBody == -1) {
                Toast.makeText(this, "마음과 몸의 감정을 선택해주세요", Toast.LENGTH_SHORT).show()
            } else {
                saveEmotionData()
            }
        }

        setupAccordionViews()
    }

    private fun setupFeelingButtons() {
        mindButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                selectedMind = index
                updateButtonStates(mindButtons, index)
            }
        }
        bodyButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                selectedBody = index
                updateButtonStates(bodyButtons, index)
            }
        }
    }

    private fun updateButtonStates(buttons: List<LinearLayout>, selectedIndex: Int) {
        buttons.forEachIndexed { index, btn ->
            btn.alpha = if (index == selectedIndex) 1.0f else 0.3f
        }
    }

    private fun setupAccordionViews() {
        accordionWhatIs = findViewById(R.id.accordionWhatIS)
        tvWhatIsDesc = findViewById(R.id.tvWhatIsDesc)
        iconArrow = findViewById(R.id.iconArrow)

        accordionHowTo = findViewById(R.id.accordionHowTo)
        layoutHowToDesc = findViewById(R.id.layoutHowToDesc)
        iconArrowHowTo = findViewById(R.id.iconArrowHowTo)

        accordionWhatIs.setOnClickListener {
            toggleAccordion(tvWhatIsDesc, iconArrow)
        }

        accordionHowTo.setOnClickListener {
            toggleAccordion(layoutHowToDesc, iconArrowHowTo)
        }
    }

    private fun toggleAccordion(description: View, icon: ImageView) {
        val isVisible = description.visibility == View.VISIBLE
        description.visibility = if (isVisible) View.GONE else View.VISIBLE
    }

    private fun saveEmotionData() {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        if (user != null) {
            val email = user.email ?: return
            val db = FirebaseFirestore.getInstance()

            val mindStates = listOf("매우 안 좋음", "안 좋음", "보통", "좋음", "매우 좋음")
            val bodyStates = listOf("매우 이완됨", "이완됨", "보통", "각성", "매우 각성됨")

            val mind = mindStates.getOrNull(selectedMind) ?: "알 수 없음"
            val body = bodyStates.getOrNull(selectedBody) ?: "알 수 없음"

            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val timestamp = dateFormat.format(Date())

            val data = hashMapOf(
                "mind" to mind,
                "body" to body,
                "timestamp" to timestamp
            )

            db.collection("user")
                .document(email)
                .collection("emotionSelect")
                .document(timestamp)
                .set(data)
                .addOnSuccessListener {
                    Toast.makeText(this, "감정이 기록되었습니다.", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "저장 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }
}
