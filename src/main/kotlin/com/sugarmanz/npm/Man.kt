package com.sugarmanz.npm

import kotlinx.serialization.Serializable

@Serializable
sealed class Man : Validatable {

    @Serializable
    data class Single(
        val path: String,
    ) : Man() {

        override fun validate() {
            require(path.removeSuffix(".gz").last().isDigit()) { ManFileMustEndWithNumber }
        }
    }

    @Serializable
    data class Collection(
        val mans: List<Man>,
    ) : Man() {

        override fun validate() {
            mans.forEach(Man::validate)
        }
    }

    companion object ValidationMessages {
        const val ManFileMustEndWithNumber = "Man files must end with a number, and optionally a .gz suffix if they are compressed. The number dictates which man section the file is installed into."
    }
}
