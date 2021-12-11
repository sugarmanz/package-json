package com.sugarmanz.npm

import kotlinx.serialization.Serializable

@Serializable
sealed class Funding {

    // TODO: Need custom serializer for bugs as it can be a simple string for URL
    // TODO: Only serialize as string if type is null!
    @Serializable
    data class Single(
        val url: String,
        val type: String? = null,
    ) : Funding()

    @Serializable
    data class Collection(
        val fundings: List<Single>
    ) : Funding()
}
