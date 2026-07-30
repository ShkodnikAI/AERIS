package com.aeris.domain.model

sealed class SafetyResult {
    object Allowed : SafetyResult()
    data class Blocked(val reasonKey: String) : SafetyResult()
    data class Warning(val messageKey: String) : SafetyResult()
}
