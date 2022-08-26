package com.zekierciyas.library.observe


sealed class SimpleCameraState {
    sealed class Action {

        // Ask the user to close other camera apps
        object Pending : Action()

        // Show the Camera UI
        object Opening : Action()

        // Setup Camera resources and begin processing
        object Open : Action()

        // Close camera UI
        object Closing : Action()

        // Free camera resources
        object Closed : Action()

    }

    sealed class Error {
        // Make sure to setup the use cases properly
        object ErrorStreamConfig : Error()

        // Close the camera or ask user to close another camera app that's using the
        // camera
        object ErrorCameraInUse : Error()

        // Close another open camera in the app, or ask the user to close another
        // camera app that's using the camera
        object ErrorMaxCameraInUse : Error()


        object ErrorOtherRecoverableError : Error()

        // Ask the user to enable the device's cameras
        object ErrorCameraDisabled : Error()

        // Ask the user to reboot the device to restore camera function
        object ErrorCameraFatalError : Error()

        // Ask the user to disable the "Do Not Disturb" mode, then reopen the camera
        object ErrorDoNotDisturbModelEnabled : Error()
    }
}




