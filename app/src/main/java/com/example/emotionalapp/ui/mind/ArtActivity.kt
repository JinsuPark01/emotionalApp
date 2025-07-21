package com.example.emotionalapp.ui.mind

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.R
import com.example.emotionalapp.ui.alltraining.AllTrainingPageActivity

class ArtActivity : AppCompatActivity() {

    private lateinit var btnPrev: TextView
    private lateinit var btnNext: TextView
    private lateinit var indicatorContainer: LinearLayout
    private lateinit var pageContainer: FrameLayout
    private lateinit var titleText: TextView

    private lateinit var tabPractice: TextView
    private lateinit var tabRecord: TextView
    private lateinit var underlinePractice: View
    private lateinit var underlineRecord: View

    private val totalPages = 9
    private var currentPage = 0

    // 이미지 선택 인덱스 리스트 (UI용)
    private var selectedImages = mutableListOf<Int>()

    // 확정된 선택 이미지 인덱스 저장 (DB 저장용)
    private var selectedImageIndices = mutableListOf<Int>()

    // 확정된 선택 이미지 리소스 ID 저장 (DB 저장용)
    private var selectedImageResourceIds = mutableListOf<Int>()

    // 이미지 리소스 ID 배열 (선택 페이지 4개 이미지에 매칭)
    private val imageResIds = arrayOf(
        R.drawable.image1,
        R.drawable.image2,
        R.drawable.image3,
        R.drawable.image4
    )

    private val userAnswers = arrayOf(
        mutableListOf<String>(), // 첫 번째 이미지
        mutableListOf<String>()  // 두 번째 이미지
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training_frame)

        btnPrev = findViewById(R.id.btnPrev)
        btnNext = findViewById(R.id.btnNext)
        indicatorContainer = findViewById(R.id.indicatorContainer)
        pageContainer = findViewById(R.id.pageContainer)
        titleText = findViewById(R.id.titleText)

