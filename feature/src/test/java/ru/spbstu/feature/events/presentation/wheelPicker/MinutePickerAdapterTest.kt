package ru.spbstu.feature.events.presentation.wheelPicker

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MinutePickerAdapterTest {

    private lateinit var adapter: MinutePickerAdapter

    @BeforeAll
    fun setUp() {
        adapter = MinutePickerAdapter()
    }

    @Test
    fun `should return one digit minute equal to positive position times 5 with leading zero`() {
        val minute = adapter.getValue(1)
        assertEquals(minute, "05")
    }

    @Test
    fun `should return two digit minute equal to positive position times 5`() {
        val minute = adapter.getValue(9)
        assertEquals(minute, "45")
    }

    @Test
    fun `should return hour of position greater than 12`() {
        val minute = adapter.getValue(20)
        assertEquals(minute, "40")
    }

    @Test
    fun `should return one digit minute by negative position times 5 with leading zero`() {
        val minute = adapter.getValue(-11)
        assertEquals(minute, "05")
    }

    @Test
    fun `should return two digit minute by negative position times 5`() {
        val minute = adapter.getValue(-5)
        assertEquals(minute, "35")
    }

    @Test
    fun `should return minute of position lower than -12`() {
        val minute = adapter.getValue(-42)
        assertEquals(minute, "30")
    }
}