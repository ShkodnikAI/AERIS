package com.aeris.util

import platform.Foundation.NSLog

actual fun log(tag: String, message: String) {
    NSLog("[$tag] $message")
}

actual fun logError(tag: String, message: String, throwable: Throwable?) {
    val errorMsg = if (throwable != null) {
        "$message: ${throwable.message}"
    } else {
        message
    }
    NSLog("[ERROR][$tag] $errorMsg")
}

actual fun logDebug(tag: String, message: String) {
    NSLog("[DEBUG][$tag] $message")
}
