package com.sugarmanz.npm

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

@Serializable
data class Repository(
    val url: String,
    val type: String? = null,
    val directory: String? = null,
) {

//    internal object Serializer : JsonTransformingSerializer<Repository>(serializer()) {
//        override fun transformDeserialize(element: JsonElement) = if (element is JsonPrimitive) buildJsonObject {
//            put("url", element)
//        } else element
//
//        override fun transformSerialize(element: JsonElement) = element.jsonObject.let {
//            if (it["type"] == JsonNull && it["directory"] == JsonNull) it.getOrDefault("url", JsonNull)
//            else it
//        }
//    }

    internal object Serializer : KSerializer<Repository> {

        override val descriptor: SerialDescriptor
            get() = serializer().descriptor

        override fun deserialize(decoder: Decoder): Repository {
            val jsonInput = decoder as? JsonDecoder ?: error("Can be deserialized only by JSON")
            return when (val json = jsonInput.decodeJsonElement()) {
                is JsonPrimitive -> Repository(json.content)
                else -> decoder.json.decodeFromJsonElement(serializer(), json)
            }
        }

        override fun serialize(encoder: Encoder, value: Repository) {
            if (value.type == null && value.directory == null)
                encoder.encodeString(value.url)
            else
                encoder.encodeSerializableValue(serializer(), value)
        }
    }
}
