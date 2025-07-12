package com.example.emotionalapp.data

import androidx.annotation.ColorRes

// 전체 훈련 어댑팅을 위한 데이터 클래스
data class TrainingItem(
    val id: String,
    val title: String,
    val subtitle: String, // XML에 tv_training_subtitle이 있으므로 추가
    val trainingType: TrainingType, // 각 아이템이 어떤 종류의 훈련인지 또는 어떤 페이지로 가야하는지 식별
    @ColorRes val backgroundColorResId: Int? = null
)
