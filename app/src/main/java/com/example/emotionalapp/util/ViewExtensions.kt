package com.example.emotionalapp.util

import android.os.SystemClock
import android.view.View

/**
 * 단순 중복 클릭 방지용 확장 함수.
 * 별도의 비동기 제어 없이 클릭 간격만 제한함.
 */
fun View.setSingleListener(
    interval: Int = 600,
    action: (View) -> Unit,
) {
    val oneClick = OnSingleClickListener(interval) {
        action(it)
    }
    setOnClickListener(oneClick)
}


class OnSingleClickListener(
    private var interval: Int = 600,
    private var onSingleClick: (View) -> Unit,
) : View.OnClickListener {

    private var lastClickTime: Long = 0

    override fun onClick(v: View) {
        val elapsedRealtime = SystemClock.elapsedRealtime()
        if ((elapsedRealtime - lastClickTime) < interval) {
            return
        }
        lastClickTime = elapsedRealtime
        onSingleClick(v)
    }
}