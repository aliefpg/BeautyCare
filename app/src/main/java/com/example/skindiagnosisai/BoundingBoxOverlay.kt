package com.example.skindiagnosisai

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.camera.core.CameraSelector
import com.google.mlkit.vision.face.Face

class BoundingBoxOverlay(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val faceRects = mutableListOf<Rect>()
    private val paint = Paint().apply {
        color = Color.parseColor("#F8A096") // Warna peach/salmon
        style = Paint.Style.STROKE
        strokeWidth = 6.0f
    }
    private var imageWidth = 0
    private var imageHeight = 0
    private var lensFacing = CameraSelector.LENS_FACING_FRONT

    fun updateFaces(faces: List<Face>, width: Int, height: Int, lens: Int) {
        faceRects.clear()
        imageWidth = width
        imageHeight = height
        lensFacing = lens

        faces.forEach { face ->
            faceRects.add(adjustBoundingBox(face.boundingBox))
        }
        // Minta view untuk menggambar ulang dirinya sendiri
        postInvalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        faceRects.forEach { rect ->
            canvas.drawRect(rect, paint)
        }
    }

    // Fungsi penting untuk menyesuaikan koordinat dari kamera ke layar
    private fun adjustBoundingBox(box: Rect): Rect {
        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()
        val scaleX = viewWidth / imageHeight
        val scaleY = viewHeight / imageWidth

        val left = box.left * scaleX
        val top = box.top * scaleY
        val right = box.right * scaleX
        val bottom = box.bottom * scaleY

        // Untuk kamera depan, koordinat horizontal perlu dibalik
        if (lensFacing == CameraSelector.LENS_FACING_FRONT) {
            return Rect(
                (viewWidth - right).toInt(),
                top.toInt(),
                (viewWidth - left).toInt(),
                bottom.toInt()
            )
        }
        return Rect(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
    }
}