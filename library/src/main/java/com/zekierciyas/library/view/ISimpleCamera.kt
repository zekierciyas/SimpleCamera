package com.zekierciyas.library.view

import androidx.lifecycle.LifecycleOwner
import com.zekierciyas.library.observe.Observers
import java.lang.NullPointerException

interface ISimpleCamera {

    fun observeCameraState(observer: Observers.CameraState?) : SimpleCameraView

    fun startImageAnalysis(lifecycleOwner: LifecycleOwner, observer : Observers.ImageAnalysis)

    fun imageCapture(lifecycleOwner: LifecycleOwner,
                     isReady: (Boolean) -> Unit ): SimpleCameraView

    fun takePhoto(observer : Observers.ImageCapture): SimpleCameraView

    fun flipCamera()

    /**
     *  TODO: Adding VideoCapture function
     *        Adding flashTorch, zoom features
     */
}