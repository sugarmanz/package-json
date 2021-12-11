package com.sugarmanz.npm

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class Engines(
    val value: Map<String, Semver>,
) {
    val node: Semver? get() = value["node"]
    val npm: Semver? get() = value["npm"]

    operator fun get(engine: String): Semver? = value[engine]

    companion object {
        val Empty = Engines(emptyMap())
    }
}
