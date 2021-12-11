package com.sugarmanz.npm

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject

interface Validatable {
    fun validate()
}

val EmptyJsonObject = JsonObject(emptyMap())

// TODO: Extract into separate package
/** [Serializable] representation of [NPMs package.json definition](https://docs.npmjs.com/cli/v8/configuring-npm/package-json) */
@Serializable
data class PackageJson(
    val name: Name? = null,
    val version: Semver? = null,
    val description: String? = null,
    val keywords: List<String> = emptyList(),
    val homepage: String? = null,
    val bugs: Bugs = Bugs.Empty,
    val license: String? = null,
    val author: People? = null,
    val funding: Funding? = null,
    val files: List<String> = listOf("*"),
    val main: String = "index.js",
    val browser: String? = "",
    val bin: Bin? = null,
    val man: Man? = null,
    val directories: Directories = Directories(),
    val repository: Repository? = null,
    val scripts: Map<String, String> = emptyMap(),
    val config: JsonObject = EmptyJsonObject,
    val dependencies: Dependencies = Dependencies.Empty,
    val devDependencies: Dependencies = Dependencies.Empty,
    val peerDependencies: Dependencies = Dependencies.Empty,
    val peerDependenciesMeta: JsonObject = EmptyJsonObject,
    @JsonNames("bundleDependencies")
    val bundledDependencies: List<Name> = emptyList(),
    val optionalDependencies: Dependencies = Dependencies.Empty,
    val engines: Engines = Engines.Empty,
    val os: OperatingSystems = OperatingSystems.Empty,
    val cpu: CPUs = CPUs.Empty,
    val private: Boolean = false,
    val publishConfig: JsonObject = EmptyJsonObject,
    val workspaces: List<String> = emptyList(),
    val contributors: List<People> = emptyList(),
    val maintainers: List<People> = emptyList(),
) : Validatable {

    override fun validate() {
        name?.validate()
        version?.validate()
    }

}

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

    internal companion object ValidationMessages {
        const val NameEmpty = "Name must not be empty"
        const val ScopePrefix = "Scopes must be preceded by an @ symbol."
        const val NameTooLong = "The name must be less than or equal to 214 characters. This includes the scope for scoped packages. See https://docs.npmjs.com/cli/v8/configuring-npm/package-json#name for more details."
    }

}

@Serializable
@JvmInline value class Semver(private val version: String) : Validatable {

    override fun validate() {
        // TODO: Ensure it's parseable by NPM node-semver
    }

    private val parts: Triple<String?, String?, String?> get() = (version.split(".") + listOf(null, null, null))
        .let { (major, minor, patch) -> Triple(major, minor, patch) }

    val major: String? get() = parts.first
    val minor: String? get() = parts.second
    val patch: String? get() = parts.third

}

// TODO: Need custom serializer for bugs as it can be a simple string for URL
@Serializable
data class Bugs(
    val url: String? = null,
    val email: String? = null,
) {

    companion object {
        val Empty = Bugs()
    }

}

// TODO: Need custom serializer for bugs as it can be a simple string for URL
@Serializable
data class People(
    val name: String,
    val email: String? = null,
    @JsonNames("web")
    val url: String? = null,
)


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


@Serializable
data class Directories(
    val bin: String? = null,
    val man: String? = null,
    val doc: String? = null,
    val lib: String? = null,
)


@Serializable
sealed class Repository {

    @Serializable
    data class Protocol(
        val url: String,
    ) : Repository()

    @Serializable
    data class Http(
        val url: String,
        val type: String? = null,
        val directory: String? = null,
    ) : Repository()

}


@Serializable
data class Dependency(
    val name: Name,
    val version: Semver,
)

@Serializable
@JvmInline
value class Dependencies(
    val dependencies: List<Dependencies>
) {

    // TODO: Add apis for adding, ensuring that the names of each dependency are unique

    companion object {
        val Empty = Dependencies(emptyList())
    }

}

@Serializable
@JvmInline
value class Engines(
    val value: Map<String, Semver>,
) {
    val node: Semver? get() = value["node"]
    val npm: Semver? get() = value["npm"]

    operator fun get(engine: String): Semver? = value[engine]

    companion object {
        val Empty = Engines(emptyMap())
    }
}


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
): HostSpecification {

    companion object {
        val Empty = OperatingSystems(emptyList())
    }
}

@Serializable
@JvmInline
value class CPUs(
    override val value: List<String>,
): HostSpecification {

    companion object {
        val Empty = CPUs(emptyList())
    }
}
