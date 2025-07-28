package com.example.emotionalapp.ui.emotion

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.example.emotionalapp.R
import com.example.emotionalapp.ui.alltraining.AllTrainingPageActivity

class AnchorActivity : AppCompatActivity() {

    private lateinit var btnPrev: TextView
    private lateinit var btnNext: TextView
    private lateinit var indicatorContainer: LinearLayout
    private lateinit var pageContainer: FrameLayout
    private lateinit var titleText: TextView // 상단 타이틀 TextView

    private val totalPages = 4
    private var currentPage = 0

    private var selectedCueIndex: Int = -1
    private var customCueInput: String = ""
    private var selectedCue: String = ""
    private var page2Answer1: String = ""
    private var page2Answer2: String = ""
    private var page2Answer3: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training_frame)

        btnPrev = findViewById(R.id.btnPrev)
        btnNext = findViewById(R.id.btnNext)
        indicatorContainer = findViewById(R.id.indicatorContainer)
        pageContainer = findViewById(R.id.pageContainer)
        titleText = findViewById(R.id.titleText)


        val btnBack = findViewById<View>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        setupIndicators(totalPages)
        updatePage()

        btnPrev.setOnClickListener {
            if (currentPage > 0) {
                currentPage--
                updatePage()
            }
        }

        btnNext.setOnClickListener {
            if (currentPage == 1) {
                val inputText = findViewById<EditText>(R.id.editCustomAnswer)?.text?.toString()?.trim() ?: ""
                customCueInput = inputText

                if (selectedCueIndex in 0..2) {
                    val cueList = listOf(
                        "숨소리에 집중하기",
                        "심장 박동 8번 느껴보기",
                        "'음~'소리를 5초간 내어보기"
                    )
                    selectedCue = cueList[selectedCueIndex]
                    Log.d("AnchorActivity", "선택한 단서: $selectedCue")
                } else if (customCueInput.isNotEmpty()) {
                    selectedCue = customCueInput
                    Log.d("AnchorActivity", "선택한 단서: $selectedCue")
                } else {
                    Toast.makeText(this, "단서를 선택하거나 입력해주세요.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }


            // 페이지 이동 처리
            if (currentPage < totalPages - 1) {
                currentPage++
                updatePage()
            } else {
                // 마지막 페이지에서 완료 시 다른 액티비티 이동
                val intent = Intent(this, AllTrainingPageActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

    }

    private fun setupIndicators(count: Int) {
        indicatorContainer.removeAllViews()
        repeat(count) {
            val dot = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(20, 20).apply {
                    marginStart = 8
                    marginEnd = 8
                }
                setBackgroundResource(R.drawable.ic_dot_circle_gray)
            }
            indicatorContainer.addView(dot)
        }
    }

    private fun updatePage() {
        val inflater = LayoutInflater.from(this)
        pageContainer.removeAllViews() // 기존 페이지 제거

        // 현재 페이지에 맞는 제목 설정
        titleText.text = when (currentPage) {
            0 -> "현재에 닻내리기"
            1 -> "단서 만들기"
            2 -> "정서의 3요소"
            3 -> "평가하기"
            else -> "현재에 닻내리기"
        }

        // 현재 페이지에 맞는 레이아웃 inflate
        val pageView = when (currentPage) {
            0 -> inflater.inflate(R.layout.fragment_emotion_anchor_training_0, pageContainer, false)
            1 -> inflater.inflate(R.layout.fragment_emotion_anchor_training_1, pageContainer, false)
            2 -> inflater.inflate(R.layout.fragment_emotion_anchor_training_2, pageContainer, false)
            3 -> inflater.inflate(R.layout.fragment_emotion_anchor_training_3, pageContainer, false)
            else -> inflater.inflate(R.layout.fragment_emotion_anchor_training_0, pageContainer, false)
        }

        pageContainer.addView(pageView)

        // 페이지별 동작 처리 - 여기서 작성
        if (currentPage == 0) {
            val titleText0 = pageView.findViewById<TextView>(R.id.textTitleAnchor0)
            val descriptionText0 = pageView.findViewById<TextView>(R.id.textDescriptionAnchor0)
            val titleText1 = pageView.findViewById<TextView>(R.id.textTitleAnchor1)
            val descriptionText1 = pageView.findViewById<TextView>(R.id.textDescriptionAnchor1)
            val titleText2 = pageView.findViewById<TextView>(R.id.textTitleAnchor2)
            val descriptionText2 = pageView.findViewById<TextView>(R.id.textDescriptionAnchor2)

            titleText0.text = "현재에 닻 내리기란?"
            descriptionText0.text = """
                현재에 초점을 둔 알아차림 활동이에요. 과거에 발생했던 것이나 미래에 일어날지 모를 일에 초점을 맞추는 것이 아니라 현재 맥락에서 정서반응을 일어나고 있는 그대로 관찰하는 활동입니다.
                
                강한 감정에 휩쓸릴 때, 멈추고 돌아보는 힘을 기를 수 있으며, 감정을 억누르지 않고, 있는 그대로 관찰하는 연습을 할 수 있습니다.
                """.trimIndent()

            titleText1.text = "1. 단서 선택하기"
            descriptionText1.text = """
                고통스러운 시기 동안에 현재의 순간으로 재빨리 주의를 이동하는 데 사용할 수 있는 ‘단서’를 만들어 보세요. 그 단서를 사용해 현재에 주의를 집중합니다.
            """.trimIndent()

            titleText2.text = "2. 정서의 3요소 입력하기"
            descriptionText2.text = """
                단서를 통해 현재에 초점이 맞춰졌다면 스스로에게 다음의 세 가지 질문을 해보세요.
                
                    ‘지금 나의 생각은 무엇인가?(인지)’ 
                    ‘지금 내가 경험하는 정서와 신체감각은 무엇인가? (신체감각)’
                    ‘나는 지금 무엇을 하고 있나? (행동)’
                    
                생각, 행동이나 반응을 되돌아보며 이들을 더 적응적인 반응들로 대체해보세요. 앞으로 우리는 이 세 가지를 살펴보고 변화시키는 연습을 해 볼 거에요.
            """.trimIndent()

        } else if (currentPage == 1) {
            val optionContainer = pageView.findViewById<LinearLayout>(R.id.optionContainerCustom)
            val editCustomAnswer = pageView.findViewById<EditText>(R.id.editCustomAnswer)

            val options = listOf(
                "숨소리에 집중하기",
                "심장 박동 8번 느껴보기",
                "'음~'소리를 5초간 내어보기"
            )

            // 옵션 카드 생성
            options.forEachIndexed { index, text ->
                val card = layoutInflater.inflate(
                    R.layout.item_option_card,
                    optionContainer,
                    false
                ) as CardView
                val textView = card.findViewById<TextView>(R.id.textOption)
                textView.text = text

                card.setOnClickListener {
                    // 카드 배경 초기화
                    for (i in 0 until optionContainer.childCount) {
                        val childCard = optionContainer.getChildAt(i) as CardView
                        childCard.setCardBackgroundColor(Color.WHITE)
                    }
                    editCustomAnswer.text.clear() // 입력창 초기화
                    card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.gray))

                    selectedCueIndex = index
                    customCueInput = ""
                }
                optionContainer.addView(card)
            }

            // EditText 포커스 시 카드 선택 해제
            editCustomAnswer.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    for (i in 0 until optionContainer.childCount) {
                        val childCard = optionContainer.getChildAt(i) as CardView
                        childCard.setCardBackgroundColor(Color.WHITE)
                    }
                    selectedCueIndex = -1
                }
            }

        } else if (currentPage == 2) {
            val answer1 = pageView.findViewById<EditText>(R.id.answer1)
            val answer2 = pageView.findViewById<EditText>(R.id.answer2)
            val answer3 = pageView.findViewById<EditText>(R.id.answer3)
            val btnSave = pageView.findViewById<Button>(R.id.btnSaveAnswers)

            btnSave.setOnClickListener {
                val response1 = answer1.text.toString().trim()
                val response2 = answer2.text.toString().trim()
                val response3 = answer3.text.toString().trim()

                if (response1.isNotEmpty() && response2.isNotEmpty() && response3.isNotEmpty()) {
                    // 👉 답변 저장 로직 (예: 로컬 DB, 서버 전송) 작성 여기에 할 것
                    Toast.makeText(this, "답변이 저장되었습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "모든 질문에 답변해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
        } else if (currentPage == 3) {
            val optionContainerQ1 = pageView.findViewById<LinearLayout>(R.id.optionContainerQ1)
            val optionContainerQ2 = pageView.findViewById<LinearLayout>(R.id.optionContainerQ2)
            val btnSave = pageView.findViewById<Button>(R.id.btnSaveDoubleQuestion)

            // 첫 번째 질문 옵션
            val optionsQ1 = listOf(
                "현재에 집중할 수 있었어요",
                "다른 단서를 찾아봐야 할 것 같아요"
            )

            // 두 번째 질문 옵션
            val optionsQ2 = listOf(
                "전보다 나아졌어요",
                "비슷한 거 같아요",
                "더 안 좋아졌어요"
            )

            var selectedQ1Index = -1
            var selectedQ2Index = -1

            // 첫 번째 질문 카드 생성
            optionsQ1.forEachIndexed { index, text ->
                val card = layoutInflater.inflate(R.layout.item_option_card, optionContainerQ1, false) as CardView
                val textView = card.findViewById<TextView>(R.id.textOption)
                textView.text = text

                card.setOnClickListener {
                    for (i in 0 until optionContainerQ1.childCount) {
                        val childCard = optionContainerQ1.getChildAt(i) as CardView
                        childCard.setCardBackgroundColor(Color.WHITE)
                    }
                    card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.gray))
                    selectedQ1Index = index
                }

                optionContainerQ1.addView(card)
            }

            // 두 번째 질문 카드 생성
            optionsQ2.forEachIndexed { index, text ->
                val card = layoutInflater.inflate(R.layout.item_option_card, optionContainerQ2, false) as CardView
                val textView = card.findViewById<TextView>(R.id.textOption)
                textView.text = text

                card.setOnClickListener {
                    for (i in 0 until optionContainerQ2.childCount) {
                        val childCard = optionContainerQ2.getChildAt(i) as CardView
                        childCard.setCardBackgroundColor(Color.WHITE)
                    }
                    card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.gray))
                    selectedQ2Index = index
                }

                optionContainerQ2.addView(card)
            }

            // 저장 버튼 클릭 시 선택한 값만 저장
            btnSave.setOnClickListener {
                if (selectedQ1Index != -1 && selectedQ2Index != -1) {
                    val answerQ1 = optionsQ1[selectedQ1Index]
                    val answerQ2 = optionsQ2[selectedQ2Index]
                    Toast.makeText(
                        this,
                        "답변 저장됨\nQ1: $answerQ1\nQ2: $answerQ2",
                        Toast.LENGTH_SHORT
                    ).show()
                    // 👉 답변 저장 처리 (DB, 서버 등)
                } else {
                    Toast.makeText(this, "두 질문 모두 답변해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
        }


        // 이전 버튼 상태
        btnPrev.isEnabled = currentPage != 0
        btnPrev.backgroundTintList = if (currentPage == 0)
            ColorStateList.valueOf(Color.parseColor("#D9D9D9"))
        else
            ColorStateList.valueOf(Color.parseColor("#3CB371"))

        // 다음 버튼 텍스트
        btnNext.text = if (currentPage == totalPages - 1) "완료 →" else "다음 →"

        // 인디케이터 업데이트
        for (i in 0 until indicatorContainer.childCount) {
            val dot = indicatorContainer.getChildAt(i)
            dot.setBackgroundResource(
                if (i == currentPage) R.drawable.ic_dot_circle_black else R.drawable.ic_dot_circle_gray
            )
        }
    }
}
