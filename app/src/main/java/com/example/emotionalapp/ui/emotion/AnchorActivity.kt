package com.example.emotionalapp.ui.emotion

import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
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
import com.example.emotionalapp.ui.login_signup.LoginActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import com.example.emotionalapp.util.setSingleListener

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
    private var selectedQ1Index: Int = -1
    private var selectedQ2Index: Int = -1
    private var page3Answer1: String = ""
    private var page3Answer2: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training_frame)

        btnPrev = findViewById(R.id.btnPrev)
        btnNext = findViewById(R.id.btnNext)
        indicatorContainer = findViewById(R.id.indicatorContainer)
        pageContainer = findViewById(R.id.pageContainer)
        titleText = findViewById(R.id.titleText)


        val btnBack = findViewById<View>(R.id.btnBack)
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

        setupIndicators(totalPages)
        updatePage()

        btnPrev.setOnClickListener {
            if (currentPage > 0) {
                currentPage--
                updatePage()
            }
        }

        btnNext.setSingleListener {
            if (currentPage == 1) {
                val inputText = findViewById<EditText>(R.id.editCustomAnswer)?.text?.toString()?.trim() ?: ""
                customCueInput = inputText

                if (selectedCueIndex in 0..2) {
                    val cueList = listOf(
                        "숨소리에 집중하기",
                        "심장 박동 8번 느껴보기",
                        "'옴~'소리를 5초간 내어보기"
                    )
                    selectedCue = cueList[selectedCueIndex]
                    Log.d("AnchorActivity", "선택한 단서: $selectedCue")
                } else if (customCueInput.isNotEmpty()) {
                    selectedCue = customCueInput
                    Log.d("AnchorActivity", "선택한 단서: $selectedCue")
                } else {
                    Toast.makeText(this, "단서를 선택하거나 입력해주세요.", Toast.LENGTH_SHORT).show()
                    return@setSingleListener
                }
            } else if (currentPage == 2) {
                // pageContainer 내부 현재 페이지 뷰 찾기
                val pageView = pageContainer.getChildAt(0)
                val answer1 = pageView.findViewById<EditText>(R.id.answer1)
                val answer2 = pageView.findViewById<EditText>(R.id.answer2)
                val answer3 = pageView.findViewById<EditText>(R.id.answer3)

                // 전역변수에 저장
                page2Answer1 = answer1.text.toString().trim()
                page2Answer2 = answer2.text.toString().trim()
                page2Answer3 = answer3.text.toString().trim()
                Log.d("AnchorActivity", "선택한 단서: $page2Answer1, $page2Answer2, $page2Answer3")

                // 입력 체크 (원하면)
                if (page2Answer1.isEmpty() || page2Answer2.isEmpty() || page2Answer3.isEmpty()) {
                    Toast.makeText(this, "모든 질문에 답변해주세요.", Toast.LENGTH_SHORT).show()
                    return@setSingleListener // 저장 안 하고 넘어가지 않음
                }
            } else if (currentPage == 3) {
                if (selectedQ1Index == -1 || selectedQ2Index == -1) {
                    Toast.makeText(this, "두 질문 모두 답변해주세요.", Toast.LENGTH_SHORT).show()
                    return@setSingleListener
                }
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

                val answerQ1 = optionsQ1[selectedQ1Index]
                val answerQ2 = optionsQ2[selectedQ2Index]

                // 전역 변수에 저장
                page3Answer1 = answerQ1
                page3Answer2 = answerQ2
                Log.d("AnchorActivity", "선택한 단서: $page3Answer1, $page3Answer2")

                // 중복 저장 방지
                btnNext.isEnabled = false

                // Firestore에 저장
                val user = FirebaseAuth.getInstance().currentUser
                val userEmail = user?.email

                if (user == null || userEmail == null) {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                    return@setSingleListener
                }

                val nowTimestamp = Timestamp.now()
                val nowDate = nowTimestamp.toDate()
                val today = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS", Locale.getDefault()).apply {
                    timeZone = TimeZone.getTimeZone("Asia/Seoul")
                }.format(nowDate)

                val data = hashMapOf(
                    "type" to "emotionAnchor",
                    "date" to nowTimestamp,
                    "selectedCue" to selectedCue,
                    "elements" to hashMapOf(
                        "thought" to page2Answer1,
                        "sensation" to page2Answer2,
                        "behavior" to page2Answer3
                    ),
                    "evaluation" to hashMapOf(
                        "effect" to page3Answer1,
                        "change" to page3Answer2
                    )
                )

                val db = FirebaseFirestore.getInstance()
                db.collection("user")
                    .document(userEmail)
                    .collection("emotionAnchor")
                    .document(today)
                    .set(data)
                    .addOnSuccessListener {
                        Log.d("Firestore", "데이터 저장 성공")
                        // 저장 성공 시에만 countComplete.anchor +1
                        db.collection("user")
                            .document(userEmail)
                            .update("countComplete.anchor", FieldValue.increment(1))
                            .addOnSuccessListener {
                                Log.d("Firestore", "카운트 증가 성공")
                                Toast.makeText(this@AnchorActivity, "닻 내리기 훈련 기록이 저장되었어요.", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, AllTrainingPageActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Log.w("Firestore", "카운트 증가 실패", e)
                                btnNext.isEnabled = true
                            }
                    }
                    .addOnFailureListener { e ->
                        Log.w("Firestore", "저장 실패", e)
                        Toast.makeText(this, "저장 실패. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                        btnNext.isEnabled = true
                        return@addOnFailureListener
                    }
            }

            // 페이지 이동 처리
            if (currentPage < totalPages - 1) {
                currentPage++
                updatePage()
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

            editCustomAnswer.setText(customCueInput)

            val options = listOf(
                "숨소리에 집중하기",
                "심장 박동 8번 느껴보기",
                "'옴~'소리를 5초간 내어보기"
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

                if (index == selectedCueIndex) {
                    card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.gray))
                } else {
                    card.setCardBackgroundColor(Color.WHITE)
                }

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

            answer1.setText(page2Answer1)
            answer2.setText(page2Answer2)
            answer3.setText(page2Answer3)
        } else if (currentPage == 3) {
            val optionContainerQ1 = pageView.findViewById<LinearLayout>(R.id.optionContainerQ1)
            val optionContainerQ2 = pageView.findViewById<LinearLayout>(R.id.optionContainerQ2)

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

            selectedQ1Index = -1
            selectedQ2Index = -1

            optionContainerQ1.removeAllViews()
            optionContainerQ2.removeAllViews()

            optionsQ1.forEachIndexed { index, text ->
                val card = layoutInflater.inflate(R.layout.item_option_card, optionContainerQ1, false) as CardView
                val textView = card.findViewById<TextView>(R.id.textOption)
                textView.text = text

                card.setOnClickListener {
                    for (i in 0 until optionContainerQ1.childCount) {
                        val child = optionContainerQ1.getChildAt(i) as CardView
                        child.setCardBackgroundColor(Color.WHITE)
                    }
                    card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.gray))
                    selectedQ1Index = index
                }

                optionContainerQ1.addView(card)
            }

            optionsQ2.forEachIndexed { index, text ->
                val card = layoutInflater.inflate(R.layout.item_option_card, optionContainerQ2, false) as CardView
                val textView = card.findViewById<TextView>(R.id.textOption)
                textView.text = text

                card.setOnClickListener {
                    for (i in 0 until optionContainerQ2.childCount) {
                        val child = optionContainerQ2.getChildAt(i) as CardView
                        child.setCardBackgroundColor(Color.WHITE)
                    }
                    card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.gray))
                    selectedQ2Index = index
                }

                optionContainerQ2.addView(card)
            }
        }


        // 이전 버튼 상태
        btnPrev.isEnabled = currentPage != 0
        btnPrev.backgroundTintList = if (currentPage == 0)
            ColorStateList.valueOf(Color.parseColor("#D9D9D9")) // 비활성화 색상
        else
            ColorStateList.valueOf(Color.parseColor("#3CB371")) // 활성화 색상

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
