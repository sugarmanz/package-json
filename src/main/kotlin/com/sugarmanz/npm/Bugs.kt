package com.sugarmanz.npm

import kotlinx.serialization.*
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.nullable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(Bugs.Serializer::class)
sealed class Bugs {

    @Serializable(Simple.Serializer::class)
    data class Simple(val url: String?) : Bugs() {

        object Serializer : KSerializer<Bugs.Simple> {

            override val descriptor: SerialDescriptor = String.serializer().descriptor.nullable

            override fun serialize(encoder: Encoder, value: Simple) = value.url?.let(encoder::encodeString)
                ?: encoder.encodeNull()

            override fun deserialize(decoder: Decoder) = decoder.decodeString()
                .let(::Simple)
        }
    }

    // TODO: Should I make a custom serializer that would encode null if both members were null?
    // TODO: Potentially override equals to account for [Simple]s if email is null
    @Serializable
    data class Boxed(
        val url: String? = null,
        val email: String? = null,
    ) : Bugs()

    object Serializer : KSerializer<Bugs> {

        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("com.sugarmanz.npm.Bugs", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): Bugs = try {
            decoder.decodeString().let(::Simple)
        } catch (exception: SerializationException) {
            decoder.decodeSerializableValue(Boxed.serializer())
        }

        override fun serialize(encoder: Encoder, value: Bugs) = when (value) {
            is Simple -> encoder.encodeSerializableValue(Simple.serializer(), value)
            is Boxed -> encoder.encodeSerializableValue(Boxed.serializer(), value)
        }
    }

    companion object {
        operator fun invoke(url: String?, email: String? = null): Bugs = email?.let {
            Boxed(url, it)
        } ?: Simple(url)
    }
}

fun Bugs.withEmail(email: String): Bugs.Boxed = when (this) {
    is Bugs.Simple -> Bugs.Boxed(url, email)
    is Bugs.Boxed -> copy(email = email)
}

fun Bugs.withUrl(url: String): Bugs = when (this) {
    is Bugs.Simple -> Bugs.Simple(url)
    is Bugs.Boxed -> copy(url = url)
}
