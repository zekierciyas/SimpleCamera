package com.zekierciyas.library

import androidx.camera.core.CameraState

object CameraStateMapper {

    fun asDataModel(cameraState: CameraState): CameraXStateModel? {
            cameraState.error?.let { error ->
                return when (error.code) {
                    // Open errors
                    CameraState.ERROR_STREAM_CONFIG -> {
                        // Make sure to setup the use cases properly
                        CameraXStateModel(null,CameraXState.Error.ERROR_STREAM_CONFIG)
                    }
                    // Opening errors
                    CameraState.ERROR_CAMERA_IN_USE -> {
                        // Close the camera or ask user to close another camera app that's using the
                        // camera
                        CameraXStateModel(null,CameraXState.Error.ERROR_CAMERA_IN_USE)
                    }
                    CameraState.ERROR_MAX_CAMERAS_IN_USE -> {
                        // Close another open camera in the app, or ask the user to close another
                        // camera app that's using the camera
                        CameraXStateModel(null,CameraXState.Error.ERROR_MAX_CAMERAS_IN_USE)

                    }
                    CameraState.ERROR_OTHER_RECOVERABLE_ERROR -> {
                        CameraXStateModel(null,CameraXState.Error.ERROR_OTHER_RECOVERABLE_ERROR)

                    }
                    // Closing errors
                    CameraState.ERROR_CAMERA_DISABLED -> {
                        // Ask the user to enable the device's cameras
                        CameraXStateModel(null,CameraXState.Error.ERROR_CAMERA_DISABLED)
                    }
                    CameraState.ERROR_CAMERA_FATAL_ERROR -> {
                        // Ask the user to reboot the device to restore camera function
                        CameraXStateModel(null,CameraXState.Error.ERROR_CAMERA_FATAL_ERROR)
                    }
                    // Closed errors
                    CameraState.ERROR_DO_NOT_DISTURB_MODE_ENABLED -> {
                        // Ask the user to disable the "Do Not Disturb" mode, then reopen the camera
                        CameraXStateModel(null,CameraXState.Error.ERROR_DO_NOT_DISTURB_MODE_ENABLED)
                    }
                    else -> {
                        null
                    }
                }
            }

            return when (cameraState.type) {
            CameraState.Type.PENDING_OPEN -> {
                CameraXStateModel(CameraXState.Action.PENDING, null)
            }
            CameraState.Type.OPENING -> {
                // Show the Camera UI
                CameraXStateModel(CameraXState.Action.OPENING, null)

            }
            CameraState.Type.OPEN -> {
                // Setup Camera resources and begin processing
                CameraXStateModel(CameraXState.Action.OPEN, null)
            }
            CameraState.Type.CLOSING -> {
                // Close camera UI
                CameraXStateModel(CameraXState.Action.CLOSING, null)

            }
            CameraState.Type.CLOSED -> {
                // Free camera resources
                CameraXStateModel(CameraXState.Action.CLOSED, null)

            }
            else -> {
                null
            }
        }
    }
}