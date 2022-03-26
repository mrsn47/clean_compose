package com.example.compose_clean.common

import kotlinx.coroutines.flow.Flow

class GenericError(
    val error: String
) {
    // todo: timestamp maybe is not needed, check if launchedeffect can check object instead of field
    val timestamp = System.currentTimeMillis()
}

data class GenericResult<T>(val data: T?, val error: String?, val isSuccess: Boolean) {

    val hasData: Boolean get() = isSuccess && data != null
}

sealed class Result<T>(val data: T) {
    class BackendResult<T>(data: T) : Result<T>(data)
    class DatabaseResult<T>(data: T) : Result<T>(data)
}

typealias FlowResult<T> = Flow<Result<T>>
