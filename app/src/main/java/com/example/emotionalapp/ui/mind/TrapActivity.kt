package com.example.emotionalapp.ui.mind

import android.app.AlertDialog
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
import androidx.lifecycle.lifecycleScope
import com.example.emotionalapp.R
import com.example.emotionalapp.ui.alltraining.AllTrainingPageActivity
import com.example.emotionalapp.ui.login_signup.LoginActivity
import com.example.emotionalapp.util.setSingleListener
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import kotlin.coroutines.resume

class TrapActivity : AppCompatActivity() {

    private lateinit var btnPrev: TextView
    private lateinit var btnNext: TextView
    private lateinit var indicatorContainer: LinearLayout
    private lateinit var pageContainer: FrameLayout
    private lateinit var titleText: TextView // 상단 타이틀 TextView

    private var responsePage1Answer1: String = ""
    private var responsePage1Answer2: String = ""

    private var selectedPage2Index: Int = -1
    private var responsePage2Text: String = ""

    private var selectedTrapIndex: Int = -1 // 3페이지 선택값 저장

    private var responsePage4ZeroAnswers: Array<String> = Array(4) { "" }
    private var responsePage4OneAnswers: Array<String> = Array(4) { "" }
    private var responsePage4TwoAnswers: Array<String> = Array(3) { "" }

    private var responsePage6Text: String = ""

    private val totalPages = 8
    private var currentPage = 0

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
                // pageContainer 내부 현재 페이지 뷰 찾기
                val pageView = pageContainer.getChildAt(0)
                val answer1 = pageView.findViewById<EditText>(R.id.answer1)
                val answer2 = pageView.findViewById<EditText>(R.id.answer2)

                // 전역변수에 저장
                val r1 = answer1.text.toString().trim()
                val r2 = answer2.text.toString().trim()

                if (r1.isEmpty() || r2.isEmpty()) {
                    Toast.makeText(this, "모든 질문에 답변해주세요.", Toast.LENGTH_SHORT).show()
                    return@setSingleListener
                }

