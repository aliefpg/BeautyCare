package com.example.skindiagnosisai

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.skindiagnosisai.databinding.FragmentSignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSignUp.setOnClickListener {
            registerUser()
        }

        binding.tvLoginLink.setOnClickListener {
            // Kembali ke halaman login jika sudah punya akun
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }
    }

    private fun registerUser() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        // Validasi input (tetap sama)
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(context, "Semua kolom harus diisi", Toast.LENGTH_SHORT).show()
            return
        }
        if (password != confirmPassword) {
            Toast.makeText(context, "Password dan konfirmasi password tidak cocok", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnSignUp.isEnabled = false

        // Proses pembuatan user di Firebase
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // --- INI LOGIKA BARU YANG BENAR ---
                    Toast.makeText(context, "Pendaftaran Berhasil! Silakan login.", Toast.LENGTH_SHORT).show()

                    // PENTING: Logout pengguna agar sesi tidak langsung aktif
                    auth.signOut()

                    // DIUBAH: Arahkan ke halaman Login, bukan Scan
                    findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)

                } else {
                    // Logika jika pendaftaran gagal (tetap sama)
                    binding.btnSignUp.isEnabled = true
                    try {
                        throw task.exception!!
                    } catch (e: FirebaseAuthException) {
                        val errorMessage = when (e.errorCode) {
                            "ERROR_EMAIL_ALREADY_IN_USE" -> "Email ini sudah terdaftar."
                            "ERROR_WEAK_PASSWORD" -> "Password terlalu lemah. Gunakan minimal 6 karakter."
                            "ERROR_INVALID_EMAIL" -> "Format email salah."
                            else -> "Pendaftaran gagal."
                        }
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                    } catch (e: Exception) {
                        Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}