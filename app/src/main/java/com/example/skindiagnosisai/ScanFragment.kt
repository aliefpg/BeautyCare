package com.example.skindiagnosisai

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
// Import yang benar ada di bawah ini
import com.example.skindiagnosisai.databinding.FragmentScanBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.mlkit.vision.face.Face
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ScanFragment : Fragment(), OnFaceDetectedListener {

    private var _binding: FragmentScanBinding? = null
    private val binding get() = _binding!!
    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null
    private var imageAnalysis: ImageAnalysis? = null
    private lateinit var auth: FirebaseAuth
    private var isCameraActive = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentScanBinding.inflate(inflater, container, false)
        cameraExecutor = Executors.newSingleThreadExecutor()
        auth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnScan.setOnClickListener {
            if (isCameraActive) {
                takePhoto()
            } else {
                checkPermissionsAndShowCamera()
            }
        }
        binding.btnLogout.setOnClickListener {
            auth.signOut()
            Toast.makeText(context, "Anda telah logout", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_scanFragment_to_loginFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        resetToInitialState()
    }

    private fun resetToInitialState() {
        if (_binding != null) {
            binding.ivPlaceholder.visibility = View.VISIBLE
            binding.cameraPreview.visibility = View.GONE
            binding.ivScanFrame.visibility = View.GONE
            binding.overlay.visibility = View.GONE
            binding.btnScan.text = "Scan Wajah"
            binding.btnScan.isEnabled = true
            isCameraActive = false
        }
    }

    private fun checkPermissionsAndShowCamera() {
        if (allPermissionsGranted()) {
            showCamera()
        } else {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
    }

    private fun showCamera() {
        binding.ivPlaceholder.visibility = View.GONE
        binding.cameraPreview.visibility = View.VISIBLE
        binding.ivScanFrame.visibility = View.VISIBLE
        binding.overlay.visibility = View.VISIBLE
        isCameraActive = true
        binding.btnScan.text = "Ambil Gambar"
        binding.btnScan.isEnabled = false
        startCamera()
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also { it.setSurfaceProvider(binding.cameraPreview.surfaceProvider) }
            imageCapture = ImageCapture.Builder().build()
            imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, FaceAnalyzer(binding.overlay, this))
                }
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
            binding.overlay.setTag(R.id.lens_facing, cameraSelector.lensFacing)
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture, imageAnalysis)
            } catch (exc: Exception) {
                Log.e(TAG, "Gagal memulai kamera", exc)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    override fun onFaceDetected(faceFound: Boolean, faces: List<Face>) {
        if (isAdded) {
            activity?.runOnUiThread {
                if (_binding != null) {
                    binding.btnScan.isEnabled = faceFound
                }
            }
        }
    }

    private fun takePhoto() {
        binding.btnScan.isEnabled = false
        val imageCapture = imageCapture ?: return
        val photoFile = File(requireContext().cacheDir, "IMG_${System.currentTimeMillis()}.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Gagal mengambil foto: ${exc.message}", exc)
                    activity?.runOnUiThread { binding.btnScan.isEnabled = true }
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = output.savedUri ?: return
                    if (isAdded) {
                        val skinTypes = listOf("Kering", "Berminyak", "Kombinasi", "Normal", "Sensitif")
                        val randomResult = skinTypes.random()
                        val action = ScanFragmentDirections.actionScanFragmentToResultFragment(randomResult, savedUri.toString())
                        findNavController().navigate(action)
                    }
                }
            })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) { showCamera() } else { Toast.makeText(context, "Izin kamera tidak diberikan.", Toast.LENGTH_SHORT).show() }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
        _binding = null
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val TAG = "ScanFragment"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}