package ru.spbstu.feature.events.presentation.wheelPicker

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HourPickerAdapterTest {
    private lateinit var adapter: HourPickerAdapter

    @BeforeAll
    fun setUp() {
        adapter = HourPickerAdapter()
    }

    @Test
    fun `should return one digit hour equal to positive position with leading zero`() {
        val hour = adapter.getValue(5)
        assertEquals(hour, "05")
    }

    @Test
    fun `should return two digit hour equal to positive position`() {
        val hour = adapter.getValue(15)
        assertEquals(hour, "15")
    }

    @Test
    fun `should return hour of position greater than 24`() {
        val hour = adapter.getValue(38)
        assertEquals(hour, "14")
    }

    @Test
    fun `should return one digit hour by negative position with leading zero`() {
        val hour = adapter.getValue(-17)
        assertEquals(hour, "07")
    }

    @Test
    fun `should return two digit hour by negative position`() {
        val hour = adapter.getValue(-11)
        assertEquals(hour, "13")
    }

    @Test
    fun `should return hour of position lower than -24`() {
        val hour = adapter.getValue(-42)
        assertEquals(hour, "06")
    }
}