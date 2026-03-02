package com.aeris.di

import com.aeris.ai.BciCalculator
import com.aeris.ai.NsiCalculator
import com.aeris.ai.ProtocolRecommender
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Common Koin module for shared dependencies.
 * Platform-specific bindings provided via expect/actual.
 */
val sharedModule = module {
    // AI Engine
    single { NsiCalculator() }
    single { BciCalculator() }
    single { ProtocolRecommender(get(), get()) }
}

/**
 * Platform-specific module - implemented in androidMain and iosMain.
 */
expect fun platformModule(): Module

/**
 * Get all Koin modules for the application.
 */
fun getSharedModules(): List<Module> = listOf(
    sharedModule,
    platformModule()
)
