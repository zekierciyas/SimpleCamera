package com.zekierciyas.library.observe

import android.graphics.Bitmap
import android.net.Uri
import com.zekierciyas.library.model.SimpleCameraStateModel
import java.lang.Exception

interface Observers {

    interface ImageAnalysis {

        fun result (bitmap: Bitmap?)
    }

    interface ImageCapture {

        fun result (savedUri: Uri?, exception: Exception? = null)
    }

    interface CameraState {

        fun cameraState(simpleCameraState: SimpleCameraStateModel)
    }
}