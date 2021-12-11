package com.sugarmanz.npm

import kotlinx.serialization.Serializable

@Serializable
@JvmInline value class Semver(private val version: String) : Validatable {

    override fun validate() {
        // TODO: Ensure it's parseable by NPM node-semver
    }

    private val parts: Triple<String?, String?, String?> get() = (version.split(".") + listOf(null, null, null))
        .let { (major, minor, patch) -> Triple(major, minor, patch) }

    val major: String? get() = parts.first
    val minor: String? get() = parts.second
    val patch: String? get() = parts.third
}
