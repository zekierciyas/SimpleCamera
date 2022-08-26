package com.zekierciyas.library


sealed class CameraXState {
    sealed class Action {

        // Ask the user to close other camera apps
        object PENDING : Action()

        // Show the Camera UI
        object OPENING : Action()

        // Setup Camera resources and begin processing
        object OPEN : Action()

        // Close camera UI
        object CLOSING : Action()

        // Free camera resources
        object CLOSED : Action()

    }

    sealed class Error {
        // Make sure to setup the use cases properly
        object ERROR_STREAM_CONFIG : Error()

        // Close the camera or ask user to close another camera app that's using the
        // camera
        object ERROR_CAMERA_IN_USE : Error()

        // Close another open camera in the app, or ask the user to close another
        // camera app that's using the camera
        object ERROR_MAX_CAMERAS_IN_USE : Error()


        object ERROR_OTHER_RECOVERABLE_ERROR : Error()

        // Ask the user to enable the device's cameras
        object ERROR_CAMERA_DISABLED : Error()

        // Ask the user to reboot the device to restore camera function
        object ERROR_CAMERA_FATAL_ERROR : Error()

        // Ask the user to disable the "Do Not Disturb" mode, then reopen the camera
        object ERROR_DO_NOT_DISTURB_MODE_ENABLED : Error()
    }
}

data class CameraXStateModel(
    val action: CameraXState.Action?,
    val error: CameraXState.Error?
)


