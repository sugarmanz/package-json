package com.sugarmanz.npm

import kotlinx.serialization.Serializable

// TODO: Need custom serializer for bugs as it can be a simple string for URL
@Serializable
data class Bugs(
    val url: String? = null,
    val email: String? = null,
) {

    companion object {
        val Empty = Bugs()
    }
}
