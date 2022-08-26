package com.zekierciyas.library

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.widget.FrameLayout
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private const val TAG = "CameraXProviderView"

class CameraXProviderView : ICameraXProvider, FrameLayout {

    /** Selecting Camera from CameraX CameraSelector, Default value as back camera */
    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    /** Selected Camera from user of SDK */
    private var selectedCamera: Int = 0

    /** Preview of CameraX  */
    private var preview: Preview? = null

    /** ImageCapture provider of CameraX*/
    private var imageCapture: ImageCapture? = null

    /** ImageAnalysis provider of CameraX */
    private var imageAnalyzer: ImageAnalysis? = null

    /** Fragment/Activity LifeCycle */
    private var lifecycleOwner: LifecycleOwner? = null

    /** Current used Camera object */
    private var camera: Camera? = null

    /** Observer only for Image Capture feature*/
    private var observerImageCapture: Observers.ImageCapture? = null

    /** Observer only for ImageAnalysis */
    private var observerImageAnalysis: Observers.ImageAnalysis? = null

    /** Common observer of Camera info for all supported camera features */
    private var observerCameraState: Observers.CameraState? = null

    /** Current used camera feature, now we are supporting only one Feature at the same time
     * If other second feature is enabled previous feature will destroyed
     * More than one Feature cannot run at the same time for now */
    private var currentUsedCameraFeature: SupportedCameraFeatures =
        SupportedCameraFeatures.NotDefined


    /** CameraProvider of CameraX is determined with public thread safety mode */
    private val cameraProvider by lazy(LazyThreadSafetyMode.PUBLICATION) {
        ProcessCameraProvider.getInstance(
            context
        ).get()
    }

