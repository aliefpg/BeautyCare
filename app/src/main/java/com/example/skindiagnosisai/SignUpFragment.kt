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
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }
    }

    private fun registerUser() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(context, "Semua kolom harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(context, "Password dan konfirmasi password tidak cocok", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Pendaftaran berhasil, dapatkan info pengguna yang baru dibuat
                    val user = auth.currentUser

                    // Kirim email verifikasi ke pengguna tersebut
                    user?.sendEmailVerification()?.addOnCompleteListener { verificationTask ->
                        if (verificationTask.isSuccessful) {
                            Toast.makeText(context, "Akun berhasil dibuat. Silakan cek email Anda untuk verifikasi.", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, "Gagal mengirim email verifikasi.", Toast.LENGTH_SHORT).show()
                        }
                    }

                    // Logout pengguna agar sesi tidak langsung aktif
                    auth.signOut()

                    // Kembali ke halaman login
                    findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
                } else {
                    // Jika pendaftaran gagal
                    Toast.makeText(context, "Pendaftaran Gagal: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}