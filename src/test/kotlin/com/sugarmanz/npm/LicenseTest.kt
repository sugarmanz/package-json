package com.sugarmanz.npm

import kotlinx.serialization.json.*
import kotlin.test.*

internal class LicenseTest {

    @Test fun `single spdx license specified`() {
        val license = License.StringBased("ISC")
        assertEquals("ISC", license.license)
        assertFalse(license.isPrivate)
        assertFalse(license.isFile)
        assertNull(license.filename)
    }

    @Test fun `multiple spdx licences specified`() {
        val license = License.StringBased("(ISC OR MIT)")
        assertEquals("(ISC OR MIT)", license.license)
        assertFalse(license.isPrivate)
        assertFalse(license.isFile)
        assertNull(license.filename)
    }

    @Test fun `local license specified`() {
        val license = License.StringBased("see license in license.md")
        assertTrue(license.isFile)
        assertEquals("license.md", license.filename)
    }

    @Test fun `unlicensed specified`() {
        val license = License.StringBased("unlicensed")
        assertTrue(license.isPrivate)
    }

    @Test fun `decode string based license`() {
        val license: License = Json.decodeFromJsonElement(JsonPrimitive("ISC"))
        assertIs<License.StringBased>(license)
        assertEquals("ISC", license.license)
        assertFalse(license.isPrivate)
        assertFalse(license.isFile)
        assertNull(license.filename)
    }

    @Test fun `encode string based license`() {
        val license = License.StringBased("ISC")
        assertEquals(JsonPrimitive("ISC"), Json.encodeToJsonElement(license))
    }

    @Test fun `decode legacy license`() {
        val license: License = Json.decodeFromJsonElement(
            buildJsonObject {
                put("name", "ISC")
                put("url", "https://license.com/isc")
            }
        )
        assertIs<License.Legacy>(license)
        assertEquals("ISC", license.name)
        assertEquals("https://license.com/isc", license.url)
    }

    @Test fun `encode legacy license`() {
        val license = License.Legacy("ISC", "https://license.com/isc")
        assertEquals(
            buildJsonObject {
                put("name", "ISC")
                put("url", "https://license.com/isc")
            },
            Json.encodeToJsonElement(license)
        )
    }

    @Test fun `decode legacy collection licenses`() {
        val licenses: List<License> = Json.decodeFromJsonElement(
            buildJsonArray {
                add(
                    buildJsonObject {
                        put("name", "ISC")
                        put("url", "https://license.com/isc")
                    }
                )
                add("ISC")
            }
        )
        val (license, license2) = licenses
        assertIs<License.Legacy>(license)
        assertEquals("ISC", license.name)
        assertEquals("https://license.com/isc", license.url)
        assertIs<License.StringBased>(license2)
        assertEquals("ISC2", license2.license)
    }

    // DISABLED: See https://github.com/Kotlin/kotlinx.serialization/issues/1874
    fun `encode legacy collection licenses`() {
        val licenses = listOf(
            License.Legacy("ISC", "https://license.com/isc"),
            License.StringBased("ISC")
        )
        assertEquals(
            buildJsonArray {
                add(
                    buildJsonObject {
                        put("name", "ISC")
                        put("url", "https://license.com/isc")
                    }
                )
                add("ISC")
            },
            Json.encodeToJsonElement(licenses)
        )
    }
}
