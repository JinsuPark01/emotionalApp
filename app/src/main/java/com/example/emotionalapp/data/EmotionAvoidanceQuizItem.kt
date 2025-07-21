package com.example.emotionalapp.data

import androidx.annotation.StringRes

data class EmotionAvoidanceQuizItem(
    val id: Int,
    @StringRes val questionResId: Int,
    val correctAnswer: Boolean,
    @StringRes val correctFeedbackResId: Int,
    @StringRes val wrongFeedbackResId: Int
)