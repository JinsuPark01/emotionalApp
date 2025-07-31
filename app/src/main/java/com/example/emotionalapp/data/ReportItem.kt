package com.example.emotionalapp.data

import com.google.firebase.Timestamp

data class ReportItem(
    val date: String,
    val name: String,
    val timeStamp: Timestamp? = null, // Timestamp 필드 추가, null 허용
    val trainingId: String? = null,
)



