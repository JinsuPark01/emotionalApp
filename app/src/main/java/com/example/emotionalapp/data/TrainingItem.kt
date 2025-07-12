package com.example.emotionalapp.data

import androidx.annotation.ColorRes

// 전체 훈련 어댑팅을 위한 데이터 클래스

// 어떤 액티비티로 이동할지 식별하기 위한 Enum (선택 사항이지만 권장)
enum class TrainingType {
                        INTRO, // 정서란
    EMOTION_TRAINING, // 정서인식 훈련
    BODY_TRAINING, // 신체자각 훈련
    MIND_WATCHING_TRAINING, // 인지재구성 훈련
    EXPRESSION_ACTION_TRAINING,// 정서표현 및 행동 훈련
    // 주당 1회 상태 체크 구현 필요
    }

data class TrainingItem(
    val id: String,
    val title: String,
    val subtitle: String, // XML에 tv_training_subtitle이 있으므로 추가
    val trainingType: TrainingType, // 각 아이템이 어떤 종류의 훈련인지 또는 어떤 페이지로 가야하는지 식별
    @ColorRes val backgroundColorResId: Int? = null, // 배경색 리소스 ID (선택 사항)
    // val iconResId: Int? = null // XML에는 없지만, 필요하다면 아이콘 리소스 ID도 추가 가능
)
