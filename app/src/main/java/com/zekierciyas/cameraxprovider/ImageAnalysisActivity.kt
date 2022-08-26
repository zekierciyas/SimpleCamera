package com.zekierciyas.cameraxprovider

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Camera
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.zekierciyas.library.*
import kotlin.random.Random

private const val TAG = "ImageAnalysisActivity"

class ImageAnalysisActivity : AppCompatActivity() {

    private lateinit var cameraXProviderView: CameraXProviderView
    private val permissions = listOf(Manifest.permission.CAMERA)
    private val permissionsRequestCode = Random.nextInt(0, 10000)
    private val buttonFlipCamera: ImageView by lazy { findViewById(R.id.flip_camera_button) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_analysis)
        cameraXProviderView = findViewById(R.id.camera_view)

        buttonFlipCamera.setOnClickListener {
            cameraXProviderView.flipCameraWhileImageAnalysis()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!hasPermissions(this)) {
            ActivityCompat.requestPermissions(
                this, permissions.toTypedArray(), permissionsRequestCode)
        } else {
            cameraXProviderView
                .observeCameraState(observerCameraState)
                .startImageAnalysis(this, observerImageAnalysis)

        }
    }

    private fun hasPermissions(context: Context) = permissions.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    private val observerImageAnalysis: Observers.ImageAnalysis = object : Observers.ImageAnalysis {
        override fun result(bitmap: Bitmap?) {
            bitmap?.let {
                Log.i(TAG,"Bitmap received while analysi " +
                        "\n byte count: ${it.byteCount} " +
                        "\n : density ${it.density}")
            }
        }
    }

    private val observerCameraState: Observers.CameraState = object  : Observers.CameraState {
        override fun cameraState(cameraXState: CameraXStateModel) {
            Log.d(TAG, "Camera state is ${cameraXState.action.toString()} ")
            when (cameraXState.action) {
                is CameraXState.Action.CLOSED -> {

                }

                is CameraXState.Action.CLOSING -> {

                }

                is CameraXState.Action.OPEN -> {

                }

                is CameraXState.Action.OPENING -> {

                }

                is CameraXState.Action.PENDING -> {

                }
                else -> {

                }
            }

            when (cameraXState.error) {
                is CameraXState.Error.ERROR_CAMERA_DISABLED -> {

                }

                is CameraXState.Error.ERROR_CAMERA_FATAL_ERROR -> {

                }

                is CameraXState.Error.ERROR_CAMERA_IN_USE -> {

                }

                is CameraXState.Error.ERROR_DO_NOT_DISTURB_MODE_ENABLED -> {

                }

                is CameraXState.Error.ERROR_MAX_CAMERAS_IN_USE -> {

                }

                is CameraXState.Error.ERROR_OTHER_RECOVERABLE_ERROR -> {

                }

                is CameraXState.Error.ERROR_STREAM_CONFIG -> {

                }
                else -> {

                }
            }
        }
    }

}