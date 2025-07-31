package com.example.emotionalapp

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.appcompat.app.AppCompatDelegate
import com.example.emotionalapp.util.AppLifecycleObserver

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // 다크모드 비활성화
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // 앱 생명주기 감지하여 백그라운드 전환 시 로그아웃 처리
        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleObserver(this))

    }
}
