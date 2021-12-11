package com.sugarmanz.npm

import kotlinx.serialization.Serializable

// TODO: Need to write serializer for string based (needs access to the `Package` instance) and map based
@Serializable
sealed class Bin {

    @Serializable
    data class Single(
        val name: String,
        val executable: String,
    ) : Bin()

    @Serializable
    data class Collection(
        val bins: List<Single>,
    ) : Bin()
}
