package com.example.emotionalapp.util

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.emotionalapp.ui.login_signup.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class AppLifecycleObserver(private val app: Application) : DefaultLifecycleObserver {

    override fun onStop(owner: LifecycleOwner) {
        // 앱이 백그라운드로 이동했을 때 호출
        // 예: 로그아웃 처리 및 로그인 화면 이동
        logoutAndRedirectToLogin()
    }

    private fun logoutAndRedirectToLogin() {
        // 로그아웃 처리 (SharedPreferences 등)
        val intent = Intent(app, LoginActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        app.startActivity(intent)
    }
}
