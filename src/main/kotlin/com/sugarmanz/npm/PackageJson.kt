package com.sugarmanz.npm

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import kotlinx.serialization.json.JsonObject

/** [Serializable] representation of [NPMs package.json definition](https://docs.npmjs.com/cli/v8/configuring-npm/package-json) */
@Serializable
data class PackageJson(
    val name: Name? = null,
    val version: Semver? = null,
    val description: String? = null,
    val keywords: List<String> = emptyList(),
    val homepage: String? = null,
    val bugs: Bugs? = null,
    val license: License? = null,
    val licenses: List<License>? = null,
    @Serializable(with = People.MultiFormatSerializer::class)
    val author: People? = null,
    val funding: Funding? = null,
    val files: List<String> = listOf("*"),
    val main: String = "index.js",
    val browser: String? = "",
    val bin: Bin? = null,
    val man: Man? = null,
    val directories: Directories = Directories(),
    @Serializable(with = Repository.Serializer::class)
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

    // TODO: Add additional catchall to preserve fields that aren't standard?
) : Validatable {

    override fun validate() {
        name?.validate()
        version?.validate()
    }

    companion object {
        private val EmptyJsonObject = JsonObject(emptyMap())
    }
}