        tabPractice = findViewById(R.id.tabPractice)
        tabRecord = findViewById(R.id.tabRecord)
        underlinePractice = findViewById(R.id.underlinePractice)
        underlineRecord = findViewById(R.id.underlineRecord)

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
            // 2페이지에서 이미지 2개 선택 확인 + 확정 저장
            if (currentPage == 2) {
                if (selectedImages.size < 2) {
                    Toast.makeText(this, "이미지를 2개 선택해야 합니다.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                // 선택 확정: UI용 selectedImages -> 저장용 리스트로 복사
                selectedImageIndices.clear()
                selectedImageIndices.addAll(selectedImages)

                // 선택된 이미지 인덱스를 이용해 리소스 ID도 저장
                selectedImageResourceIds.clear()
                selectedImageIndices.forEach { index ->
                    if (index in imageResIds.indices) {
                        selectedImageResourceIds.add(imageResIds[index])
                    }
                }
            }

            // 3~8페이지 질문 답변 유효성 검사 및 저장
            if (currentPage in 3..8) {
                val pageView = pageContainer.getChildAt(0)
                val answer1 = pageView.findViewById<EditText>(R.id.answer1)
                val answer2 = pageView.findViewById<EditText>(R.id.answer2)

                val isSingleAnswerPage = (currentPage - 3) % 3 == 2
                val isValid = if (isSingleAnswerPage) {
                    answer1.text.toString().isNotBlank()
                } else {
                    answer1.text.toString().isNotBlank() && answer2.text.toString().isNotBlank()
                }

                if (!isValid) {
                    Toast.makeText(this, "모든 질문에 답변해주세요.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // 임시 저장
                val answers = mutableListOf<String>()
                answers.add(answer1.text.toString())
                if (!isSingleAnswerPage) answers.add(answer2.text.toString())

                val imageIndex = if (currentPage in 3..5) 0 else 1
                userAnswers[imageIndex].addAll(answers)
            }

            // 다음 페이지 이동 or 완료 처리
            if (currentPage < totalPages - 1) {
                currentPage++
                updatePage()
            } else {
                saveToDatabase()
                val intent = Intent(this, AllTrainingPageActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        tabPractice.setOnClickListener { selectTab(true) }
        tabRecord.setOnClickListener { selectTab(false) }
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
        pageContainer.removeAllViews()

        titleText.text = when (currentPage) {
            0 -> "인지적 평가"
            else -> "모호한 그림 연습하기"
        }

        val pageView = when (currentPage) {
            0 -> inflater.inflate(R.layout.fragment_mind_art_traning_0, pageContainer, false) //소개
            1 -> inflater.inflate(R.layout.fragment_mind_art_traning_1, pageContainer, false) //설명
            2 -> inflater.inflate(R.layout.fragment_mind_art_traning_2, pageContainer, false) // 선택
            in 3..5 -> inflater.inflate(R.layout.fragment_mind_art_traning_3, pageContainer, false) //작성
            in 6..8 -> inflater.inflate(R.layout.fragment_mind_art_traning_3, pageContainer, false) //작성 두번째 이미지 질문 페이지
            else -> inflater.inflate(R.layout.fragment_mind_art_traning_0, pageContainer, false)
        }

        pageContainer.addView(pageView)

        when (currentPage) {
            0 -> {
                val title = pageView.findViewById<TextView>(R.id.textTitle)
                val description = pageView.findViewById<TextView>(R.id.textDescription)
                title.text = "소개"
                description.text = """
                    우리가 느끼는 감정은, 상황 자체보다 ‘그 상황을 어떻게 해석했는가’에 따라 달라져요. 예를 들어, 물이 반쯤 찬 컵을 보고 ‘아직 반이나 남았네’라고 생각하면 기분이 좋아지지만, ‘벌써 반이나 줄었어’라고 생각하면 아쉬워지죠. 이처럼 감정은 ‘생각’에서 출발할 수 있습니다.

                    우리는 모두 자신만의 방식으로 세상을 해석하며 살아갑니다. 이러한 해석은 오랜 시간 반복되어 왔기에 자동적으로 떠오르는 생각이 될 수 있어요. 이번 훈련에서는 익숙한 해석을 고치려는 것이 아니라, 다른 관점도 가능하다는 점을 함께 알아보기 위한 연습을 해볼 것입니다. 여러분이 상황을 어떻게 해석하는지 돌아보고, 그 해석이 감정에 어떤 영향을 미치는지도 함께 살펴봅시다.
                """.trimIndent()
            }
            1 -> {
                val title = pageView.findViewById<TextView>(R.id.textTitle)
                val description = pageView.findViewById<TextView>(R.id.textDescription)
                val image = pageView.findViewById<ImageView>(R.id.imageEX)

                title.text = "설명"
                description.text = """
                    우리가 느끼는 감정은, 단지 상황 때문에 일어나는 것이 아니에요. 같은 상황도 내가 어떤 기분인지, 어떤 생각을 하는지에 따라 전혀 다르게 느껴질 수 있습니다.

                    다음 그림을 살펴봅시다. 같은 그림을 봐도 기분이 우울할 때에는 ‘서로 다투어 냉담한 연인의 모습’처럼 보일 수 있지만, 기분이 좋을 때는 ‘노을지는 풍경을 함께 바라보는 평온한 연인의 모습’처럼 보일 수 있어요. 같은 장면이라도 우리의 감정 상태에 따라 전혀 다른 해석이 가능합니다.

                    이렇게 우리의 감정과 사고는 서로 상호작용하며 변화할 수 있어요. 이번 훈련에서는 이러한 관계를 살펴보며, 다양한 해석을 연습해볼 것입니다.
                """.trimIndent()

                image.setImageResource(R.drawable.image_ex)
            }
            2 -> {
                val imageViews = listOf(
                    pageView.findViewById<ImageView>(R.id.image1),
                    pageView.findViewById<ImageView>(R.id.image2),
                    pageView.findViewById<ImageView>(R.id.image3),
                    pageView.findViewById<ImageView>(R.id.image4)
                )

                fun updateImageUI() {
                    imageViews.forEachIndexed { index, imageView ->
                        val isSelected = selectedImages.contains(index)
                        imageView.setBackgroundColor(
                            if (isSelected) Color.parseColor("#FFA726")
                            else Color.TRANSPARENT
                        )
                    }
                }

                imageViews.forEachIndexed { index, imageView ->
                    imageView.setOnClickListener {
                        if (selectedImages.contains(index)) {
                            selectedImages.remove(index)
                        } else {
                            if (selectedImages.size >= 2) {
                                Toast.makeText(this, "2개까지만 선택할 수 있어요.", Toast.LENGTH_SHORT).show()
                                return@setOnClickListener
                            }
                            selectedImages.add(index)
                        }
                        updateImageUI()
                    }
                }
                updateImageUI()
            }
            in 3..8 -> {
                val question1 = pageView.findViewById<TextView>(R.id.question1)
                val question2 = pageView.findViewById<TextView>(R.id.question2)
                val answer1 = pageView.findViewById<EditText>(R.id.answer1)
                val answer2 = pageView.findViewById<EditText>(R.id.answer2)

                // 첫 번째 or 두 번째 이미지
                val isFirstImagePhase = currentPage in 3..5
                val selectedImageIndex = if (isFirstImagePhase) selectedImageIndices.getOrNull(0) else selectedImageIndices.getOrNull(1)

                val phase = if (isFirstImagePhase) "첫 번째" else "두 번째"
                val localPage = (currentPage - 3) % 3

                val questions = listOf(
                    "1. 여러분들이 평가하고 싶은 상황을 되돌아봅시다. 무슨 일이 일어났나요?",
                    "2. 그때 어떤 생각을 했나요?",
                    "3. 그 감정에 어울리는 색은 어떤 색인가요?",
                    "4. 그 감정을 떠올리게 하는 이미지는 무엇인가요?",
                    "5. 지금 감정을 음악으로 표현하면 어떤 곡일까요?"
                )

                when (localPage) {
                    0 -> {
                        question1.text = questions[0]
                        question2.text = questions[1]
                        question1.visibility = View.VISIBLE
                        question2.visibility = View.VISIBLE
                        answer1.visibility = View.VISIBLE
                        answer2.visibility = View.VISIBLE

                        // 이미지 표시
                        val imageView = pageView.findViewById<ImageView>(R.id.image)
                        imageView.setImageResource(selectedImageResourceIds[0])
                        imageView.visibility = View.VISIBLE
                    }
                    1 -> {
                        question1.text = questions[2]
                        question2.text = questions[3]
                        question1.visibility = View.VISIBLE
                        question2.visibility = View.VISIBLE
                        answer1.visibility = View.VISIBLE
                        answer2.visibility = View.VISIBLE

                        // 이미지 표시
                        val imageView = pageView.findViewById<ImageView>(R.id.image)
                        imageView.setImageResource(selectedImageResourceIds[0])
                        imageView.visibility = View.VISIBLE
                    }
                    2 -> { // 질문 5만
                        question1.text = questions[4]
                        question1.visibility = View.VISIBLE
                        question2.visibility = View.GONE
                        answer1.visibility = View.VISIBLE
                        answer2.visibility = View.GONE

                        // 이미지 표시
                        val imageView = pageView.findViewById<ImageView>(R.id.image)
                        imageView.setImageResource(selectedImageResourceIds[0])
                        imageView.visibility = View.VISIBLE
                    }
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

    private fun selectTab(practice: Boolean) {
        tabPractice.setTextColor(
            resources.getColor(if (practice) R.color.black else R.color.gray, null)
        )
        tabRecord.setTextColor(
            resources.getColor(if (practice) R.color.gray else R.color.black, null)
        )
        underlinePractice.visibility = if (practice) View.VISIBLE else View.GONE
        underlineRecord.visibility = if (practice) View.GONE else View.VISIBLE
    }

    private fun saveToDatabase() {
        val result = hashMapOf(
            "selectedImageIndices" to selectedImageIndices,
            "selectedImageResourceIds" to selectedImageResourceIds,
            "firstImageAnswers" to userAnswers[0],
            "secondImageAnswers" to userAnswers[1],
            "timestamp" to System.currentTimeMillis()
        )

        // 실제 Firebase 저장 주석 해제 및 사용
        // FirebaseFirestore.getInstance().collection("art_training").add(result)
    }
}
