package com.example.compose_clean.common

import timber.log.Timber

object Validator {

    fun <T> notNull(value: T?, message: String, userMessage: String? = null): T {
        if (value != null) {
            return value
        }
        val exception = CleanComposeException(message = message, userMessage = userMessage)
        Timber.w(exception, message)
        throw exception
    }

    fun notNullOrEmpty(value: String?, message: String, userMessage: String? = null): String {
        if(!value.isNullOrEmpty()) {
            return value
        }
        val exception = CleanComposeException(message = message, userMessage = userMessage)
        Timber.w(exception, message)
        throw exception
    }

}

class CleanComposeException : Exception {
    var userMessage: String? = null
    constructor(userMessage: String? = null, message: String, cause: Throwable) : super(message, cause) {
        this.userMessage = userMessage
    }
    constructor(userMessage: String? = null, message: String) : super(message) {
        this.userMessage = userMessage
    }
    constructor(userMessage: String? = null, cause: Throwable) : super(cause) {
        this.userMessage = userMessage
    }

}