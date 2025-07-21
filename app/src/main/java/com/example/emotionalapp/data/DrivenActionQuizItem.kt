package com.example.emotionalapp.data

import androidx.annotation.StringRes

data class DrivenActionQuizItem(
    val id: Int,
    @StringRes val questionResId: Int,
    val correctAnswer: Boolean, // true: O, false: X
    @StringRes val correctFeedbackResId: Int,
    @StringRes val wrongFeedbackResId: Int
)