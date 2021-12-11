package com.sugarmanz.npm

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

internal class PackageTest {

    @Test fun `name should contain at least one character`() {
        assertFails(Name.ValidationMessages.NameEmpty) {
            Name("").validate()
        }
    }

    @Test fun `name shouldn't have allow more than 214 characters`() {
        assertFails(Name.ValidationMessages.NameTooLong) {
            Name((0..300).fold("") { acc, _ -> acc + "a" }).validate()
        }
    }

    @Test fun `scope should start with an @ symbol`() {
        assertFails(Name.ValidationMessages.ScopePrefix) {
            Name("scope/package").validate()
        }
    }

    @Test fun `valid name with scope doesn't trigger validation`() {
        val raw = "@scope/package"
        val (scope, `package`) = raw.split("/")
        val name = Name(raw)
        name.validate()
        assertEquals(`package`, name.`package`)
        assertEquals(scope, name.scope)
    }

    @Test fun `valid name without scope doesn't trigger validation`() {
        val raw = "package"
        val name = Name(raw)
        name.validate()
        assertEquals(raw, name.`package`)
    }

}