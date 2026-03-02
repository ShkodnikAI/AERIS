package com.aeris.util

/**
 * Platform-specific logging.
 * Android: Timber, iOS: os_log
 */
expect fun log(tag: String, message: String)

expect fun logError(tag: String, message: String, throwable: Throwable? = null)

expect fun logDebug(tag: String, message: String)
