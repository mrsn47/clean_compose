package com.example.compose_clean.ui.view.states

class GenericError(
    val error: String
) {
    val timestamp = System.currentTimeMillis()
}

data class GenericResult<T>(val data: T?, val error: Throwable?, val isSuccess: Boolean) {

    val hasData: Boolean get() = isSuccess && data != null
}