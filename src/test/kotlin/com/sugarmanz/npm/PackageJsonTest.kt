package com.sugarmanz.npm

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlin.test.Test
import kotlin.test.assertEquals

internal class PackageJsonTest {

    val basicPackageJsonString = this::class.java.classLoader.getResource("basic.json")!!.readText()
    val Json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    @Test fun `basic package json can be deserialized`() {
        val basicPackageJson: PackageJson = Json.decodeFromString(basicPackageJsonString)
        assertEquals(Name("basic-package"), basicPackageJson.name)
        assertEquals(Semver("1.0.0"), basicPackageJson.version)
        assertEquals("Basic test module", basicPackageJson.description)
        assertEquals("main.js", basicPackageJson.main)
        assertEquals(mapOf("test" to "test"), basicPackageJson.scripts)
        assertEquals(Repository("git+https://github.com/sugarmanz/package-json.git", "git"), basicPackageJson.repository)
        assertEquals(listOf("test", "kotlin", "mpp"), basicPackageJson.keywords)
        assertEquals(People("Jeremiah Zucker", "zucker.jeremiah@gmail.com", "http://jeremiahzucker.com"), basicPackageJson.author)
        assertEquals(License.StringBased("ISC"), basicPackageJson.license)
        assertEquals(Bugs("https://github.com/sugarmanz/package-json/issues"), basicPackageJson.bugs)
        assertEquals("https://github.com/sugarmanz/package-json#readme", basicPackageJson.homepage)
        assertEquals(
            Dependencies(
                listOf(
                    Dependency(Name("auto"), Semver("10.34.1")),
                    Dependency(Name("@auto-it/gradle"), Semver("10.34.1")),
                )
            ),
            basicPackageJson.dependencies
        )

        assertEquals(Json.decodeFromString(basicPackageJsonString), Json.encodeToJsonElement(basicPackageJson))
    }

    @Test fun `basic package json can be serialized`() {
        val packageJson: PackageJson = Json.decodeFromString(basicPackageJsonString)
        val encoded = Json.encodeToJsonElement(packageJson)
        val parsed = Json.parseToJsonElement(basicPackageJsonString)
        assertEquals(parsed, encoded)
    }
}
