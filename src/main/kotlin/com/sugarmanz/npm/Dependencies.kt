package com.sugarmanz.npm

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(Dependencies.Serializer::class)
@JvmInline
value class Dependencies(
    val dependencies: List<Dependency>
) {

    // TODO: Add apis for adding, ensuring that the names of each dependency are unique

    companion object {
        val Empty = Dependencies(emptyList())
    }

    object Serializer : KSerializer<Dependencies> {

        private val structureSerializer = MapSerializer(Name.serializer(), Semver.serializer())

        override val descriptor: SerialDescriptor = structureSerializer.descriptor

        override fun deserialize(decoder: Decoder): Dependencies = decoder
            .decodeSerializableValue(structureSerializer)
            .entries
            .map { (name, version) -> Dependency(name, version) }
            .let(::Dependencies)

        override fun serialize(encoder: Encoder, value: Dependencies) = value.dependencies
            .associate { it.name to it.version }
            .let { encoder.encodeSerializableValue(structureSerializer, it) }
    }
}

@Serializable
data class Dependency(
    val name: Name,
    val version: Semver,
)
