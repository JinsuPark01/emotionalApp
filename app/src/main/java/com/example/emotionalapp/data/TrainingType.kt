package com.example.emotionalapp.data

// 어떤 액티비티로 이동할지 식별하기 위한 Enum (선택 사항이지만 권장)
enum class TrainingType {
    INTRO, // 정서란
    EMOTION_TRAINING, // 정서인식 훈련
    BODY_TRAINING, // 신체자각 훈련
    MIND_WATCHING_TRAINING, // 인지재구성 훈련
    EXPRESSION_ACTION_TRAINING,// 정서표현 및 행동 훈련
    // 주당 1회 상태 체크 구현 필요
}