                responsePage1Answer1 = r1
                responsePage1Answer2 = r2
                Log.d("TrapActivity", "저장된 답변: $responsePage1Answer1, $responsePage1Answer2")

            } else if (currentPage == 2) {
                if (selectedPage2Index in 0..9) {
                    val page2List = listOf(
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
                    responsePage2Text = page2List[selectedPage2Index]
                    Log.d("TrapActivity", "선택한 단서: $responsePage2Text")
                } else {
                    Toast.makeText(this, "덫을 선택해주세요.", Toast.LENGTH_SHORT).show()
                    return@setSingleListener
                }
            } else if (currentPage == 3) {
                if (selectedTrapIndex != -1) {
                    Log.d("TrapActivity", "선택한 덫 번호: $selectedTrapIndex")
                } else {
                    Toast.makeText(this, "질문을 선택해주세요.", Toast.LENGTH_SHORT).show()
                    return@setSingleListener
                }
            } else if (currentPage == 4) {
                // pageContainer 내부 현재 페이지 뷰 찾기
                val pageView = pageContainer.getChildAt(0)
                val answer1 = pageView.findViewById<EditText>(R.id.answer1)
                val answer2 = pageView.findViewById<EditText>(R.id.answer2)
                val answer3 = pageView.findViewById<EditText>(R.id.answer3)
                val answer4 = pageView.findViewById<EditText?>(R.id.answer4)

                val a1 = answer1.text.toString().trim()
                val a2 = answer2.text.toString().trim()
                val a3 = answer3.text.toString().trim()
                val a4 = answer4?.text?.toString()?.trim() ?: ""

                // 공란 체크
                val inputs = listOf(a1, a2, a3) + if (selectedTrapIndex != 2) listOf(a4) else emptyList()
                if (inputs.any { it.isEmpty() }) {
                    Toast.makeText(this, "모든 항목을 입력해주세요.", Toast.LENGTH_SHORT).show()
                    return@setSingleListener  // 저장 없이 리턴
                }

                // 전역 변수에 저장
                when (selectedTrapIndex) {
                    0 -> {
                        responsePage4ZeroAnswers[0] = a1
                        responsePage4ZeroAnswers[1] = a2
                        responsePage4ZeroAnswers[2] = a3
                        responsePage4ZeroAnswers[3] = a4
                        Log.d("TrapActivity", "저장된 답변: $responsePage4ZeroAnswers")

                    }
                    1 -> {
                        responsePage4OneAnswers[0] = a1
                        responsePage4OneAnswers[1] = a2
                        responsePage4OneAnswers[2] = a3
                        responsePage4OneAnswers[3] = a4
                        Log.d("TrapActivity", "저장된 답변: $responsePage4OneAnswers")
                    }
                    2 -> {
                        responsePage4TwoAnswers[0] = a1
                        responsePage4TwoAnswers[1] = a2
                        responsePage4TwoAnswers[2] = a3
                        Log.d("TrapActivity", "저장된 답변: $responsePage4TwoAnswers")
                    }
                    else -> { Log.d("TrapActivity", "selectedTrapIndex가 올바르지 않습니다.") }
                }

            } else if (currentPage == 6) {
                // pageContainer 내부 현재 페이지 뷰 찾기
                val pageView = pageContainer.getChildAt(0)
                val answer1 = pageView.findViewById<EditText>(R.id.answer1)

                responsePage6Text = answer1.text.toString().trim()
                Log.d("TrapActivity", "저장된 답변: $responsePage6Text")

                if (responsePage6Text.isEmpty()) {
                    Toast.makeText(this, "모든 항목을 입력해주세요.", Toast.LENGTH_SHORT).show()
                    return@setSingleListener  // 저장 없이 리턴
                }

                // Firestore에 저장
                btnNext.isEnabled = false

                lifecycleScope.launch {
                    val success = withContext(Dispatchers.IO) {
                        saveMindTrapDataToFirestore()
                    }
                    btnNext.isEnabled = true

                    if (success) {
                        moveToNextPageOrFinish()
                    } else {
                        Toast.makeText(this@TrapActivity, "저장 실패. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                    }
                }
                return@setSingleListener
            }
            moveToNextPageOrFinish()
        }

    }

    private fun moveToNextPageOrFinish() {
        if (currentPage < totalPages - 1) {
            currentPage++
            updatePage()
        } else {
            // 마지막 페이지에서 완료 시 다른 액티비티 이동
            Toast.makeText(this@TrapActivity, "생각의 덫이 기록되었습니다.", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, AllTrainingPageActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private suspend fun saveMindTrapDataToFirestore(): Boolean = suspendCancellableCoroutine { continuation ->
        val user = FirebaseAuth.getInstance().currentUser
        val userEmail = user?.email

        if (user == null || userEmail == null) {
            continuation.resume(false)
            return@suspendCancellableCoroutine
        }

        val nowTimestamp = Timestamp.now()
        val nowDate = nowTimestamp.toDate()
        val today = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("Asia/Seoul")
        }.format(nowDate)

        val data = hashMapOf(
            "type" to "mindTrap",
            "date" to nowTimestamp,
            "situation" to responsePage1Answer1,
            "thought" to responsePage1Answer2,
            "trap" to responsePage2Text,
            "validity" to hashMapOf(
                "answer1" to responsePage4ZeroAnswers[0],
                "answer2" to responsePage4ZeroAnswers[1],
                "answer3" to responsePage4ZeroAnswers[2],
                "answer4" to responsePage4ZeroAnswers[3]
            ),
            "assumption" to hashMapOf(
                "answer1" to responsePage4OneAnswers[0],
                "answer2" to responsePage4OneAnswers[1],
                "answer3" to responsePage4OneAnswers[2],
                "answer4" to responsePage4OneAnswers[3]
            ),
            "perspective" to hashMapOf(
                "answer1" to responsePage4TwoAnswers[0],
                "answer2" to responsePage4TwoAnswers[1],
                "answer3" to responsePage4TwoAnswers[2]
            ),
            "alternative" to responsePage6Text
        )

        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("user").document(userEmail)

        // 1. 문서 저장
        userRef.collection("mindTrap")
            .document(today)
            .set(data)
            .addOnSuccessListener {
                // 2. countComplete.trap 증가
                userRef.update("countComplete.trap", FieldValue.increment(1))
                    .addOnSuccessListener {
                        // 3. 카운트 값 확인
                        userRef.get().addOnSuccessListener { document ->
                            val trapCount = document.get("countComplete.trap") as? Long ?: 0L
                            Log.d("TrapActivity", "현재 trap 기록 수: $trapCount")

                            // 조건 만족 시 팝업 표시
                            if (trapCount >= 3) {
                                runOnUiThread {
                                    AlertDialog.Builder(this@TrapActivity)
                                        .setTitle("생각의 덫 통계 잠금해제!")
                                        .setMessage("기록보기에서 생각의 덫 통계를 확인할 수 있습니다!")
                                        .setPositiveButton("확인") { dialog, _ ->
                                            dialog.dismiss()
                                            continuation.resume(true) // 저장 성공 처리
                                        }
                                        .setCancelable(false)
                                        .show()
                                }
                            } else {
                                continuation.resume(true)
                            }
                        }.addOnFailureListener {
                            Log.w("Firestore", "카운트 가져오기 실패", it)
                            continuation.resume(false)
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.w("Firestore", "카운트 증가 실패", e)
                        continuation.resume(false)
                    }
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "저장 실패", e)
                continuation.resume(false)
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
            0 -> "자동적 사고와 생각의 덫" //2-2 완료
            1 -> "생각의 덫 파악하기" // 2-3 완료
            2 -> "생각의 덫 파악하기" //2-4 완료
            3 -> "생각의 덫 풀어내기" //2-5 완료
            4 -> "생각의 덫 풀어내기" // 2-5에서 한 선택에 따라 동적으로 변화시키기
            5 -> "생각의 덫 풀어내기" // 선택에 따라 2-5로 되돌아가기
            6 -> "생각의 덫 풀어내기"
            7 -> "생각의 덫 풀어내기"
            else -> "생각의 덫"
        }
        
        // 현재 페이지에 맞는 레이아웃 inflate
        val pageView = when (currentPage) {
            0 -> inflater.inflate(R.layout.fragment_mind_trap_training_0, pageContainer, false)
            1 -> inflater.inflate(R.layout.fragment_mind_trap_training_1, pageContainer, false)
            2 -> inflater.inflate(R.layout.fragment_mind_trap_training_2, pageContainer, false)
            3 -> inflater.inflate(R.layout.fragment_mind_trap_training_3, pageContainer, false)
            4 -> {
                when (selectedTrapIndex) {
                    0 -> inflater.inflate(R.layout.fragment_mind_trap_training_4_0, pageContainer, false)
                    1 -> inflater.inflate(R.layout.fragment_mind_trap_training_4_1, pageContainer, false)
                    2 -> inflater.inflate(R.layout.fragment_mind_trap_training_4_2, pageContainer, false)
                    else -> throw IllegalStateException("선택된 옵션이 없습니다.")
                }
            }
            5 -> inflater.inflate(R.layout.fragment_mind_trap_training_5, pageContainer, false)
            6 -> inflater.inflate(R.layout.fragment_mind_trap_training_6, pageContainer, false)
            7 -> inflater.inflate(R.layout.fragment_mind_trap_training_0, pageContainer, false)
            else -> inflater.inflate(R.layout.fragment_mind_trap_training_0, pageContainer, false)
        }

        pageContainer.addView(pageView)

        // 페이지별 동작 처리 - 여기서 작성
        if (currentPage == 0) {
            val titleText = pageView.findViewById<TextView>(R.id.textTitleTrap0)
            val descriptionText = pageView.findViewById<TextView>(R.id.textDescriptionTrap0)

            titleText.text = "오늘의 훈련"
            descriptionText.text = """
                우리가 어떤 사건이나 상황을 경험할 때 자동적으로 떠오르는 생각이 있는데, 이를 '자동적 평가', 혹은 ‘자동적 사고’라고 합니다.
                
                시간이 지나면서 사람들은 상황을 평가하는 특정 방식이나 스타일을 개발하게 됩니다.
                자동적 사고는 우리가 받아들이는 정보를 신속하고 효율적으로 처리하도록 도울 수 있지만, 종종 비합리적이거나 왜곡된 '생각의 덫(thinking traps)'이 포함될 수 있어요.
                
                이러한 자동적 평가들이 문제가 되는 것은 이런 평가들이 ‘나쁘거나’ ‘잘못된’ 사고방식이기 때문이 아니라, 주어진 상황에 관한 해석을 제한하기 때문이에요.
                
                이번 모듈 훈련에서는 이러한 흔한 사고의 함정들을 배우고, 자신에게 어떤 함정이 자주 나타나는지 알아차리는 연습을 할 것입니다.
                """.trimIndent()
        } else if (currentPage == 1) {
            val answer1 = pageView.findViewById<EditText>(R.id.answer1)
            val answer2 = pageView.findViewById<EditText>(R.id.answer2)

            answer1.setText(responsePage1Answer1)
            answer2.setText(responsePage1Answer2)
        } else if (currentPage == 2) {
            val optionContainer = pageView.findViewById<LinearLayout>(R.id.optionContainerTrap2)

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


            options.forEachIndexed { index, text ->
                val card = layoutInflater.inflate(R.layout.item_option_card, optionContainer, false) as CardView
                val textView = card.findViewById<TextView>(R.id.textOption)
                textView.text = text

                if (index == selectedPage2Index) {
                    card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.gray))
                } else {
                    card.setCardBackgroundColor(Color.WHITE)
                }

                card.setOnClickListener {
                    // 선택한 카드 강조
                    for (i in 0 until optionContainer.childCount) {
                        val childCard = optionContainer.getChildAt(i) as CardView
                        childCard.setCardBackgroundColor(Color.WHITE)
                    }
                    card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.gray))
                    selectedPage2Index = index
                }

                optionContainer.addView(card)
            }
        } else if (currentPage == 3) {
            val optionContainer = pageView.findViewById<LinearLayout>(R.id.optionContainerTrap2)

            val options = listOf(
                "그 생각이 확실할까요?\n - 생각의 타당성 점검하기",
                "그 생각이 만약 실제라면 얼마나 나쁠까요?\n - 생각을 실제로 가정하기",
                "객관적으로 살펴볼까요?\n - 관점을 다르게 해보기"
            )

            options.forEachIndexed { index, text ->
                val card = layoutInflater.inflate(R.layout.item_option_card, optionContainer, false) as CardView
                val textView = card.findViewById<TextView>(R.id.textOption)
                textView.text = text

                if (index == selectedTrapIndex) {
                    card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.gray))
                } else {
                    card.setCardBackgroundColor(Color.WHITE)
                }

                card.setOnClickListener {
                    // 선택한 카드 강조
                    for (i in 0 until optionContainer.childCount) {
                        val childCard = optionContainer.getChildAt(i) as CardView
                        childCard.setCardBackgroundColor(Color.WHITE)
                    }
                    card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.gray))
                    selectedTrapIndex = index
                }

                optionContainer.addView(card)
            }
        } else if (currentPage == 4) {
            val answer1 = pageView.findViewById<EditText>(R.id.answer1)
            val answer2 = pageView.findViewById<EditText>(R.id.answer2)
            val answer3 = pageView.findViewById<EditText>(R.id.answer3)
            val answer4 = pageView.findViewById<EditText?>(R.id.answer4)

            when (selectedTrapIndex) {
                0 -> {
                    answer1.setText(responsePage4ZeroAnswers[0])
                    answer2.setText(responsePage4ZeroAnswers[1])
                    answer3.setText(responsePage4ZeroAnswers[2])
                    answer4.setText(responsePage4ZeroAnswers[3])
                }
                1 -> {
                    answer1.setText(responsePage4OneAnswers[0])
                    answer2.setText(responsePage4OneAnswers[1])
                    answer3.setText(responsePage4OneAnswers[2])
                    answer4.setText(responsePage4OneAnswers[3])
                }
                2 -> {
                    answer1.setText(responsePage4TwoAnswers[0])
                    answer2.setText(responsePage4TwoAnswers[1])
                    answer3.setText(responsePage4TwoAnswers[2])
                }
            }

        } else if (currentPage == 5) {
            val btnGoBack = pageView.findViewById<Button>(R.id.btnGoBackTrap5)
            val btnContinue = pageView.findViewById<Button>(R.id.btnContinueTrap5)

            btnGoBack.setOnClickListener {
                currentPage = 3 // 2페이지로 이동
                updatePage()
            }

            btnContinue.setOnClickListener {
                currentPage = 6 // 5페이지로 이동
                updatePage()
            }
        } else if (currentPage == 6) {
            val answer1 = pageView.findViewById<EditText>(R.id.answer1)

            answer1.setText(responsePage6Text)
        } else if (currentPage == 7) {
            val titleText = pageView.findViewById<TextView>(R.id.textTitleTrap0)
            val descriptionText = pageView.findViewById<TextView>(R.id.textDescriptionTrap0)

            titleText.text = "대단해요!"
            descriptionText.text = "생각의 덫은 유연성을 낮추고, 여러 가지 다양한 해석을 못하게 할 수 있어요. 자동적 평가는 ‘나쁘거나’ ‘잘못된’ 사고방식이기 때문이 아니라, 주어진 상황에 관한 해석을 제한하기 때문에 문제가 됩니다. 따라서 우리의 목표는 상황을 평가하는 데 있어 나쁜 생각을 대체하거나 잘못된 사고방식을 ‘고치는’ 것이 아니라, 유연성을 키우는 것입니다.\n" +
                    "\n이러한 생각의 덫에서 벗어나기 위해서는 자동적 평가를 ‘객관적 사실‘이 아니라, 그 상황에 관한 가능한 해석으로 고려해야 합니다. 최악의 시나리오는 여전히 떠오를 수 있지만, 그 상황에 대해 할 수 있는 다른 평가들과 ‘공존’할 수 있어요. 우리의 목표는 생각을 유연하게 하고 정서를 유발하는 상황에서 여러 대안적 평가를 내릴 수 있도록 하는 것입니다."
        }


        // 이전 버튼 상태
        btnPrev.isEnabled = !(currentPage == 0 || currentPage == 5)
        btnPrev.backgroundTintList = if (currentPage == 0 || currentPage == 5)
            ColorStateList.valueOf(Color.parseColor("#D9D9D9"))
        else
            ColorStateList.valueOf(Color.parseColor("#3CB371"))

        btnNext.isEnabled = currentPage != 5
        btnNext.backgroundTintList = if (currentPage == 5)
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
