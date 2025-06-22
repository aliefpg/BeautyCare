package com.example.skindiagnosisai

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.skindiagnosisai.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        // Cek jika pengguna sudah login DAN emailnya sudah terverifikasi
        val currentUser = auth.currentUser
        if (currentUser != null && currentUser.isEmailVerified) {
            // Jika ya, langsung pindah ke ScanFragment
            findNavController().navigate(R.id.action_loginFragment_to_scanFragment)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Listener untuk Tombol Login
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // --- BLOK JIKA LOGIN BERHASIL ---
                            val user = auth.currentUser
                            if (user != null && user.isEmailVerified) {
                                // Jika email SUDAH diverifikasi, masuk ke aplikasi
                                Toast.makeText(context, "Login Berhasil!", Toast.LENGTH_SHORT).show()
                                findNavController().navigate(R.id.action_loginFragment_to_scanFragment)
                            } else {
                                // Jika email BELUM diverifikasi, beri pesan dan logout
                                Toast.makeText(context, "Login Gagal. Silakan verifikasi email Anda terlebih dahulu.", Toast.LENGTH_LONG).show()
                                auth.signOut()
                            }
                        } else {
                            // --- BLOK JIKA LOGIN GAGAL (KARENA PASSWORD SALAH, DLL) ---
                            try {
                                throw task.exception!!
                            } catch (e: FirebaseAuthException) {
                                val errorMessage = when (e.errorCode) {
                                    // GABUNGKAN SEMUA KASUS LOGIN GAGAL MENJADI SATU PESAN
                                    "ERROR_USER_NOT_FOUND",
                                    "ERROR_WRONG_PASSWORD",
                                    "INVALID_LOGIN_CREDENTIALS" -> "Email atau Password yang Anda masukkan salah."

                                    "ERROR_INVALID_EMAIL" -> "Format email yang Anda masukkan salah."
                                    "ERROR_USER_DISABLED" -> "Akun ini telah dinonaktifkan."
                                    else -> "Terjadi kesalahan. Coba beberapa saat lagi."
                                }
                                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                            } catch (e: Exception) {
                                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                            }
                        }
                    }
            } else {
                Toast.makeText(context, "Email dan Password tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
        } // <-- Penutup untuk btnLogin.setOnClickListener

        // Listener untuk Teks Sign Up
        binding.tvSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}