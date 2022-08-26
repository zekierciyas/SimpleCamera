package com.zekierciyas.library.model

import com.zekierciyas.library.observe.SimpleCameraState

data class SimpleCameraStateModel(
    val action: SimpleCameraState.Action?,
    val error: SimpleCameraState.Error?
)
