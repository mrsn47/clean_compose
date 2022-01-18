package com.example.compose_clean.common.states

sealed class ResultState {
    sealed class Result<out T> : ResultState(){
        class Data<out T>(val data: T? = null) : Result<T>()
        class Error(val errorData: Throwable? = null) : Result<Nothing>() {
            companion object {
                operator fun invoke(msg: String) = Error(Throwable(msg))
            }
        }

    }

    sealed class ProgressState : ResultState() {
        object LoadingProgressState : ProgressState()
        object LoadedProgressState : ProgressState()
        object EmptyProgressState : ProgressState()
    }

}

data class GenericResult<T>(val data: T?, val error: Throwable?, val isSuccess: Boolean) {

    val hasData: Boolean get() = isSuccess && data != null
}