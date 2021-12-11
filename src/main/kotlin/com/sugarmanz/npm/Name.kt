package com.sugarmanz.npm

import kotlinx.serialization.Serializable

@Serializable
@JvmInline value class Name(private val value: String) : Validatable {

    override fun validate() {
        // TODO: Not sure these should belong in init or some validate method. My thinking is that someone might potentially read an invalid package.json but still need to operate on it. Maybe validate during serialization?
        require(value.length <= 214) { NameTooLong }
        require(scope?.startsWith("@") ?: true) { ScopePrefix }
        require(`package`.isNotEmpty()) { NameEmpty }
    }

    private val parts: Pair<String?, String?> get() = (value.split("/") + listOf(null, null))
        .let { (scope, `package`) -> scope to `package` }

    val scope: String? get() = parts.let { (maybeScope, maybePackage) ->
        maybePackage?.let { maybeScope }
    }

    val `package`: String get() = parts.let { (maybeScope, maybePackage) ->
        maybePackage ?: requireNotNull(maybeScope)
    }

//    override fun equals(other: Any?): Boolean = when (other) {
//        is String -> value == other
//        else -> super.equals(other)
//    }

    internal companion object ValidationMessages {
        const val NameEmpty = "Name must not be empty"
        const val ScopePrefix = "Scopes must be preceded by an @ symbol."
        const val NameTooLong = "The name must be less than or equal to 214 characters. This includes the scope for scoped packages. See https://docs.npmjs.com/cli/v8/configuring-npm/package-json#name for more details."
    }
}
