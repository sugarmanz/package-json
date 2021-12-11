package com.sugarmanz.npm

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class Dependencies(
    val dependencies: List<Dependency>
) {

    // TODO: Add apis for adding, ensuring that the names of each dependency are unique

    companion object {
        val Empty = Dependencies(emptyList())
    }
}

@Serializable
data class Dependency(
    val name: Name,
    val version: Semver,
)
