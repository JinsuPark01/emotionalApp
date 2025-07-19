package com.example.emotionalapp.ui.body

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.R

class BodyTrainingPracticeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_body_practice)

        // 뒤로 가기 버튼
        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }

        // Intent로부터 ID/제목 받기
        val trainingId    = intent.getStringExtra("TRAINING_ID") ?: ""
        val trainingTitle = intent.getStringExtra("TRAINING_TITLE") ?: "연습"

        findViewById<TextView>(R.id.tv_practice_title).text = trainingTitle

        // ID에 따라 서로 다른 연습 내용을 분기
        val detailText = when (trainingId) {
            "bt_detail_002" -> "DAY 1 연습:\n\n머리부터 발끝까지 몸 전체의 감각을 스캔하며, 긴장된 부위를 풀어주세요.\n- 6분 타이머\n- 자연음 배경\n\n[참고] 눈을 감고, 호흡에 집중합니다."
            "bt_detail_003" -> "DAY 2 연습:\n\n호흡, 심장박동, 발바닥 등 특정 감각 하나에만 집중합니다.\n- 5분 타이머\n\n[TIP] 잡념이 떠오르면 ‘감각으로 돌아오세요’ 라고 내게 속삭여보세요."
            "bt_detail_004" -> "DAY 3 연습:\n\n최근 떠오른 감정을 떠올리고, 그 감정이 몸 어디에 나타나는지 관찰합니다.\n- 7분 타이머\n\n“감정은 생각보다 먼저 몸에 나타납니다.”"
            "bt_detail_005" -> "DAY 4 연습:\n\n머리→발끝으로 주의를 이동하며, 감각이 없더라도 판단 말고 관찰하세요.\n- 6분 타이머\n\n“잘하려고 애쓰지 않아도 괜찮아요.”"
            "bt_detail_006" -> "DAY 5 연습:\n\n바디스캔 중 변화된 감각을 느껴봅니다.\n- 6분 타이머\n\n“느끼려는 태도 자체가 중요합니다.”"
            "bt_detail_007" -> "DAY 6 연습:\n\n한 입의 음식을 천천히 먹으며 오감과 감정을 인식합니다.\n- 준비물: 건포도 또는 견과류 한 입\n\n“천천히….”"
            "bt_detail_008" -> "DAY 7 연습:\n\n감정이 먹기 행동에 어떤 영향을 주는지 알아차립니다.\n- 상상 연습: 먹기 전/후 감정 변화 관찰\n\n“판단 없이 느껴보세요.”"
            else -> "준비 중인 연습입니다."
        }

        findViewById<TextView>(R.id.tv_practice_detail).text = detailText
    }
}
