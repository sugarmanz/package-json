package com.sugarmanz.npm

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

// TODO: Need custom serializer for bugs as it can be a simple string for URL
@Serializable
data class People(
    val name: String,
    val email: String? = null,
    @JsonNames("web")
    val url: String? = null,
) {

    // This shows off the json transformation approach, which i currently hate
    internal object MultiFormatSerializer : JsonTransformingSerializer<People>(serializer()) {
        // fair notice.. i was a little past the balmers curve when i wrote this
        private val regex = """([^<>()]*)\b ?(?:<([^<>()]*+)>)? ?(?:\(([^<>()]*+)\))?""".toRegex()

        override fun transformDeserialize(element: JsonElement) = if (element is JsonPrimitive) buildJsonObject {
            val name = element.content
                .let(regex::find)
                ?.let(MatchResult::destructured)
                ?.let { (name, email, url) ->
                    put("name", name.trim())
                    put("email", email.trim())
                    put("url", url.trim())
                }
        } else element

        override fun transformSerialize(element: JsonElement) = element.jsonObject.let {
            JsonPrimitive("${it["name"]!!.jsonPrimitive.content}${if (it["email"] != JsonNull) " <${it["email"]!!.jsonPrimitive.content}>" else ""}${if (it["url"] != JsonNull) " (${it["url"]!!.jsonPrimitive.content})" else ""}")
//            if (it["email"] == JsonNull && it["url"] == JsonNull && it["web"] == JsonNull) it.getOrDefault("name", JsonNull)
//            else it
        }
    }

}
