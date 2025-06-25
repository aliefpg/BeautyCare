package com.example.skindiagnosisai

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var handler: Handler
    private lateinit var logoutRunnable: Runnable
    private lateinit var auth: FirebaseAuth

    // Variabel untuk mengontrol kapan timer harus aktif
    private var isUserLoggedIn = false
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inisialisasi
        auth = FirebaseAuth.getInstance()
        handler = Handler(Looper.getMainLooper())
        logoutRunnable = Runnable {
            performLogout("Anda telah logout secara otomatis karena tidak aktif")
        }

        // --- LOGIKA BARU MENGGUNAKAN NAV CONTROLLER ---
        // Cari NavController
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Tambahkan listener yang akan berjalan setiap kali pindah halaman
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Cek apakah halaman tujuan adalah halaman setelah login
            if (destination.id == R.id.scanFragment || destination.id == R.id.resultFragment) {
                // Jika ya, aktifkan timer
                isUserLoggedIn = true
                startUserActivityTimer()
                Log.d("TIMER_DEBUG", "Memasuki halaman aman. Timer dimulai.")
            } else {
                // Jika tidak (misal: di halaman login/signup), hentikan timer
                isUserLoggedIn = false
                stopUserActivityTimer()
                Log.d("TIMER_DEBUG", "Memasuki halaman publik. Timer dihentikan.")
            }
        }
    }

    // Fungsi ini dipanggil setiap kali pengguna menyentuh layar
    override fun onUserInteraction() {
        super.onUserInteraction()
        // Hanya reset timer jika pengguna sudah dalam keadaan login
        if (isUserLoggedIn) {
            resetUserActivityTimer()
        }
    }

    // Fungsi onResume dan onPause tidak lagi kita butuhkan untuk mengatur timer
    // karena sudah ditangani oleh NavController

    // Logout saat aplikasi ditutup (ini tetap berfungsi seperti sebelumnya)
    override fun onStop() {
        super.onStop()
        if (auth.currentUser != null) {
            auth.signOut()
            Log.d("SESSION_STOP", "Pengguna logout karena aplikasi di-stop.")
        }
    }

    private fun startUserActivityTimer() {
        // Hapus dulu timer lama untuk memastikan tidak ada duplikat
        handler.removeCallbacks(logoutRunnable)
        // Set timer untuk berjalan setelah 1 menit (60,000 milidetik)
        handler.postDelayed(logoutRunnable, 60000)
        Log.d("TIMER_DEBUG", "Timer 60 detik dimulai.")
    }

    private fun stopUserActivityTimer() {
        handler.removeCallbacks(logoutRunnable)
        Log.d("TIMER_DEBUG", "Timer dihentikan.")
    }

    private fun resetUserActivityTimer() {
        Log.d("TIMER_DEBUG", "Interaksi pengguna terdeteksi. Timer direset.")
        startUserActivityTimer()
    }

    private fun performLogout(message: String) {
        if (isFinishing) return

        if (auth.currentUser != null) {
            auth.signOut()
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }

        val intent = Intent(this, SplashActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}