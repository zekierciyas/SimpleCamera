package com.zekierciyas.library.utility

import androidx.camera.core.CameraState
import com.zekierciyas.library.model.SimpleCameraStateModel
import com.zekierciyas.library.observe.SimpleCameraState

object SimpleCameraStateMapper {

    fun asDataModel(cameraState: CameraState): SimpleCameraStateModel? {
            cameraState.error?.let { error ->
                return when (error.code) {
                    // Open errors
                    CameraState.ERROR_STREAM_CONFIG -> {
                        // Make sure to setup the use cases properly
                        SimpleCameraStateModel(null, SimpleCameraState.Error.ErrorStreamConfig)
                    }
                    // Opening errors
                    CameraState.ERROR_CAMERA_IN_USE -> {
                        // Close the camera or ask user to close another camera app that's using the
                        // camera
                        SimpleCameraStateModel(null, SimpleCameraState.Error.ErrorCameraInUse)
                    }
                    CameraState.ERROR_MAX_CAMERAS_IN_USE -> {
                        // Close another open camera in the app, or ask the user to close another
                        // camera app that's using the camera
                        SimpleCameraStateModel(null, SimpleCameraState.Error.ErrorMaxCameraInUse)

                    }
                    CameraState.ERROR_OTHER_RECOVERABLE_ERROR -> {
                        SimpleCameraStateModel(null, SimpleCameraState.Error.ErrorOtherRecoverableError)

                    }
                    // Closing errors
                    CameraState.ERROR_CAMERA_DISABLED -> {
                        // Ask the user to enable the device's cameras
                        SimpleCameraStateModel(null, SimpleCameraState.Error.ErrorCameraDisabled)
                    }
                    CameraState.ERROR_CAMERA_FATAL_ERROR -> {
                        // Ask the user to reboot the device to restore camera function
                        SimpleCameraStateModel(null, SimpleCameraState.Error.ErrorCameraFatalError)
                    }
                    // Closed errors
                    CameraState.ERROR_DO_NOT_DISTURB_MODE_ENABLED -> {
                        // Ask the user to disable the "Do Not Disturb" mode, then reopen the camera
                        SimpleCameraStateModel(null, SimpleCameraState.Error.ErrorDoNotDisturbModelEnabled)
                    }
                    else -> {
                        null
                    }
                }
            }

            return when (cameraState.type) {
            CameraState.Type.PENDING_OPEN -> {
                SimpleCameraStateModel(SimpleCameraState.Action.Pending, null)
            }
            CameraState.Type.OPENING -> {
                // Show the Camera UI
                SimpleCameraStateModel(SimpleCameraState.Action.Opening, null)

            }
            CameraState.Type.OPEN -> {
                // Setup Camera resources and begin processing
                SimpleCameraStateModel(SimpleCameraState.Action.Open, null)
            }
            CameraState.Type.CLOSING -> {
                // Close camera UI
                SimpleCameraStateModel(SimpleCameraState.Action.Closing, null)

            }
            CameraState.Type.CLOSED -> {
                // Free camera resources
                SimpleCameraStateModel(SimpleCameraState.Action.Closed, null)

            }
            else -> {
                null
            }
        }
    }
}