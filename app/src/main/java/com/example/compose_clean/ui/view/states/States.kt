package com.example.compose_clean.ui.view.states

class GenericError(
    val error: String
) {
    // todo: timestamp maybe is not needed, check if launchedeffect can check object instead of field
    val timestamp = System.currentTimeMillis()
}

data class GenericResult<T>(val data: T?, val error: Throwable?, val isSuccess: Boolean) {

    val hasData: Boolean get() = isSuccess && data != null
}