package com.lens.taskmanager.helper

import android.annotation.SuppressLint
import android.content.Context
import android.util.Size
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

class FaceRecognitionHelper(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner
) {

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    fun startFaceRecognition(onSuccess: () -> Unit, onFailure: () -> Unit) {
        cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider, onSuccess, onFailure)
        }, ContextCompat.getMainExecutor(context))
    }

    @SuppressLint("UnsafeExperimentalUsageError", "RestrictedApi")
    private fun bindPreview(cameraProvider: ProcessCameraProvider, onSuccess: () -> Unit, onFailure: () -> Unit) {
        val preview = Preview.Builder().build()
        val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

        val faceDetectorOptions = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .build()

        val faceDetector = FaceDetection.getClient(faceDetectorOptions)

        val analysisUseCase = ImageAnalysis.Builder()
            .setTargetResolution(Size(1280, 720))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        analysisUseCase.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                faceDetector.process(image)
                    .addOnSuccessListener { faces ->
                        if (faces.isNotEmpty()) {
                            onSuccess()
                        } else {
                            onFailure()
                        }
                        imageProxy.close()
                    }
                    .addOnFailureListener {
                        onFailure()
                        imageProxy.close()
                    }
            }
        }

        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, analysisUseCase)
    }
}
