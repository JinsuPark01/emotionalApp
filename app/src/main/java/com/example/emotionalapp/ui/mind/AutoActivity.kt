package com.example.emotionalapp.ui.mind

import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.emotionalapp.R
import com.example.emotionalapp.ui.alltraining.AllTrainingPageActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AutoActivity : AppCompatActivity() {

    private lateinit var btnPrev: TextView
    private lateinit var btnNext: TextView
    private lateinit var indicatorContainer: LinearLayout
    private lateinit var pageContainer: FrameLayout
    private lateinit var titleText: TextView

    private val totalPages = 4
    private var currentPage = 0

    private val answerList = mutableListOf("", "", "", "", "")
    private var selectedTrapIndex = -1
    private var selectedTrapText = ""

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
            saveCurrentInput()
            if (currentPage > 0) {
                currentPage--
                updatePage()
            }
        }

        btnNext.setOnClickListener {
            if (!validateCurrentPage()) return@setOnClickListener
            btnNext.isEnabled = false

            saveCurrentInput()
            if (currentPage < totalPages - 1) {
                currentPage++
                updatePage()
                btnNext.isEnabled = true
            } else {
                // 마지막 페이지 - 저장 처리 (Coroutine)
                lifecycleScope.launch {
                    val success = withContext(Dispatchers.IO) {
                        saveToFirestoreSuspend()
                    }

                    if (success) {
                        AlertDialog.Builder(this@AutoActivity)
                            .setTitle("수고 많으셨습니다.")
                            .setMessage("우리는 누구나 익숙한 방식으로 세상을 바라보며 살아갑니다. 여러분은 그 익숙함을 잠시 멈추고, 다른 시선과 해석의 가능성을 연습하셨습니다. 물론 훈련이 끝났다고 해서 완벽하게 새로운 해석이 바로 떠오르지 않을 수 있어요. 중요한 것은 해석을 바꿀 수 있다는 가능성을 기억하는 것입니다.\n\n앞으로도 감정을 흔들리게 하는 생각이 떠오를 때, “내가 지금 어떻게 해석하고 있는 걸까?”, “혹시 다른 해석도 가능하지 않을까?”라는 질문을 마음속에 떠올려보세요. 감정은 우리가 세상을 어떻게 해석하는지에 따라 달라집니다. 그리고 해석은 언제든지 다시 바라보고, 선택할 수 있는 것입니다. 앞으로도 연습을 통해 생각의 범위를 더욱 늘려가봅시다.")
                            .setPositiveButton("확인") { _, _ ->
                                startActivity(Intent(this@AutoActivity, AllTrainingPageActivity::class.java))
                                finish()
                            }
                            .setCancelable(false)
                            .show()
                    } else {
                        Toast.makeText(this@AutoActivity, "저장에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        btnNext.isEnabled = true
                    }
                }
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
        pageContainer.removeAllViews()

        titleText.text = when (currentPage) {
            0 -> "자동적 평가"
            1 -> "자동적 사고 평가하기"
            2 -> "생각의 덫 목록"
            3 -> "자동적 사고 평가하기"
            else -> "자동적 평가"
        }

        val pageView = when (currentPage) {
            0 -> inflater.inflate(R.layout.fragment_mind_auto_training_0, pageContainer, false)
            1 -> inflater.inflate(R.layout.fragment_mind_auto_training_1, pageContainer, false)
            2 -> inflater.inflate(R.layout.fragment_mind_auto_training_2, pageContainer, false)
            3 -> inflater.inflate(R.layout.fragment_mind_auto_training_3, pageContainer, false)
            else -> inflater.inflate(R.layout.fragment_mind_auto_training_0, pageContainer, false)
        }

        pageContainer.addView(pageView)

        when (currentPage) {
            1 -> {
                val a1 = pageView.findViewById<EditText>(R.id.answer1)
                val a2 = pageView.findViewById<EditText>(R.id.answer2)
                val a3 = pageView.findViewById<EditText>(R.id.answer3)

                a1?.setText(answerList[0])
                a2?.setText(answerList[1])
                a3?.setText(answerList[2])
            }

            2 -> {
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

                optionContainer.removeAllViews()
                options.forEachIndexed { index, text ->
                    val card = layoutInflater.inflate(R.layout.item_option_card, optionContainer, false) as CardView
                    val textView = card.findViewById<TextView>(R.id.textOption)
                    textView.text = text

                    if (index == selectedTrapIndex) {
                        card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.gray))
                    }

                    card.setOnClickListener {
                        for (i in 0 until optionContainer.childCount) {
                            val child = optionContainer.getChildAt(i) as CardView
                            child.setCardBackgroundColor(Color.WHITE)
                        }
                        card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.gray))
                        selectedTrapIndex = index
                        selectedTrapText = text
                    }

                    optionContainer.addView(card)
                }
            }

            3 -> {
                val a5 = pageView.findViewById<EditText>(R.id.answer5)
                a5?.setText(answerList[4])

                val accordionHeader = pageView.findViewById<LinearLayout>(R.id.accordionQuestionList)
                val descView = pageView.findViewById<TextView>(R.id.tvQuestionListDesc)

                // 아코디언 클릭 이벤트
                accordionHeader?.setOnClickListener {
                    if (descView.visibility == View.GONE) {
                        descView.visibility = View.VISIBLE
                    } else {
                        descView.visibility = View.GONE
                    }
                }
            }
        }

        btnPrev.isEnabled = currentPage != 0
        btnPrev.backgroundTintList = if (currentPage == 0)
            ColorStateList.valueOf(Color.parseColor("#D9D9D9"))
        else
            ColorStateList.valueOf(Color.parseColor("#3CB371"))

        btnNext.text = if (currentPage == totalPages - 1) "완료 →" else "다음 →"

        for (i in 0 until indicatorContainer.childCount) {
            val dot = indicatorContainer.getChildAt(i)
            dot.setBackgroundResource(
                if (i == currentPage) R.drawable.ic_dot_circle_black else R.drawable.ic_dot_circle_gray
            )
        }
    }

    private fun validateCurrentPage(): Boolean {
        val currentView = pageContainer.getChildAt(0) ?: return false

        return when (currentPage) {
            1 -> {
                val a1 = currentView.findViewById<EditText>(R.id.answer1)?.text?.toString()?.trim()
                val a2 = currentView.findViewById<EditText>(R.id.answer2)?.text?.toString()?.trim()
                val a3 = currentView.findViewById<EditText>(R.id.answer3)?.text?.toString()?.trim()
                if (a1.isNullOrEmpty() || a2.isNullOrEmpty() || a3.isNullOrEmpty()) {
                    Toast.makeText(this, "모든 항목을 입력해주세요.", Toast.LENGTH_SHORT).show()
                    false
                } else true
            }

            2 -> {
                if (selectedTrapIndex == -1) {
                    Toast.makeText(this, "생각의 덫 항목을 선택해주세요.", Toast.LENGTH_SHORT).show()
                    false
                } else true
            }

            3 -> {
                val a5 = currentView.findViewById<EditText>(R.id.answer5)?.text?.toString()?.trim()
                if (a5.isNullOrEmpty()) {
                    Toast.makeText(this, "답변을 입력해주세요.", Toast.LENGTH_SHORT).show()
                    false
                } else true
            }

            else -> true // 0페이지는 설명이므로 유효성 검사 없음
        }
    }

    private fun saveCurrentInput() {
        val currentView = pageContainer.getChildAt(0) ?: return

        when (currentPage) {
            1 -> {
                answerList[0] = currentView.findViewById<EditText>(R.id.answer1)?.text?.toString() ?: ""
                answerList[1] = currentView.findViewById<EditText>(R.id.answer2)?.text?.toString() ?: ""
                answerList[2] = currentView.findViewById<EditText>(R.id.answer3)?.text?.toString() ?: ""
            }
            3 -> {
                answerList[4] = currentView.findViewById<EditText>(R.id.answer5)?.text?.toString() ?: ""
            }
        }
    }

    private suspend fun saveToFirestoreSuspend(): Boolean = suspendCoroutine { continuation ->
        val user = FirebaseAuth.getInstance().currentUser
        val userEmail = user?.email
        if (user == null || userEmail == null) {
            continuation.resume(false)
            return@suspendCoroutine
        }

        val db = FirebaseFirestore.getInstance()
        val timestamp = Timestamp.now()

        val sdf = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        val docId = sdf.format(timestamp.toDate())

        val data = hashMapOf(
            "answer1" to answerList[0],
            "answer2" to answerList[1],
            "answer3" to answerList[2],
            "trap" to selectedTrapText,
            "answer5" to answerList[4],
            "date" to timestamp
        )

        db.collection("user").document(userEmail)
            .collection("mindAuto").document(docId)
            .set(data)
            .addOnSuccessListener {
                db.collection("user").document(userEmail)
                    .update("countComplete.auto", FieldValue.increment(1))
                    .addOnSuccessListener {
                        Log.d("Firestore", "카운트 증가 성공")
                        continuation.resume(true)
                    }
                    .addOnFailureListener { e ->
                        Log.w("Firestore", "카운트 증가 실패", e)
                        continuation.resume(false)
                    }
            }
            .addOnFailureListener {
                continuation.resume(false)
            }
    }
}