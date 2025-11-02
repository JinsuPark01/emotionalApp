package com.example.emotionalapp.ui.emotion

import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.R
import com.example.emotionalapp.ui.alltraining.AllTrainingPageActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
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

    private lateinit var accordionCaution: LinearLayout
    private lateinit var layoutCautionDesc: LinearLayout
    private lateinit var iconArrowCaution: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emotion_select)

        btnBack = findViewById(R.id.btnBack)
        btnSelect = findViewById(R.id.btnSelect)

        btnBack.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("훈련 종료")
                .setMessage("훈련을 종료하고 나가시겠어요?")
                .setPositiveButton("예") { _, _ ->
                    finish()
                }
                .setNegativeButton("아니오", null)
                .show()
        }

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

        checkTimeAndSetButton()

        setupAccordionViews()
    }

    private fun checkTimeAndSetButton() {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"))
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("Asia/Seoul")
        }.format(calendar.time)

        val timeSlot = when (hour) {
            in 5..13 -> "morning"
            in 14..22 -> "evening"
            else -> null
        }

        if (timeSlot == null) {
            btnSelect.isEnabled = false
            btnSelect.text = "기록은 11~12시, 19~20시에만 가능합니다."
            btnSelect.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D9D9D9"))
            return
        }

        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser ?: return
        val email = user.email ?: return
        val db = FirebaseFirestore.getInstance()

        db.collection("user")
            .document(email)
            .collection("emotionSelect")
            .whereGreaterThanOrEqualTo("date", getTimeSlotStart(timeSlot))
            .whereLessThan("date", getTimeSlotEnd(timeSlot))
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    btnSelect.isEnabled = false
                    btnSelect.text = "해당 시간 상태 기록이 완료되었습니다."
                    btnSelect.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D9D9D9"))
                } else {
                    btnSelect.isEnabled = true
                    btnSelect.text = "상태 기록하기"
                    btnSelect.setOnClickListener {
                        if (selectedMind == -1 || selectedBody == -1) {
                            Toast.makeText(this, "마음과 몸의 감정을 선택해주세요", Toast.LENGTH_SHORT).show()
                        } else {
                            btnSelect.isEnabled = false
                            saveEmotionData()
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "기록 확인 실패: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // 현재 시간 기준 오전 or 오후 타임슬롯 시작
    private fun getTimeSlotStart(slot: String): Timestamp {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul")).apply {
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (slot == "morning") {
                set(Calendar.HOUR_OF_DAY, 5)
            } else {
                set(Calendar.HOUR_OF_DAY, 14)
            }
        }
        return Timestamp(calendar.time)
    }

    private fun getTimeSlotEnd(slot: String): Timestamp {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul")).apply {
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (slot == "morning") {
                set(Calendar.HOUR_OF_DAY, 13)  // 13시 미만까지 포함
            } else {
                set(Calendar.HOUR_OF_DAY, 22)  // 22시 미만까지 포함
            }
        }
        return Timestamp(calendar.time)
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

        accordionCaution = findViewById(R.id.accordionCaution)
        layoutCautionDesc = findViewById(R.id.layoutCautionDesc)
        iconArrowCaution = findViewById(R.id.iconArrowCaution)


        accordionWhatIs.setOnClickListener {
            toggleAccordion(tvWhatIsDesc, iconArrow)
        }

        accordionHowTo.setOnClickListener {
            toggleAccordion(layoutHowToDesc, iconArrowHowTo)
        }

        accordionCaution.setOnClickListener {
            toggleAccordion(layoutCautionDesc, iconArrowCaution)
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

            // Timestamp 값
            val timestamp = Timestamp.now()

            // 문서 ID용 문자열 (정렬 및 구분 위해 그대로 사용 가능)
            val idFormat = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS", Locale.getDefault())
            idFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")
            val timestampStr = idFormat.format(timestamp.toDate())

            // 저장할 데이터
            val data = hashMapOf(
                "mind" to mind,
                "body" to body,
                "date" to timestamp  // 🔥 Firestore Timestamp 타입으로 저장됨
            )

            db.collection("user")
                .document(email)
                .collection("emotionSelect")
                .document(timestampStr) // 문자열 기반 ID (문서명으로 사용)
                .set(data)
                .addOnSuccessListener {
                    // 저장 성공 시에만 countComplete.select +1
                    db.collection("user")
                        .document(email)
                        .update("countComplete.select", FieldValue.increment(1))
                        .addOnSuccessListener {
                            Toast.makeText(this, "상태 기록이 완료되었습니다", Toast.LENGTH_SHORT).show()
                            // 마지막 페이지에서 완료 시 다른 액티비티 이동
                            val intent = Intent(this, AllTrainingPageActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Log.w("Firestore", "카운트 증가 실패", e)
                        }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "저장 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                }

        } else {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

}
