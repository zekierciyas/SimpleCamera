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
import com.zekierciyas.library.CameraXProviderView
import com.zekierciyas.library.Observers
import java.lang.Exception
import kotlin.random.Random

private const val TAG = "ImageCaptureActivity"

class ImageCaptureActivity: AppCompatActivity() {

    private lateinit var cameraXProviderView: CameraXProviderView
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
            cameraXProviderView.flipCameraWhileImageCapture()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!hasPermissions(this)) {
            ActivityCompat.requestPermissions(
                this, permissions.toTypedArray(), permissionsRequestCode)
        } else {
            cameraXProviderView.imageCapture(this) {
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
}