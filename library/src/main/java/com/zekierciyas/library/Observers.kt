package com.zekierciyas.library

import android.graphics.Bitmap
import android.net.Uri
import androidx.camera.core.ImageProxy
import java.lang.Exception

interface Observers {

    interface ImageAnalysis {

        fun result (bitmap: Bitmap?)
    }

    interface ImageCapture {

        fun result (savedUri: Uri?, exception: Exception? = null)
    }

    interface CameraState {

        fun cameraState(cameraXState: CameraXStateModel)
    }
}