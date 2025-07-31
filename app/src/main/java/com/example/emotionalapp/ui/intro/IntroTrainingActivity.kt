package com.example.emotionalapp.ui.intro

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.MediaController
import android.widget.TextView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.R
import com.example.emotionalapp.ui.alltraining.AllTrainingPageActivity

class IntroTrainingActivity : AppCompatActivity() {

    private lateinit var title: TextView
    private lateinit var text: TextView
    private lateinit var btnPrev: TextView
    private lateinit var btnNext: TextView
    private lateinit var indicatorContainer: LinearLayout

    private lateinit var videoView: VideoView
    private lateinit var accordion: LinearLayout

    private val titlePages = listOf(
        "정서란?",
        "정서조절훈련이란?",
        "정서조절훈련에 들어가기 전에"
    )

    private val pages = listOf(
        "정서는 때로 ‘나쁘고 위험한‘ 것으로 느껴질 수 있지만 모든 정서는 부정적이건 긍정적이건 우리 삶에서 적응적인 기능을 한답니다.\n\n예를 들어, 기분이 좋을 때 우리는 마음이 여유로워지고 주변도 잘 돌보며, 신체적으로 면역력이 좋아져요. 부정적 정서도 마찬가지에요. 걱정과 불안은 닥쳐올 어떤 것에 대응하여 준비하게 만들고 분노나 화는 우리 관계를 망치는 것이 아니라 나의 생각이나 감정을 주장할 수 있게 해주기도 해요. 분노가 문제인 것처럼 보이는 것은 그것을 표현하는 방법이 잘못되었기 때문입니다. 그런데 부정적 정서는 부정적이라서, 또 긍정적 정서는 마치 도사리고 있는 위험을 감추는 것처럼 느껴져서 모두 피하고 싶기도 합니다.\n\n즉, 아무 일도 없이 무탈하고 안녕하다는 것이 좋다는 것을 머리로는 알지만, ’내가 이렇게 편안해도(행복해도) 될까?’,  ‘분명히 무슨일이 있을거야‘ 라고 오히려 부정적으로 해석하기도 하지요. 무엇이 문제일까요? 아마도 대부분은 우리가 경험하는 정서를 적절히 조절하지 못하기 때문일 수 있어요.",
        "정서조절은 우리가 정서의 발생과 강도, 표현 및 경험에 영향을 미치는 과정입니다(Gross & Thompson, 2007). 이 과정에서 우리는 종종 부적응적인 정서조절전략을 사용해서 오히려 의도하지 않은 심리적 고통을 겪을 수 있어요.\n\n예를 들어 정서적 느낌을 느끼지 않으려고 회피하거나 혹은 무시해서 오히려 정서경험이 강해지거나 떨쳐내기가 더욱 힘들어지는 것 등입니다. 어떤 것을 회피하려는 것은 타조가 위협을 피해 머리만 숨기는 것과 같이 ‘눈가리고 아웅‘하는 것일 수 있어요.\n\n<감정록> 에서 하고자 하는 것은 우리가 사용하고 있는 부적응적 정서조절 전략을 살펴보고 이를 적응적으로 바꾸고자 훈련을 하는 것입니다.",
        "1. 훈련의 최종목표는 정서를 없애는 것이 아니에요. 정서를 적응적이고 기능적인 수준으로 경험할 수 있도록 조절하면서, 불편한 정서들조차 적응적이고 유용할 수 있음을 경험하는 것입니다.\n\n2. 훈련 과정의 핵심은 ‘비판단적으로 알아차리고’, ‘기록하고’, ‘행동을 수정’하는 것이에요. \n\n3. 정서는 3요소로 이루어져 있어요. 정서와 관련된 ‘몸의 느낌(신체적 요소)’과 ‘생각(인지적 요소)’과 ‘정서에 반응하여 일어나는 정서주도행동(행동적 요소)’입니다. 정서를 경험할 때 이 세 가지의 상호작용을 잘 알아차리는 것이 중요해요. 앞으로 우리는 이 요소들을 골고루 연습해 볼 것입니다. \n\n4. 우선 정서의 느낌을 알아차리고, 정서와 관련된 생각을 알아차려서 수정해보고, 정서에 따른 행동도 살펴서 변화를 시도해 볼 거에요. 매주 순서대로 연습하지만 한 주 훈련이 끝나면 그 부분은 언제든지 다시 돌아가서 살펴볼 수 있습니다."
    )
    private var currentPage = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro_training)

        title = findViewById(R.id.title)
        text = findViewById(R.id.text)
        btnPrev = findViewById(R.id.btnPrev)
        btnNext = findViewById(R.id.btnNext)
        indicatorContainer = findViewById(R.id.indicatorContainer)

        videoView = findViewById(R.id.videoView)
        accordion = findViewById(R.id.accordion)

        // 👇 btnBack 처리 추가
        val btnBack = findViewById<View>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        setupIndicators(pages.size)
        updatePage()

        btnPrev.setOnClickListener {
            if (currentPage > 0) {
                currentPage--
                updatePage()
            }
        }

        btnNext.setOnClickListener {
            if (currentPage < pages.lastIndex) {
                currentPage++
                updatePage()
            } else {
                // 마지막 페이지에서 완료 시 다른 액티비티 이동
                val intent = Intent(this, AllTrainingPageActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        // 아코디언 클릭 리스너
        accordion.setOnClickListener {
            if (text.visibility == View.GONE) {
                text.visibility = View.VISIBLE
            } else {
                text.visibility = View.GONE
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
        title.text = titlePages[currentPage]
        text.text = pages[currentPage]

        // 이전 버튼 상태
        btnPrev.isEnabled = currentPage != 0
        if (currentPage == 0) {
            btnPrev.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D9D9D9"))
        } else {
            btnPrev.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#3CB371"))
        }

        // 다음 버튼 텍스트
        btnNext.text = if (currentPage == pages.lastIndex) "완료 →" else "다음 →"

        // 인디케이터 업데이트
        for (i in 0 until indicatorContainer.childCount) {
            val dot = indicatorContainer.getChildAt(i)
            dot.setBackgroundResource(
                if (i == currentPage) R.drawable.ic_dot_circle_black else R.drawable.ic_dot_circle_gray
            )
        }

        val videoView = findViewById<VideoView>(R.id.videoView)
        val mediaController = MediaController(this)
        mediaController.setAnchorView(videoView)

        val videoName = "intro${currentPage + 1}"
        val videoResId = resources.getIdentifier(videoName, "raw", packageName)
        val uri = Uri.parse("android.resource://$packageName/$videoResId")

        videoView.setMediaController(mediaController)
        videoView.setVideoURI(uri)
        videoView.setOnPreparedListener { it.isLooping = true }
        videoView.start()

    }
}
