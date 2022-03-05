package com.example.compose_clean.common

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import com.example.compose_clean.ui.view.states.GenericResult
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.onSuccess
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.coroutines.CoroutineContext


inline fun safeVoidCall(block: () -> Unit) {
    try {
        block.invoke()
    } catch (t: Throwable) {

    }
}

inline fun <reified T> safeCall(block: () -> T?): T? {
    return try {
        block.invoke()
    } catch (t: Throwable) {
        null
    }
}

fun <E> SendChannel<E>.trySendBlockingExt(element: E) {
    trySendBlocking(element).onSuccess {

    }.onFailure {

    }
}

suspend fun <T> safeResultWithContext(
    context: CoroutineContext,
    block: suspend () -> T?
): GenericResult<T> = withContext(context) {
    try {
        val data = block.invoke()
        GenericResult(data, null, true)
    } catch (t: Throwable) {
        GenericResult(null, t, false)
    }
}

suspend fun <T> safeFirebaseResultWithContext(
    context: CoroutineContext,
    block: suspend () -> T?
): GenericResult<T> = withContext(context) {
    var throwable: Throwable? = null
    try {
        val data = block.invoke()
        GenericResult(data, null, true)
    } catch (t: FirebaseAuthUserCollisionException) {
        throwable = Throwable("User already exists")
    } catch (t: FirebaseAuthWeakPasswordException) {
        throwable = Throwable("Password is too weak")
    } catch (t: FirebaseAuthInvalidCredentialsException) {
        throwable = Throwable("Invalid credentials")
    } catch (t: Throwable) {
        throwable = Throwable("An error occurred")
    }
    GenericResult(null, throwable, false)
}

fun <T> safeResult(
    block: () -> T?
): GenericResult<T> {
    return try {
        val data = block.invoke()
        GenericResult(data, null, true)
    } catch (t: Throwable) {
        GenericResult(null, t, false)
    }
}

fun String.capitalizeExt(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}

fun String.containsExt(string: String): Boolean {
    return this.lowercase().contains(string.lowercase())
}

// lazycolumn state saver because rememberSaveable is bugged
fun <T> stateSaver() = Saver<MutableState<T>, Any>(
    save = { state -> state.value ?: "null" },
    restore = { value ->
        @Suppress("UNCHECKED_CAST")
        mutableStateOf((if (value == "null") null else value) as T)
    }
)