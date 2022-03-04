package com.sugarmanz.npm

import kotlinx.serialization.json.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

private const val URL = "https://bugs.com"
private const val EMAIL = "email@bugs.com"

internal class BugsTest {

    @Test fun `read bugs from simple string`() {
        val bugs: Bugs = Json.decodeFromJsonElement(JsonPrimitive(URL))
        assertIs<Bugs.Simple>(bugs)
        assertEquals(URL, bugs.url)
    }

    @Test fun `write bugs to simple string`() {
        val bugs: Bugs = Bugs.Simple(URL)
        assertEquals(JsonPrimitive(URL), Json.encodeToJsonElement(bugs))
    }

    @Test fun `simple to boxed with email`() {
        val bugs: Bugs = Bugs.Simple(URL).withEmail(EMAIL)
        assertIs<Bugs.Boxed>(bugs)
        assertEquals(URL, bugs.url)
        assertEquals(EMAIL, bugs.email)
    }

    @Test fun `write boxed to object`() {
        val bugs: Bugs = Bugs.Boxed(URL, EMAIL)
        assertEquals(
            buildJsonObject {
                put("url", URL)
                put("email", EMAIL)
            },
            Json.encodeToJsonElement(bugs)
        )
    }

    @Test fun `with url helper`() {
        val URL2 = "https://bugs2.com"
        assertEquals(Bugs.Simple(URL2), Bugs.Simple(URL).withUrl(URL2))
        assertEquals(Bugs.Boxed(URL2), Bugs.Boxed(URL).withUrl(URL2))
    }
}
