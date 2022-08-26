package com.zekierciyas.library

sealed interface SupportedCameraFeatures {
    object ImageCapture: SupportedCameraFeatures
    object ImageAnalysis: SupportedCameraFeatures
    object NotDefined: SupportedCameraFeatures
}