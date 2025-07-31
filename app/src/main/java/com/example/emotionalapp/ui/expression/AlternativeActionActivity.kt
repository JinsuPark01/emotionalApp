package com.example.emotionalapp.ui.expression

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.emotionalapp.R
import com.example.emotionalapp.adapter.AlternativeActionAdapter
import com.example.emotionalapp.adapter.DetailedEmotionAdapter
import com.example.emotionalapp.data.AlternativeActionItem
import com.example.emotionalapp.databinding.ActivityAlternativeActionBinding
import com.example.emotionalapp.ui.alltraining.AllTrainingPageActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

data class EmotionDetails(
    val detailedEmotionsResId: Int,
    val alternativeActionsResId: Int
)

class AlternativeActionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlternativeActionBinding
    private var currentPage = 0
    private val totalPages = 3

    private var situation: String = ""
    private var selectedEmotion: String = ""
    private var selectedDetailedEmotion: String = ""
    private var selectedAlternative: String = ""
    private var customAlternative: String = ""
    private var finalActionTaken: String = ""

    private var selectedDetailedEmotionPosition: Int = -1
    private var selectedAlternativePosition: Int = -1

    private var selectedEmotionButton: Button? = null
    private var isSaving = false

    private val emotionDetailsMap = mapOf(
        "화남" to EmotionDetails(R.array.detailed_emotions_anger, R.array.alternative_actions_anger),
        "슬픔" to EmotionDetails(R.array.detailed_emotions_sadness, R.array.alternative_actions_sadness),
        "불안" to EmotionDetails(R.array.detailed_emotions_anxiety, R.array.alternative_actions_anxiety),
        "외로움" to EmotionDetails(R.array.detailed_emotions_loneliness, R.array.alternative_actions_loneliness),
        "죄책감" to EmotionDetails(R.array.detailed_emotions_guilt, R.array.alternative_actions_guilt),
        "무기력" to EmotionDetails(R.array.detailed_emotions_lethargy, R.array.alternative_actions_lethargy),
        "창피함" to EmotionDetails(R.array.detailed_emotions_shame, R.array.alternative_actions_shame)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlternativeActionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupIndicators()
        updatePage()

        binding.btnBack.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("훈련 종료")
                .setMessage("훈련을 종료하고 나가시겠어요?")
                .setPositiveButton("예") { _, _ -> finish() }
                .setNegativeButton("아니오", null)
                .show()
        }

        binding.navPage.btnNext.setOnClickListener {
            if (isSaving) return@setOnClickListener
            if (!validateAndSaveCurrentPage()) return@setOnClickListener

            val nextPage = if (currentPage == 0 && selectedEmotion == "직접 입력") 2 else currentPage + 1

            if (nextPage < totalPages) {
                currentPage = nextPage
                updatePage()
            } else {
                lifecycleScope.launch {
                    isSaving = true
                    binding.navPage.btnNext.isEnabled = false
                    try {
                        saveToFirestore()
                        Toast.makeText(this@AlternativeActionActivity, "훈련 기록이 저장되었습니다.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@AlternativeActionActivity, AllTrainingPageActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        startActivity(intent)
                        finish()
                    } catch (e: Exception) {
                        Toast.makeText(this@AlternativeActionActivity, "저장에 실패했습니다: ${e.message}", Toast.LENGTH_LONG).show()
                        isSaving = false
                        binding.navPage.btnNext.isEnabled = true
                    }
                }
            }
        }

        binding.navPage.btnPrev.setOnClickListener {
            saveCurrentPageData()
            if (currentPage > 0) {
                if (currentPage == 2 && selectedEmotion == "직접 입력") {
                    currentPage = 0
                } else {
                    currentPage--
                }
                updatePage()
            }
        }
    }

    private fun updatePage() {
        val inflater = LayoutInflater.from(this)
        binding.pageContainer.removeAllViews()
        val pageView = when (currentPage) {
            0 -> inflater.inflate(R.layout.page_alternative_action_1_emotion, binding.pageContainer, false)
            1 -> inflater.inflate(R.layout.page_alternative_action_2_suggestion, binding.pageContainer, false)
            2 -> inflater.inflate(R.layout.page_alternative_action_3_action, binding.pageContainer, false)
            else -> throw IllegalStateException("Invalid page")
        }
        binding.pageContainer.addView(pageView)
        loadPageContent(pageView)
        updateNavButtons()
        updateIndicators()
    }

    // In AlternativeActionActivity.kt

    private fun loadPageContent(view: View) {
        when (currentPage) {
            0 -> {
                view.findViewById<EditText>(R.id.edit_situation).setText(situation)
                setupEmotionButtons(view)
            }
            1 -> {
                if (selectedEmotion != "직접 입력") {
                    setupSuggestionPage(view)
                }
                // --- 여기가 핵심 수정 부분입니다 ---
                // 2페이지가 로드될 때, 저장된 '나만의 대안 행동' 텍스트를 다시 채워줍니다.
                view.findViewById<EditText>(R.id.edit_custom_action).setText(customAlternative)
            }
            2 -> {
                view.findViewById<EditText>(R.id.edit_action_taken).setText(finalActionTaken)
            }
        }
    }

    private fun setupEmotionButtons(view: View) {
        val gridEmotions = view.findViewById<android.widget.GridLayout>(R.id.grid_emotions)
        gridEmotions.removeAllViews()
        val emotions = listOf("화남", "슬픔", "불안", "외로움", "죄책감", "무기력", "창피함", "직접 입력")
        emotions.forEach { emotion ->
            val button = createEmotionButton(emotion)
            gridEmotions.addView(button)
            if (emotion == selectedEmotion) {
                handleEmotionSelection(button, emotion, view)
            }
            button.setOnClickListener { handleEmotionSelection(it as Button, emotion, view) }
        }
    }

    // In AlternativeActionActivity.kt

    private fun setupSuggestionPage(view: View) {
        emotionDetailsMap[selectedEmotion]?.let { details ->
            // --- 여기가 핵심 수정 부분입니다 ---
            // 더 이상 존재하지 않는 'recycler_detailed_emotions' 관련 코드를 모두 삭제했습니다.

            // 대안 행동 목록 설정
            val alternativeActions = resources.getStringArray(details.alternativeActionsResId).toList().map { AlternativeActionItem(it) }
            val recyclerAlternative = view.findViewById<RecyclerView>(R.id.recycler_alternative_actions)
            recyclerAlternative.layoutManager = LinearLayoutManager(this)
            recyclerAlternative.adapter = AlternativeActionAdapter(alternativeActions, selectedAlternativePosition) { position, item ->
                selectedAlternative = item.actionText
                selectedAlternativePosition = position
                // 대안 행동 선택 시, '나만의 대안 행동' 입력창은 비워줍니다.
                view.findViewById<EditText>(R.id.edit_custom_action).text.clear()
            }
        }
    }

    // In AlternativeActionActivity.kt

    private fun saveCurrentPageData() {
        val pageView = binding.pageContainer.getChildAt(0) ?: return
        when (currentPage) {
            0 -> situation = pageView.findViewById<EditText>(R.id.edit_situation).text.toString().trim()
            1 -> {
                // --- 여기가 핵심 수정 부분입니다 (1) ---
                // '나만의 대안 행동'은 항상 저장합니다.
                // '대안 행동 예시' 선택 여부와는 독립적입니다.
                customAlternative = pageView.findViewById<EditText>(R.id.edit_custom_action).text.toString().trim()
            }
            2 -> finalActionTaken = pageView.findViewById<EditText>(R.id.edit_action_taken).text.toString().trim()
        }
    }

    // In AlternativeActionActivity.kt

    private fun validateAndSaveCurrentPage(): Boolean {
        saveCurrentPageData()
        return when (currentPage) {
            0 -> {
                if (situation.isBlank()) { Toast.makeText(this, "상황을 입력해주세요.", Toast.LENGTH_SHORT).show(); return false }
                if (selectedEmotion.isBlank()) { Toast.makeText(this, "감정을 선택해주세요.", Toast.LENGTH_SHORT).show(); return false }
                if (selectedEmotion != "직접 입력" && selectedDetailedEmotion.isBlank()) { Toast.makeText(this, "세부 감정을 선택해주세요.", Toast.LENGTH_SHORT).show(); return false }
                true
            }
            1 -> {
                // --- 여기가 핵심 수정 부분입니다 (2) ---
                // 이제 둘 중 하나만 입력되어 있어도 유효성 검사를 통과합니다.
                if (selectedAlternative.isBlank() && customAlternative.isBlank()) {
                    Toast.makeText(this, "대안 행동을 선택하거나 직접 입력해주세요.", Toast.LENGTH_SHORT).show(); return false
                }
                true
            }
            2 -> {
                if (finalActionTaken.isBlank()){ Toast.makeText(this, "어떻게 행동했는지 입력해주세요.", Toast.LENGTH_SHORT).show(); return false }
                true
            }
            else -> true
        }
    }

    // In AlternativeActionActivity.kt

    private suspend fun saveToFirestore() {
        val user = FirebaseAuth.getInstance().currentUser ?: throw Exception("User not logged in")
        val db = FirebaseFirestore.getInstance()
        val timestamp = Timestamp.now()
        val sdf = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault())
        val docId = sdf.format(timestamp.toDate())

        // --- 여기가 핵심 수정 부분입니다 (3) ---
        // Firestore에 두 필드를 모두 저장하도록 변경
        val data = hashMapOf(
            "answer1" to situation,
            "answer2" to selectedEmotion,
            "answer3" to if (selectedEmotion == "직접 입력") "" else selectedDetailedEmotion,
            "answer4" to selectedAlternative, // 선택한 예시
            "answer5" to customAlternative,   // 직접 입력한 내용
            "answer6" to finalActionTaken,
            "date" to timestamp
        )

        db.collection("user").document(user.email ?: "unknown_user")
            .collection("expressionAlternative")
            .document(docId)
            .set(data)
            .await()
    }

    // --- 여기가 핵심 수정 부분입니다 (1) ---
    // 누락된 함수들을 모두 추가합니다.
    private fun handleEmotionSelection(clickedButton: Button, emotion: String, pageView: View) {
        if (selectedEmotion != emotion) {
            selectedDetailedEmotion = ""
            selectedDetailedEmotionPosition = -1
            selectedAlternative = ""
            selectedAlternativePosition = -1
        }
        selectedEmotion = emotion

        highlightSelectedEmotion(clickedButton)
        val tvDirectInputHint = pageView.findViewById<TextView>(R.id.tv_direct_input_hint)
        val detailedContainer = pageView.findViewById<LinearLayout>(R.id.detailed_emotion_container)
        if (emotion == "직접 입력") {
            detailedContainer.visibility = View.GONE
            tvDirectInputHint.visibility = View.VISIBLE  // 직접 입력일 때 텍스트 표시
        } else {
            detailedContainer.visibility = View.VISIBLE
            tvDirectInputHint.visibility = View.GONE     // 감정 선택 시 텍스트 숨김
            emotionDetailsMap[emotion]?.let { details ->
                val detailedEmotions = resources.getStringArray(details.detailedEmotionsResId).toList()
                val recyclerDetailed = pageView.findViewById<RecyclerView>(R.id.recycler_detailed_emotions)
                recyclerDetailed.layoutManager = LinearLayoutManager(this)
                recyclerDetailed.adapter = DetailedEmotionAdapter(detailedEmotions, selectedDetailedEmotionPosition) { position, item ->
                    selectedDetailedEmotion = item
                    selectedDetailedEmotionPosition = position
                }
            }
        }
    }

    private fun highlightSelectedEmotion(button: Button) {
        selectedEmotionButton?.apply {
            background = ContextCompat.getDrawable(this@AlternativeActionActivity, R.drawable.bg_topic_button)
            setTextColor(ContextCompat.getColor(this@AlternativeActionActivity, android.R.color.black))
        }
        button.background = ContextCompat.getDrawable(this, R.drawable.bg_round_green_button)?.apply {
            setTint(ContextCompat.getColor(this@AlternativeActionActivity, R.color.purple_500))
        }
        button.setTextColor(ContextCompat.getColor(this, android.R.color.white))
        selectedEmotionButton = button
    }

    private fun createEmotionButton(text: String): Button {
        return Button(this).apply {
            this.text = text
            layoutParams = android.widget.GridLayout.LayoutParams().apply {
                width = 0
                height = android.widget.GridLayout.LayoutParams.WRAP_CONTENT
                columnSpec = android.widget.GridLayout.spec(android.widget.GridLayout.UNDEFINED, 1f)
                setMargins(8, 8, 8, 8)
            }
            background = ContextCompat.getDrawable(this@AlternativeActionActivity, R.drawable.bg_topic_button)
            gravity = Gravity.CENTER
            setPadding(16, 24, 16, 24)
            isAllCaps = false
        }
    }

    private fun setupIndicators() {
        val indicatorContainer = binding.navPage.indicatorContainer
        indicatorContainer.removeAllViews()
        for (i in 0 until totalPages) {
            val dot = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(20, 20).apply { setMargins(8, 0, 8, 0) }
                setBackgroundResource(R.drawable.ic_dot_circle_gray)
            }
            indicatorContainer.addView(dot)
        }
    }

    private fun updateIndicators() {
        val indicatorContainer = binding.navPage.indicatorContainer
        for (i in 0 until indicatorContainer.childCount) {
            val dot = indicatorContainer.getChildAt(i)
            dot.setBackgroundResource(if (i == currentPage) R.drawable.ic_dot_circle_black else R.drawable.ic_dot_circle_gray)
        }
    }

    private fun updateNavButtons() {
        binding.navPage.btnPrev.isEnabled = currentPage > 0
        binding.navPage.btnNext.text = if (currentPage == totalPages - 1) "완료 →" else "다음 →"
    }
}