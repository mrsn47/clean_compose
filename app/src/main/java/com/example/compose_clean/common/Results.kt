package com.example.compose_clean.common

import kotlinx.coroutines.flow.Flow

class GenericErrorMessage(
    val error: String
)

data class GenericResult<T>(val data: T? = null, val error: String? = null) {
    val hasData: Boolean get() = data != null
    val isSuccess: Boolean get() = error != null
}

sealed class Result<out T : Any> {
    class BackendResult<T : Any>(val data: T) : Result<T>()
    class DatabaseResult<T : Any>(val data: T) : Result<T>()
    class ErrorResult(val error: String) : Result<Nothing>()
}

typealias FlowResult<T> = Flow<Result<T>>
