package com.example.compose_clean.common

import kotlinx.coroutines.flow.Flow

class GenericErrorMessage(
    val error: String
)

data class GenericResult<T>(val data: T?, val error: String?, val isSuccess: Boolean) {

    val hasData: Boolean get() = isSuccess && data != null
}

sealed class Result<out T : Any> {
    class BackendResult<T : Any>(val data: T) : Result<T>()
    class DatabaseResult<T : Any>(val data: T) : Result<T>()
    class ErrorResult(val error: String) : Result<Nothing>()
}

typealias FlowResult<T> = Flow<Result<T>>