    /** Blocking camera operations are performed using this executor */
    private lateinit var cameraExecutor: ExecutorService

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
        init(attrs, defStyleAttr)
        mPreviewView = PreviewView(context, attrs, defStyleAttr, defStyleRes)
        addView(mPreviewView)
    }

    private fun init(attrs: AttributeSet?, defStyleAttr: Int) {
        val typedArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CameraXProviderView,
            defStyleAttr,
            defStyleAttr
        )
        cameraExecutor = Executors.newSingleThreadExecutor()
        selectedCamera = typedArray.getInt(R.styleable.CameraXProviderView_cameraID, 0)
    }

    companion object {
        private var mPreviewView: PreviewView? = null
    }

    override fun observeCameraState(observer: Observers.CameraState?) = apply {
        observer?.let {
            observerCameraState = observer
        }
    }


    /** Starts the ImageAnalysis
     *  @param lifecycleOwner  : Owner of the Fragment/Activity life cycle
     *  @param observer : Observer of ImageAnalysis events */
    override fun startImageAnalysis(
        lifecycleOwner: LifecycleOwner,
        observer: Observers.ImageAnalysis
    ) {
        // Re-passing the image capture observer
        observerImageAnalysis = observer
        this.lifecycleOwner = lifecycleOwner
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        // Defining ImageAnalysis as current used Camera Feature
        currentUsedCameraFeature = SupportedCameraFeatures.ImageAnalysis

        cameraProviderFuture.addListener({

            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider = cameraProviderFuture.get()

            // Preview
            preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(mPreviewView!!.surfaceProvider)
                }

            //Selecting camera accordingly xml parser
            cameraSelector = CameraSelector.Builder().requireLensFacing(selectedCamera).build()

            imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setImageQueueDepth(1)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor) { imageProxy ->
                        // Getting bitmap from ImageProxy, returning Bitmap to observer
                        observerImageAnalysis!!.result(imageProxy.toBitmap())
                        // Closing the imageProxy after it used
                        imageProxy.close()
                    }
                }

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner, cameraSelector, preview, imageAnalyzer
                )

                // Observing the camera states of current use case
                observeCameraState(camera!!.cameraInfo)

            } catch (exc: Exception) {
            }

        }, ContextCompat.getMainExecutor(context))
    }

    /** Init of ImageCapture
     *  @param lifecycleOwner :  Owner of the Fragment/Activity life cycle
     *  @param isReady : Unit callback -> return Boolean that represents the ImageCapture ready to capture image */
    override fun imageCapture(
        lifecycleOwner: LifecycleOwner,
        isReady: (Boolean) -> Unit
    ) = apply {

        this.lifecycleOwner = lifecycleOwner
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        // Defining ImageCapture as current used Camera Feature
        currentUsedCameraFeature = SupportedCameraFeatures.ImageCapture

        cameraProviderFuture.addListener({

            // Aspect ration due to this view
            val screenAspectRatio = aspectRatio(this.width, this.height)

            // Camera Provider
            val cameraProvider = cameraProviderFuture.get()

            // CameraSelector
            cameraSelector = CameraSelector.Builder().requireLensFacing(selectedCamera).build()

            preview = Preview.Builder()
                .setTargetAspectRatio(screenAspectRatio)
                .build()
                .also {
                    it.setSurfaceProvider(mPreviewView!!.surfaceProvider)
                }

            // ImageCapture
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                // We request aspect ratio but no resolution to match preview config, but letting
                // CameraX optimize for whatever specific resolution best fits our use cases
                .setTargetAspectRatio(screenAspectRatio)
                // Set initial target rotation, we will have to call this again if rotation changes
                // during the lifecycle of this use case
                //.setTargetRotation(rotation)
                .build()


            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner, cameraSelector, preview, imageCapture
                )

                // Observing the camera states of current use case
                observeCameraState(camera!!.cameraInfo)

                // Returning callback as Ready, it means its ready to take photo
                // With other words, camera is opened photo could be taken
                isReady.invoke(true)

            } catch (exc: Exception) {
                Log.e("TAG", "imageCapture: $exc ")
            }

        }, ContextCompat.getMainExecutor(context))
    }

    /** Takes photo from Camera stream and saves it as Uri file
     *  @param observer : Observer of ImageCapture events */
    override fun takePhoto(observer: Observers.ImageCapture) = apply {

        // Setting the observer of ImageCapture events
        observerImageCapture = observer

        // Get a stable reference of the modifiable image capture use case
        imageCapture?.let { imageCapture ->

            // Create output file to hold the image
            val photoFile: File = File.createTempFile(FILENAME, null, context.cacheDir)

            // Create output options object which contains file + metadata
            val outputOptions = ImageCapture.OutputFileOptions
                .Builder(photoFile)
                .build()

            // Setup image capture listener which is triggered after photo has been taken
            imageCapture.takePicture(
                outputOptions, cameraExecutor, object : ImageCapture.OnImageSavedCallback {
                    override fun onError(exc: ImageCaptureException) {
                        observerImageCapture!!.result(null, exc)
                        Log.e("TAG", "Photo capture failed: ${exc.message}", exc)
                    }

                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                        val savedUri = output.savedUri ?: Uri.fromFile(photoFile)
                        observerImageCapture!!.result(savedUri)
                        Log.d("TAG", "Photo capture succeeded: $savedUri")
                    }
                })
        }
    }

    /** Flip camera according to current camera for only ImageAnalysis
     * If selected camera is front, turning the back, Else turning the front */
    override fun flipCameraWhileImageAnalysis() {
        // Selecting the Camera
        cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else CameraSelector.DEFAULT_BACK_CAMERA
        // Re-binding the ImageAnalysis
        reBindImageAnalysis(lifecycleOwner!!)
    }

    /** Flip camera according to current camera for only ImageCapture
     * If selected camera is front, turning the back, Else turning the front */
    override fun flipCameraWhileImageCapture() {
        // Selecting the Camera
        cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else CameraSelector.DEFAULT_BACK_CAMERA
        // Re-binding the ImageAnalysis
        reBindImageCapture(lifecycleOwner!!)
    }


    /** Re-binding the ImageCapture use cases
     *  While camera selecting, re-binding is required.
     *  @param lifecycleOwner : Owner of the Fragment/Activity lifeCycle */
    private fun reBindImageCapture(lifecycleOwner: LifecycleOwner) {
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner, cameraSelector, preview, imageCapture
        )
    }

    /** Re-binding the ImageCapture use cases
     *  While camera selecting, re-binding is required.
     *  @param lifecycleOwner : Owner of the Fragment/Activity lifeCycle */
    private fun reBindImageAnalysis(lifecycleOwner: LifecycleOwner) {
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner, cameraSelector, preview, imageAnalyzer
        )
    }


    private fun observeCameraState(cameraInfo: CameraInfo) {
        cameraInfo.cameraState.observe(lifecycleOwner!!) { cameraState ->
            Log.i(TAG, "observeCameraState: ${cameraState.type.name}")

            observerCameraState?.let {
                CameraStateMapper.asDataModel(cameraState)
                    ?.let {
                            it1 -> it.cameraState(it1)
                    }
            }
        }
    }
}

