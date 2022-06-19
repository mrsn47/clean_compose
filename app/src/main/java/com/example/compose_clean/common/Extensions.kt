package com.example.compose_clean.common

import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.onSuccess
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.withContext
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
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
    block: suspend (CoroutineScope) -> T?
): GenericResult<T> = withContext(context) {
    var exception: CCException? = null
    var data: T? = null
    try {
        data = block.invoke(this)
    } catch (e: Exception) {
        exception = convertToCCException(e)
    }
    Timber.e(exception)
    GenericResult(data, exception?.userMessage, data != null)
}

fun <T> safeResult(
    block: () -> T?
): GenericResult<T> {
    var exception: CCException? = null
    var data: T? = null
    try {
        data = block.invoke()
    } catch (e: Exception) {
        exception = convertToCCException(e)
    }
    Timber.e(exception)
    return GenericResult(data, exception?.userMessage, data != null)
}

fun convertToCCException(throwable: Throwable): CCException {
    return when(throwable) {
        is CCException -> throwable
        is FirebaseException -> convertFirebaseExceptionToCCException(throwable)
        else -> CCException("An error occurred", "An unhandled exception was thrown", throwable)
    }
}

fun convertFirebaseExceptionToCCException(e: FirebaseException): CCException {
    // todo: should consider error code
    return when(e) {
        // auth
        is FirebaseAuthUserCollisionException -> CCException("User already exists", e)
        is FirebaseAuthWeakPasswordException -> CCException("Password is too weak", e)
        is FirebaseAuthInvalidCredentialsException -> CCException("Invalid credentials", e)

        // network
        is FirebaseNetworkException -> CCException("Could not reach the server. Check your internet connection", e)

        else -> CCException("An error occurred", "An unhandled firebase exception was thrown", e)
    }
}

fun String.capitalizeExt(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}

fun String.containsExt(string: String): Boolean {
    return this.lowercase().contains(string.lowercase())
}

fun String.toZoneOffset(): ZoneOffset = safeCall {
    val zoneId = ZoneId.of(this)
    val instant = Instant.now()
    return zoneId.rules.getOffset(instant)
} ?: ZoneOffset.ofHours(0)

fun ZonedDateTime.formatForUi(): String {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    return this.withZoneSameInstant(ZoneId.systemDefault()).format(formatter)
}