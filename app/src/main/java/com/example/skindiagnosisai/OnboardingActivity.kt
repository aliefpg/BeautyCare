package com.example.skindiagnosisai

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.skindiagnosisai.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var onboardingAdapter: OnboardingAdapter
    // Kita akan menyimpan referensi ke tiga View indikator kita
    private lateinit var indicators: List<View>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Memanggil semua fungsi setup
        setupViewPager()
        setupIndicators()
        setupButton()
    }

    private fun setupViewPager() {
        val onboardingItems = listOf(
            OnboardingItem(
                image = R.drawable.face_recognition,
                title = "Unlock Your Skin's Story with AI Acne Detection!",
                description = "Unlock your skin's secrets with advanced AI that detects acne with precision."
            ),
            OnboardingItem(
                image = R.drawable.skincare_tips,
                title = "Expert Guidance: Get Tailored Articles and Skincare Tips!",
                description = "Get expert articles and tailored skincare advice to make informed choices."
            ),
            OnboardingItem(
                image = R.drawable.daily_skincare,
                title = "Stay on Track: Personalized Daily Skincare Reminders!",
                description = "Stay on track with your skincare routine with personalized reminders."
            )
        )

        onboardingAdapter = OnboardingAdapter(onboardingItems)
        binding.viewPager.adapter = onboardingAdapter

        // Menambahkan listener untuk mendeteksi perubahan halaman/swipe
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                // Setiap kali halaman berubah, update tampilan indikator
                updateIndicators(position)

                // Ubah teks tombol jika berada di halaman terakhir
                if (position == onboardingAdapter.itemCount - 1) {
                    binding.btnNext.text = "GET STARTED"
                } else {
                    binding.btnNext.text = "NEXT"
                }
            }
        })
    }

    // Fungsi untuk menyiapkan daftar indikator kita
    private fun setupIndicators() {
        indicators = listOf(
            binding.indicator1,
            binding.indicator2,
            binding.indicator3
        )
        // Atur kondisi awal (halaman pertama yang aktif)
        updateIndicators(0)
    }

    // Fungsi untuk mengubah warna indikator berdasarkan posisi halaman
    private fun updateIndicators(position: Int) {
        indicators.forEachIndexed { index, indicator ->
            if (index == position) {
                // Jika ini halaman yang aktif, beri background drawable 'active'
                indicator.setBackgroundResource(R.drawable.indicator_active)
            } else {
                // Jika tidak, beri background drawable 'inactive'
                indicator.setBackgroundResource(R.drawable.indicator_inactive)
            }
        }
    }

    // Fungsi untuk mengatur logika tombol
    private fun setupButton() {
        binding.btnNext.setOnClickListener {
            // Jika bukan halaman terakhir, pindah ke halaman selanjutnya
            if (binding.viewPager.currentItem < onboardingAdapter.itemCount - 1) {
                binding.viewPager.currentItem += 1
            } else {
                // Jika sudah di halaman terakhir, selesaikan onboarding
                finishOnboarding()
            }
        }
    }

    // Fungsi untuk menyimpan status bahwa onboarding sudah selesai
    private fun finishOnboarding() {
        val sharedPref = getSharedPreferences("onboarding", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean("isFinished", true)
            apply()
        }
        navigateToMainApp()
    }

    // Fungsi untuk navigasi ke halaman utama aplikasi (login)
    private fun navigateToMainApp() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Tutup activity ini agar tidak bisa kembali ke onboarding
    }
}