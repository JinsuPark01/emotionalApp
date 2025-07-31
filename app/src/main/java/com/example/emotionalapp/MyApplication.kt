package com.example.emotionalapp

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.emotionalapp.ui.login_signup.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import androidx.appcompat.app.AppCompatDelegate
import android.os.Handler
import android.os.Looper
import com.example.emotionalapp.util.AppLifecycleObserver

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // 다크모드 비활성화
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // 앱 생명주기 감지하여 백그라운드 전환 시 로그아웃 처리
        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleObserver(this))

    }
    // 앱이 백그라운드로 이동할 때 호출됨
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        Log.d("MyApplication", "앱이 백그라운드로 이동함 - 로그아웃 처리")

        // Firebase 로그아웃
        FirebaseAuth.getInstance().signOut()

        // 딜레이를 줘야 안전하게 ActivityContext 얻을 수 있음
        Handler(Looper.getMainLooper()).post {
            // FLAG_ACTIVITY_NEW_TASK 필요 (ApplicationContext에서 startActivity할 때)
            val intent = Intent(applicationContext, LoginActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            startActivity(intent)
        }
    }
}
