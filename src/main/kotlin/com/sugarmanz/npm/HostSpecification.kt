package com.sugarmanz.npm

import kotlinx.serialization.Serializable

interface HostSpecification {
    val value: List<String>
}

val HostSpecification.allowed: List<String> get() = value - blocked
val HostSpecification.blocked: List<String> get() = value.filter { it.startsWith("!") }

fun HostSpecification.allowed(name: String) = allowed.contains(name)
fun HostSpecification.blocked(name: String) = blocked.contains(name)

@Serializable
@JvmInline
value class OperatingSystems(
    override val value: List<String>,
) : HostSpecification {

    companion object {
        val Empty = OperatingSystems(emptyList())
    }
}

@Serializable
@JvmInline
value class CPUs(
    override val value: List<String>,
) : HostSpecification {

    companion object {
        val Empty = CPUs(emptyList())
    }
}
