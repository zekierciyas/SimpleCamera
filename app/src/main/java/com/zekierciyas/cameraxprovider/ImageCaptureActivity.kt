package com.zekierciyas.cameraxprovider

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.zekierciyas.library.observe.SimpleCameraState
import com.zekierciyas.library.model.SimpleCameraStateModel
import com.zekierciyas.library.observe.Observers
import com.zekierciyas.library.view.SimpleCameraView
import java.lang.Exception
import kotlin.random.Random

private const val TAG = "ImageCaptureActivity"

class ImageCaptureActivity: AppCompatActivity() {

    private lateinit var cameraXProviderView: SimpleCameraView
    private val permissions = listOf(Manifest.permission.CAMERA)
    private val permissionsRequestCode = Random.nextInt(0, 10000)
    private val buttonImageCapture: ImageView by lazy { findViewById(R.id.image_capture_button) }
    private val buttonFlipCamera: ImageView by lazy { findViewById(R.id.flip_camera_button) }
    private val capturedImagePreview: ImageView by lazy { findViewById(R.id.preview_image_view) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_capture)
        cameraXProviderView = findViewById(R.id.camera_view)

        buttonImageCapture.setOnClickListener {
            cameraXProviderView.takePhoto(observerImageCapture)
        }

        buttonFlipCamera.setOnClickListener {
            cameraXProviderView.flipCamera()
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
                .imageCapture(this) { ready -> {

                }
            }

        }
    }

    private fun hasPermissions(context: Context) = permissions.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    private val observerImageCapture: Observers.ImageCapture = object : Observers.ImageCapture {
        override fun result(savedUri: Uri?, exception: Exception?) {
            if (savedUri != null) {
                Log.i(TAG, "Image capture is succeed")
                runOnUiThread {
                    capturedImagePreview.setImageBitmap(savedUri.toBitmap(this@ImageCaptureActivity))
                }
            }
        }
    }

    private val observerCameraState: Observers.CameraState = object  : Observers.CameraState {
        override fun cameraState(simpleCameraState: SimpleCameraStateModel) {
            Log.d(TAG, "Camera state is ${simpleCameraState.action} ")
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