package com.zekierciyas.library.feature

sealed interface SupportedCameraFeatures {
    object ImageCapture: SupportedCameraFeatures
    object ImageAnalysis: SupportedCameraFeatures
    object NotDefined: SupportedCameraFeatures
}