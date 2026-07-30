package com.aeris.domain.model

data class Badge(
    val id: String,
    val nameKey: String,
    val descriptionKey: String,
    val earnedAt: Long? = null
)
