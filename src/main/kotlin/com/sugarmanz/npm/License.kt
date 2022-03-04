package com.sugarmanz.npm

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(License.Serializer::class)
sealed class License {

    @Serializable(StringBased.Serializer::class)
    data class StringBased(val license: String) : License(), Validatable {

        companion object {
            private val fileRegex = Regex("SEE LICENSE IN (.*)", RegexOption.IGNORE_CASE)
            private val privateRegex = Regex("UNLICENSED", RegexOption.IGNORE_CASE)
        }

        // TODO: If i make this truly MPP, for JS, I can just rely on those libraries, like SPDX or NPM semver
        override fun validate() {
        }

        val isFile by lazy {
            license.matches(fileRegex)
        }

        val filename by lazy {
            fileRegex.find(license)?.groupValues?.get(1)
        }

        val isPrivate by lazy {
            license.matches(privateRegex)
        }

        object Serializer : KSerializer<StringBased> {

            override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("com.sugarmanz.npm.License.StringBased", PrimitiveKind.STRING)

            override fun deserialize(decoder: Decoder): StringBased = decoder.decodeString().let(::StringBased)

            override fun serialize(encoder: Encoder, value: StringBased) = encoder.encodeString(value.license)
        }
    }

    @Serializable
    data class Legacy(val name: String, val url: String) : License()

    object Serializer : KSerializer<License> {

        // TODO: This might break if used within a collection
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("com.sugarmanz.npm.Bugs", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): License = try {
            decoder.decodeString().let(::StringBased)
        } catch (exception: Exception) {
            decoder.decodeSerializableValue(Legacy.serializer())
        }

        override fun serialize(encoder: Encoder, value: License) = when (value) {
            is StringBased -> encoder.encodeSerializableValue(StringBased.serializer(), value)
            is Legacy -> encoder.encodeSerializableValue(Legacy.serializer(), value)
        }
    }
}
