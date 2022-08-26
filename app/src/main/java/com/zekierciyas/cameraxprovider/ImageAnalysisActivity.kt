package com.zekierciyas.cameraxprovider

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.zekierciyas.library.model.SimpleCameraStateModel
import com.zekierciyas.library.observe.Observers
import com.zekierciyas.library.observe.SimpleCameraState
import com.zekierciyas.library.view.SimpleCameraView
import kotlin.random.Random

private const val TAG = "ImageAnalysisActivity"

class ImageAnalysisActivity : AppCompatActivity() {

    private lateinit var cameraXProviderView: SimpleCameraView
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
        override fun cameraState(simpleCameraState: SimpleCameraStateModel) {
            Log.d(TAG, "Camera state is ${simpleCameraState.action.toString()} ")
            when (simpleCameraState.action) {
                is SimpleCameraState.Action.Closed -> {

                }

                is SimpleCameraState.Action.Closing -> {

                }

                is SimpleCameraState.Action.Open -> {

                }

                is SimpleCameraState.Action.Opening -> {

                }

                is SimpleCameraState.Action.Pending -> {

                }
                else -> {

                }
            }

            when (simpleCameraState.error) {
                is SimpleCameraState.Error.ErrorCameraDisabled -> {

                }

                is SimpleCameraState.Error.ErrorCameraFatalError -> {

                }

                is SimpleCameraState.Error.ErrorCameraInUse -> {

                }

                is SimpleCameraState.Error.ErrorDoNotDisturbModelEnabled -> {

                }

                is SimpleCameraState.Error.ErrorMaxCameraInUse -> {

                }

                is SimpleCameraState.Error.ErrorOtherRecoverableError -> {

                }

                is SimpleCameraState.Error.ErrorStreamConfig -> {

                }
                else -> {

                }
            }
        }
    }

}