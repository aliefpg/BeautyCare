package com.example.skindiagnosisai

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

// Interface untuk mengirim hasil kembali ke Fragment
interface OnFaceDetectedListener {
    fun onFaceDetected(faceFound: Boolean, faces: List<Face> = emptyList())
}

// Hanya kelas FaceAnalyzer yang ada di file ini
class FaceAnalyzer(
    private val overlay: BoundingBoxOverlay,
    private val listener: OnFaceDetectedListener
) : ImageAnalysis.Analyzer {

    private val options = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .build()

    private val detector = FaceDetection.getClient(options)

    @ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            val cameraSelector = overlay.getTag(R.id.lens_facing) as Int? ?: 0

            detector.process(image)
                .addOnSuccessListener { faces ->
                    if (faces.isNotEmpty()) {
                        listener.onFaceDetected(true, faces)
                        overlay.updateFaces(faces, image.width, image.height, cameraSelector)
                    } else {
                        listener.onFaceDetected(false)
                        overlay.updateFaces(emptyList(), image.width, image.height, cameraSelector)
                    }
                }
                .addOnFailureListener {
                    listener.onFaceDetected(false)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }
}