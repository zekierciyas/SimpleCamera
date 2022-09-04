# Simple Camera

Simple Camera is a library that allows you to use cameras in a simplified, easy, fast and effective way. It contains CameraX as an internal library. It simplifies the camera features used by CameraX and provides ease of use with a single library. As updates come on the CameraX side, the library will try to stay up to date. Missing features will be added soon. Like video capturing.

### To be added/Added Features

- [x] Image Analysis
    - [x] Flip Camera
- [x] Image Capture
    - [x] Flip Camera
- [ ] Video Capture
    - [ ] Flip Camera


## Initial Installation
### Gradle
Add below codes to your **root** `build.gradle` file (not your module build.gradle file).
```gradle
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```
And add a dependency code to your **module**'s `build.gradle` file.
```gradle
dependencies {
	        implementation 'com.github.zekierciyas:SimpleCamera:Tag'
	}
```

## Usage
Add following XML namespace inside your XML layout file.

```gradle
xmlns:app="http://schemas.android.com/apk/res-auto"
```

### SimpleCameraView
We can use `SimpleCameraView` with customized attributes.
```xml
   <com.zekierciyas.library.view.SimpleCameraView
        android:id="@+id/camera_view"
        app:cameraID="1"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
```

### Attribute descriptions
We can customize the palette image and selector or various options using the below attributes.
```gradle
        app:cameraID="0" // Default Front Side Lens 
        app:cameraID="1" // Default Back Side Lens 
```

### Observers
Observers can be used as separate objects as in the example. You can access it from the interface object of `Observers`.

* `Observers.CameraState` is a common observation that provides general observation of the camera. It can also be used in other camera features.
```kotlin
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
```

* `Observers.ImageAnalysis` can only be used during the image analysis feature. It is an observer pattern that returns analysis results. You can find the simple usage separated from here.

```kotlin
  private val observerImageAnalysis: Observers.ImageAnalysis = object : Observers.ImageAnalysis {
    override fun result(bitmap: Bitmap?) {
        bitmap?.let {
            Log.i(TAG,"Bitmap received while analysi " +
                    "\n byte count: ${it.byteCount} " +
                    "\n : density ${it.density}")
        }
    }
}
```

* `Observers.ImageCapture` can only be used during the Image Capture feature. It is an observer pattern that returns captured image results. You can find the simple usage separated from here.

```kotlin
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
```




### Usage
You can find View definitions and Builder configuration for each feature separately.
#### Usage of Image Analysis
```kotlin
 val simpleCameraView: SimpleCameraView = findViewById(R.id.camera_view)

// Must be called after getting camera permissions
simpleCameraView.observeCameraState(observerCameraState)
    .startImageAnalysis(this, observerImageAnalysis)
```

#### Usage of Image Capture
```kotlin
 val simpleCameraView: SimpleCameraView = findViewById(R.id.camera_view)

// Must be called after getting camera permissions
simpleCameraView.observeCameraState(observerCameraState)
    .imageCapture(this) {
            ready -> if(ready) {
        // Photo could be taken
        // You can set visibility of capture button here if you had one !
        // Or you can take photo directly when it's ready
        cameraXProviderView.takePhoto(observerImageCapture)
    }
    }
```

#### Common Feature
```kotlin
// Flipping camera 
simpleCameraView.flipCamera()
```
# Used Internal Libraries
- [CameraX](https://developer.android.com/jetpack/androidx/releases/camera)
# License
```xml
Copyright 2022 github/zekierciyas (Zeki Erciyas)

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
```
