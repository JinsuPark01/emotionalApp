package com.example.emotionalapp.ui.mind

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
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

class AutoActivity : AppCompatActivity() {

    private lateinit var btnPrev: TextView
    private lateinit var btnNext: TextView
    private lateinit var indicatorContainer: LinearLayout
    private lateinit var pageContainer: FrameLayout
    private lateinit var titleText: TextView // 상단 타이틀 TextView

    private lateinit var tabPractice: TextView
    private lateinit var tabRecord: TextView
    private lateinit var underlinePractice: View
    private lateinit var underlineRecord: View

    private var selectedTrapIndex = -1 // 3페이지 선택값 저장

    private val totalPages = 5
    private var currentPage = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training_frame)

        btnPrev = findViewById(R.id.btnPrev)
        btnNext = findViewById(R.id.btnNext)
        indicatorContainer = findViewById(R.id.indicatorContainer)
        pageContainer = findViewById(R.id.pageContainer)
        titleText = findViewById(R.id.titleText)

        tabPractice       = findViewById(R.id.tabPractice)
        tabRecord         = findViewById(R.id.tabRecord)
        underlinePractice = findViewById(R.id.underlinePractice)
        underlineRecord   = findViewById(R.id.underlineRecord)


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

        // 탭 리스너 & 초기 탭
        tabPractice.setOnClickListener { selectTab(true) }
        tabRecord  .setOnClickListener { selectTab(false) }
        selectTab(true)
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
            0 -> "자동적 평가"
            1 -> "생각의 덫 기록"
            2 -> "자동적 사고 평가하기"
            3 -> "생각의 덫 목록"
            4 -> "자동적 사고 평가하기"
            else -> "자동적 평가"
        }

        // 현재 페이지에 맞는 레이아웃 inflate
        val pageView = when (currentPage) {
            0 -> inflater.inflate(R.layout.fragment_mind_auto_training_0, pageContainer, false)
            1 -> inflater.inflate(R.layout.fragment_mind_auto_training_1, pageContainer, false)
            2 -> inflater.inflate(R.layout.fragment_mind_auto_training_2, pageContainer, false)
            3 -> inflater.inflate(R.layout.fragment_mind_auto_training_3, pageContainer, false)
            4 -> inflater.inflate(R.layout.fragment_mind_auto_training_4, pageContainer, false)
            else -> inflater.inflate(R.layout.fragment_mind_auto_training_0, pageContainer, false)
        }

        pageContainer.addView(pageView)

        // 페이지별 동작 처리 - 여기서 작성
        if (currentPage == 0) {
            val titleText = pageView.findViewById<TextView>(R.id.textTitle)
            val descriptionText = pageView.findViewById<TextView>(R.id.textDescription)

            titleText.text = "오늘의 훈련"
            descriptionText.text = """
                지금까지 우리는 자동적 사고와 생각의 덫에 대해 알아보았어요. 이번에는 정서를 함께 살펴보려고해요. 어려운 감정을 느끼거나 특정 상황을 경험했을 때, 여기에 기록해봅시다. 여러분들이 되돌아보고 싶은 상황도 좋아요. 언제 어디서 무엇을 하고 있었는지 구체적인 상황과, 그 상황이나 감정을 마주하였을 때 자동적으로 떠오른 생각은 무엇이었는지, 어떤 감정을 느꼈으며, 강도는 어느 정도인지 천천히 헤아려봅시다. 그동안 연습했던 훈련을 되돌아보며, 여러분의 생각의 덫을 찾아보고 대안적인 평가까지도 만들어봐요.
                """.trimIndent()
        } else if (currentPage == 1) {
        } else if (currentPage == 2) {
            val answer1 = pageView.findViewById<EditText>(R.id.answer1)
            val answer2 = pageView.findViewById<EditText>(R.id.answer2)
            val answer3 = pageView.findViewById<EditText>(R.id.answer3)
        } else if (currentPage == 3) {
            val optionContainer = pageView.findViewById<LinearLayout>(R.id.optionContainerTrap2)
            val btnSave = pageView.findViewById<Button>(R.id.btnSaveTrap2)

            val options = listOf(
                "성급하게 결론짓기\n -이 비행기가 추락할 확률은 90%야. (실제 확률은 0.000013%)",
                "최악을 생각하기\n -부모님이 집에 늦게 들어오시네. 사고를 당한 것 같아.",
                "긍정적인 면 무시하기\n -시험문제가 우연히 쉬워서 좋은 점수를 받았을 뿐이야.",
                "흑백사고\n -시험에서 100점을 받지 못한다면 나는 실패자야.",
                "점쟁이 사고 (지레짐작하기)\n -연주회를 망칠 거야, 공연을 하지 않겠어.",
                "독심술\n -한 번도 대화를 나누지는 않았지만, 쟤는 나를 좋아하지 않아.",
                "정서적 추리\n -애인이 일 때문에 늦는다고 했지만, 그게 아닌 것 같아. 직감이 와. 나를 속이는 게 틀림없어.",
                "꼬리표 붙이기\n -나는 멍청해.",
                "“해야만 한다“는 진술문\n -사람들은 모두 정직해야해. 거짓말을 하는 건 있을 수 없는 일이야.",
                "마술적 사고\n -내가 아버지에게 전화를 걸면 아버지는 사고를 피할 수 있을 거야."
            )

            var selectedIndex = -1

            options.forEachIndexed { index, text ->
                val card = layoutInflater.inflate(R.layout.item_option_card, optionContainer, false) as CardView
                val textView = card.findViewById<TextView>(R.id.textOption)
                textView.text = text

                card.setOnClickListener {
                    // 선택한 카드 강조
                    for (i in 0 until optionContainer.childCount) {
                        val childCard = optionContainer.getChildAt(i) as CardView
                        childCard.setCardBackgroundColor(Color.WHITE)
                    }
                    card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.gray))
                    selectedIndex = index
                }

                optionContainer.addView(card)
            }

            btnSave.setOnClickListener {
                if (selectedIndex != -1) {
                    val selectedText = options[selectedIndex]
                    Toast.makeText(this, "선택한 답변: $selectedText", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "하나를 선택해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
        } else if (currentPage == 4) {
            val answer5 = pageView.findViewById<EditText>(R.id.answer5)


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

    // 선택된 탭에 따른 동작 여기에 작성해야함
    private fun selectTab(practice: Boolean) {
        tabPractice.setTextColor(
            resources.getColor(if (practice) R.color.black else R.color.gray, null)
        )
        tabRecord.setTextColor(
            resources.getColor(if (practice) R.color.gray else R.color.black, null)
        )
        underlinePractice.visibility = if (practice) View.VISIBLE else View.GONE
        underlineRecord.visibility = if (practice) View.GONE    else View.VISIBLE
    }
}
