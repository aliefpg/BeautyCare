package com.example.skindiagnosisai

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var handler: Handler
    private lateinit var logoutRunnable: Runnable
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inisialisasi
        auth = FirebaseAuth.getInstance()
        handler = Handler(Looper.getMainLooper())
        logoutRunnable = Runnable {
            performLogout()
        }
    }

    // Fungsi ini dipanggil setiap kali pengguna menyentuh layar, menekan tombol, dll.
    override fun onUserInteraction() {
        super.onUserInteraction()
        // Setiap ada interaksi, reset timernya kembali ke 1 menit
        resetUserActivityTimer()
    }

    // Fungsi ini dipanggil saat activity ini muncul di layar (misalnya setelah login, atau kembali dari app lain)
    override fun onResume() {
        super.onResume()
        // Mulai timer saat activity aktif
        startUserActivityTimer()
    }

    // Fungsi ini dipanggil saat activity ini tidak lagi di layar (misalnya pengguna menekan tombol home)
    override fun onPause() {
        super.onPause()
        // Hentikan timer agar tidak logout saat aplikasi di background
        handler.removeCallbacks(logoutRunnable)
    }

    private fun resetUserActivityTimer() {
        // Hapus timer yang sedang berjalan (jika ada)
        handler.removeCallbacks(logoutRunnable)
        // Mulai timer baru
        startUserActivityTimer()
    }

    private fun startUserActivityTimer() {
        // Set timer untuk berjalan setelah 1 menit (60,000 milidetik)
        handler.postDelayed(logoutRunnable, 60000)
    }

    private fun performLogout() {
        // Lakukan sign out dari Firebase
        auth.signOut()

        // Buat Intent untuk kembali ke layar pembuka (SplashActivity)
        // Flag ini penting untuk membersihkan semua halaman sebelumnya
        val intent = Intent(this, SplashActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)

        // Tampilkan pesan ke pengguna
        Toast.makeText(this, "Anda telah logout secara otomatis karena tidak aktif", Toast.LENGTH_LONG).show()

        // Tutup MainActivity
        finish()
    }
}