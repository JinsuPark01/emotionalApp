package com.example.emotionalapp.ui.expression

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.emotionalapp.R
import com.example.emotionalapp.adapter.AlternativeActionAdapter
import com.example.emotionalapp.adapter.DetailedEmotionAdapter
import com.example.emotionalapp.data.AlternativeActionItem
import com.example.emotionalapp.databinding.ActivityAlternativeActionBinding

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
    private var actionTaken: String = ""

    private var selectedEmotionButton: Button? = null

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

        binding.btnBack.setOnClickListener { finish() }

        binding.navPage.btnNext.setOnClickListener {
            // '다음' 버튼을 누를 때, 현재 페이지의 데이터를 저장하고 유효성을 검사합니다.
            // '직접 입력'을 선택하면, saveCurrentPageDataAndValidate 함수 내부에서 currentPage가 자동으로 변경됩니다.
            if (!saveCurrentPageDataAndValidate()) return@setOnClickListener

            if (currentPage < totalPages - 1) {
                currentPage++
                updatePage()
            } else {
                Toast.makeText(this, "훈련이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        binding.navPage.btnPrev.setOnClickListener {
            if (currentPage > 0) {
                currentPage--
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

        setupPageContent(pageView)
        updateNavButtons()
        updateIndicators()
    }

    private fun setupPageContent(view: View) {
        when (currentPage) {
            0 -> {
                val gridEmotions = view.findViewById<android.widget.GridLayout>(R.id.grid_emotions)
                gridEmotions.removeAllViews()
                val emotions = listOf("화남", "슬픔", "불안", "외로움", "죄책감", "무기력", "창피함", "직접 입력")
                emotions.forEach { emotion ->
                    val button = createEmotionButton(emotion)
                    gridEmotions.addView(button)
                    button.setOnClickListener { handleEmotionSelection(it as Button, emotion, view) }
                }
            }
            1 -> {
                emotionDetailsMap[selectedEmotion]?.let { details ->
                    val alternativeActions = resources.getStringArray(details.alternativeActionsResId).toList().map { AlternativeActionItem(it) }
                    val recyclerAlternative = view.findViewById<RecyclerView>(R.id.recycler_alternative_actions)
                    recyclerAlternative.layoutManager = LinearLayoutManager(this)
                    recyclerAlternative.adapter = AlternativeActionAdapter(alternativeActions) { item ->
                        selectedAlternative = item.actionText
                        Toast.makeText(this, "'${item.actionText}' 선택됨", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun handleEmotionSelection(clickedButton: Button, emotion: String, pageView: View) {
        selectedEmotionButton?.apply {
            background = ContextCompat.getDrawable(this@AlternativeActionActivity, R.drawable.bg_topic_button)
            setTextColor(ContextCompat.getColor(this@AlternativeActionActivity, android.R.color.black))
        }
        clickedButton.background = ContextCompat.getDrawable(this, R.drawable.bg_round_green_button)?.apply {
            setTint(ContextCompat.getColor(this@AlternativeActionActivity, R.color.purple_500))
        }
        clickedButton.setTextColor(ContextCompat.getColor(this, android.R.color.white))
        selectedEmotionButton = clickedButton
        selectedEmotion = emotion

        val detailedContainer = pageView.findViewById<LinearLayout>(R.id.detailed_emotion_container)

        if (emotion == "직접 입력") {
            detailedContainer.visibility = View.GONE
        } else {
            emotionDetailsMap[emotion]?.let { details ->
                val detailedEmotions = resources.getStringArray(details.detailedEmotionsResId).toList()
                val recyclerDetailed = pageView.findViewById<RecyclerView>(R.id.recycler_detailed_emotions)
                recyclerDetailed.layoutManager = LinearLayoutManager(this)
                recyclerDetailed.adapter = DetailedEmotionAdapter(detailedEmotions) { item ->
                    selectedDetailedEmotion = item
                    Toast.makeText(this, "'$item' 선택됨", Toast.LENGTH_SHORT).show()
                }
                detailedContainer.visibility = View.VISIBLE
            }
        }
    }

    // --- 여기가 핵심 수정 부분입니다 ---
    private fun saveCurrentPageDataAndValidate(): Boolean {
        // 현재 페이지의 View를 가져옵니다.
        // getChildAt(0)이 null일 수 있으므로 안전 호출(?.)을 사용합니다.
        val pageView = binding.pageContainer.getChildAt(0) ?: return false

        return when (currentPage) {
            0 -> {
                situation = pageView.findViewById<EditText>(R.id.edit_situation).text.toString().trim()
                if (situation.isBlank()) { Toast.makeText(this, "상황을 입력해주세요.", Toast.LENGTH_SHORT).show(); return false }
                if (selectedEmotion.isBlank()) { Toast.makeText(this, "감정을 선택해주세요.", Toast.LENGTH_SHORT).show(); return false }

                // '직접 입력'이 아니고, 세부 감정도 선택하지 않았다면 막습니다.
                if (selectedEmotion != "직접 입력" && selectedDetailedEmotion.isBlank()) {
                    Toast.makeText(this, "세부 감정을 선택해주세요.", Toast.LENGTH_SHORT).show(); return false
                }

                // '직접 입력'을 선택했다면, 다음 페이지를 2(3번째 페이지)로 설정합니다.
                if (selectedEmotion == "직접 입력") {
                    currentPage = 1 // 다음 페이지 인덱스를 2가 아닌 1로 설정하여 다음 클릭 시 2가 되도록 함
                }
                true
            }
            1 -> {
                customAlternative = pageView.findViewById<EditText>(R.id.edit_custom_action).text.toString().trim()
                if (selectedAlternative.isBlank() && customAlternative.isBlank()) {
                    Toast.makeText(this, "대안 행동을 선택하거나 직접 입력해주세요.", Toast.LENGTH_SHORT).show(); return false
                }
                true
            }
            2 -> {
                actionTaken = pageView.findViewById<EditText>(R.id.edit_action_taken).text.toString().trim()
                if (actionTaken.isBlank()){ Toast.makeText(this, "어떻게 행동했는지 입력해주세요.", Toast.LENGTH_SHORT).show(); return false }
                true
            }
            else -> true
        }
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
        binding.navPage.btnNext.text = if (currentPage == totalPages - 1) "완료" else "다음 →"
    }
}