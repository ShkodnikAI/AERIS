package com.aeris.di

import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Android-specific Koin module bindings.
 */
actual fun platformModule(): Module = module {
    // Android-specific dependencies will be provided by androidApp module
    // This module serves as a bridge for shared components
}
