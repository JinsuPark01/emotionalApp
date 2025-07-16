package com.example.emotionalapp.data

import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity

data class TrainingItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val trainingType: TrainingType, // 이 필드는 유지하거나, 아래 targetActivityClass로 완전히 대체 가능
    val currentProgress: String,
    @ColorRes val backgroundColorResId: Int? = null,
    // 클릭 시 이동할 Activity 클래스를 직접 지정하는 필드 추가
    val targetActivityClass: Class<out AppCompatActivity>? = null
)