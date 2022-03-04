package ru.spbstu.feature

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SimpleTest {
    @Test
    fun `Simple test 2 + 2 return 4`() {
        val expected = 4
        val actual = 2 + 2

        assertEquals(expected, actual)
    }
}
