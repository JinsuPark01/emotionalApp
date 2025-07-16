package com.example.emotionalapp.data

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity

data class ActionTrainingItem(
    val id: String,
    @DrawableRes val iconResId: Int,
    val title: String,
    val subtitle: String,
    val progressText: String,
    @ColorRes val backgroundColorResId: Int,
    val targetActivityClass: Class<out AppCompatActivity> // 클릭 시 이동할 Activity 클래스
)