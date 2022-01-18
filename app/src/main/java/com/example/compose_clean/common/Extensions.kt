package com.example.compose_clean.common

import com.example.compose_clean.common.states.GenericResult
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.onSuccess
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.withContext
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

fun <T> safeResult(
    block:  () -> T?
): GenericResult<T>  {
    return try {
        val data = block.invoke()
        GenericResult(data, null, true)
    } catch (t: Throwable) {
        GenericResult(null, t, false)
    }
}