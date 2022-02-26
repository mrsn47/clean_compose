package com.example.compose_clean.ui.view.states

sealed class Result<out T> {
    class Data<out T>(val data: T? = null) : Result<T>()
    class Error(val errorData: Throwable? = null) : Result<Nothing>() {
        companion object {
            operator fun invoke(msg: String) = Error(Throwable(msg))
        }
    }

}

sealed class ProgressState {
    object LoadingProgressState : ProgressState()
    object LoadedProgressState : ProgressState()
    object EmptyProgressState : ProgressState()
}
// todo: move to common
data class GenericResult<T>(val data: T?, val error: Throwable?, val isSuccess: Boolean) {

    val hasData: Boolean get() = isSuccess && data != null
}