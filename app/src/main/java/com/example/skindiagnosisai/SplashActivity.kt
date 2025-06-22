package com.example.skindiagnosisai

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            val sharedPref = getSharedPreferences("onboarding", Context.MODE_PRIVATE)
            val isOnboardingFinished = sharedPref.getBoolean("isFinished", false)

            if (isOnboardingFinished) {
                // Jika sudah selesai onboarding, langsung ke MainActivity
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                // Jika belum, ke OnboardingActivity
                startActivity(Intent(this, OnboardingActivity::class.java))
            }
            finish()
        }, 2500)
    }
}