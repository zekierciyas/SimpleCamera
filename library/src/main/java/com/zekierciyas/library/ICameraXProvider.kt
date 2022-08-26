package com.zekierciyas.library

import android.graphics.Camera
import android.net.Uri
import androidx.lifecycle.LifecycleOwner
import java.lang.NullPointerException

interface ICameraXProvider {

    fun observeCameraState(observer: Observers.CameraState?) : CameraXProviderView

    fun startImageAnalysis(lifecycleOwner: LifecycleOwner, observer : Observers.ImageAnalysis)

    fun imageCapture(lifecycleOwner: LifecycleOwner,
                     isReady: (Boolean) -> Unit ): CameraXProviderView

    fun takePhoto(observer : Observers.ImageCapture): CameraXProviderView

    @Throws(NullPointerException::class)
    fun flipCameraWhileImageAnalysis ()

    @Throws(NullPointerException::class)
    fun flipCameraWhileImageCapture()

    /**
     *  TODO: Adding VideoCapture function
     *        Adding takeBitmap function: Takes bitmap from PreviewView of CameraX
     *        Adding flashTorch, zoom features
     */
}