package com.sugarmanz.npm

import kotlinx.serialization.Serializable

@Serializable
data class Directories(
    val bin: String? = null,
    val man: String? = null,
    val doc: String? = null,
    val lib: String? = null,
)
