package com.example.compose_clean.common

import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.onSuccess
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.withContext
import timber.log.Timber
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
        Timber.d("trySendBlocking success")
    }.onFailure {
        Timber.d("trySendBlocking failure! Throwable: $it")
    }
}

suspend fun <T> safeResultWithContext(
    context: CoroutineContext,
    block: suspend () -> T?
): GenericResult<T> = withContext(context) {
    var exception: CleanComposeException? = null
    var data: T? = null
    try {
        data = block.invoke()
    } catch (e: CleanComposeException) {
        exception = e
    } catch (e: Throwable) {
        exception = CleanComposeException("An error occurred", "An unhandled exception was thrown", e)
    }
    Timber.e(exception)
    GenericResult(data, exception?.userMessage, data != null)
}

suspend fun <T> safeFirebaseResultWithContext(
    context: CoroutineContext,
    block: suspend () -> T?
): GenericResult<T> = withContext(context) {
    var exception: CleanComposeException? = null
    var data: T? = null
    try {
        data = block.invoke()
    } catch (e: FirebaseAuthUserCollisionException) {
        exception = CleanComposeException("User already exists", e)
    } catch (e: FirebaseAuthWeakPasswordException) {
        exception = CleanComposeException("Password is too weak", e)
    } catch (e: FirebaseAuthInvalidCredentialsException) {
        exception = CleanComposeException("Invalid credentials", e)
    } catch (e: CleanComposeException) {
        exception = e
    } catch (e: Throwable) {
        exception = CleanComposeException("An error occurred", "An unhandled firebase exception was thrown", e)
    }
    Timber.e(exception)
    GenericResult(null, exception?.userMessage, data != null)
}

fun <T> safeResult(
    block: () -> T?
): GenericResult<T> {
    return try {
        val data = block.invoke()
        GenericResult(data, null, true)
    } catch (t: Throwable) {
        val exception = CleanComposeException("An error occurred", "An unhandled exception was thrown", t)
        Timber.e(exception)
        GenericResult(null, exception.userMessage, false)
    }
}

fun String.capitalizeExt(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}

fun String.containsExt(string: String): Boolean {
    return this.lowercase().contains(string.lowercase())
}
