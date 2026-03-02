package com.aeris.util

import timber.log.Timber

actual fun log(tag: String, message: String) {
    Timber.tag(tag).i(message)
}

actual fun logError(tag: String, message: String, throwable: Throwable?) {
    if (throwable != null) {
        Timber.tag(tag).e(throwable, message)
    } else {
        Timber.tag(tag).e(message)
    }
}

actual fun logDebug(tag: String, message: String) {
    Timber.tag(tag).d(message)
}
