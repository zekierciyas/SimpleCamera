package com.zekierciyas.library

import android.widget.FrameLayout
import androidx.camera.core.CameraSelector
import kotlin.jvm.JvmOverloads
import androidx.camera.view.PreviewView
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.core.ImageAnalysis
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.camera.core.Preview
import androidx.lifecycle.LifecycleOwner
import androidx.core.content.ContextCompat
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors

class CameraXProviderView : FrameLayout {
    private val mSwitchCameraState = CameraSelector.LENS_FACING_FRONT

    constructor(context: Context) : super(context) {
        CameraXProviderView(context, null)
    }

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        mPreviewView = PreviewView(context, attrs, defStyleAttr, defStyleRes)
        addView(mPreviewView)
        //startCamera()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener(
            {
                try {
                    // Camera provider is now guaranteed to be available
                    val cameraProvider = cameraProviderFuture.get()
                    // Set up the view finder use case to display camera preview
                    val preview = Preview.Builder().build()
                    val cameraSelector = CameraSelector.Builder()
                        .requireLensFacing(mSwitchCameraState)
                        .build()

                    // Set up the capture use case to allow users to take photos
                    val imageAnalysis =
                        ImageAnalysis.Builder() //.setTargetResolution(new Size(1280, 720))
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                    imageAnalysis.setAnalyzer({ obj: Runnable -> obj.run() }) { image -> image.close() }


                    // Unbind use cases before rebinding
                    cameraProvider.unbindAll()
                    // Bind use cases to camera
                    // Attach use cases to the camera with the same lifecycle owner
                    cameraProvider.bindToLifecycle(
                        (context as LifecycleOwner),
                        cameraSelector,
                        preview,
                        imageAnalysis
                    )
                    preview.setSurfaceProvider(mPreviewView!!.surfaceProvider)
                } catch (e: InterruptedException) {
                    // Currently no exceptions thrown. cameraProviderFuture.get() should
                    // not block since the listener is being called, so no need to
                    // handle InterruptedException.
                } catch (e: ExecutionException) {
                }
            }, ContextCompat.getMainExecutor(
                context
            )
        )
    }

    companion object {
        private var mPreviewView: PreviewView? = null
    }

    @SuppressLint("UnsafeOptInUsageError")
    fun startCamera(lifecycleOwner: LifecycleOwner) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val cameraExecutor = Executors.newSingleThreadExecutor()

        cameraProviderFuture.addListener({

            // Used to bind the lifecycle of cameras to the lifecycle owner
           val cameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(mPreviewView!!.surfaceProvider)
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            val imageAnalyzer = ImageAnalysis.Builder()
               // .setTargetAspectRatio(aspectRatio(getScreenWidth(), getScreenHeight()))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setImageQueueDepth(1)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor) { imageProxy ->


                        imageProxy.close()
                    }
                }

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    lifecycleOwner, cameraSelector, preview, imageAnalyzer)

            } catch (exc: Exception) {
            }

        }, ContextCompat.getMainExecutor(context))
    }
}