package ru.spbstu.feature.calendar.presentation.calendar_binder

import com.kizitonwose.calendarview.utils.yearMonth
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate

class CalendarDayBinderHelperTest {

    @Test
    fun `should return false for startDate and endDate in the same month for InDate`() {
        val inDate = LocalDate.now()
        val startDate = LocalDate.now()
        val endDate = LocalDate.now()
        assertFalse(CalendarDayBinderHelper.isInDateBetween(inDate, startDate, endDate))
    }

    @Test
    fun `should return true for inDate and startDate in the same month`() {
        val inDate = LocalDate.now()
        val startDate = LocalDate.now()
        val endDate = LocalDate.now().plusMonths(1)
        assertTrue(CalendarDayBinderHelper.isInDateBetween(inDate, startDate, endDate))
    }

    @Test
    fun `should return true for inDate between startDate and endDate (different months)`() {
        val inDate = LocalDate.now()
        val startDate = LocalDate.now().minusMonths(1)
        val endDate = LocalDate.now().plusMonths(2)
        assertTrue(CalendarDayBinderHelper.isInDateBetween(inDate, startDate, endDate))
    }

    @Test
    fun `should return false for inDate between startDate and endDate (same month)`() {
        val inDate = LocalDate.now()
        val startDate = LocalDate.now().minusDays(2)
        val endDate = LocalDate.now().plusDays(2)
        assertFalse(CalendarDayBinderHelper.isInDateBetween(inDate, startDate, endDate))
    }

    @Test
    fun `should return false for inDate before startDate and endDate`() {
        val inDate = LocalDate.now()
        val startDate = LocalDate.now().plusMonths(2)
        val endDate = LocalDate.now().plusMonths(3)
        assertFalse(CalendarDayBinderHelper.isInDateBetween(inDate, startDate, endDate))
    }

    @Test
    fun `should return false for inDate after endDate`() {
        val inDate = LocalDate.now()
        val startDate = LocalDate.now().minusMonths(2)
        val endDate = LocalDate.now().minusMonths(1)
        assertFalse(CalendarDayBinderHelper.isInDateBetween(inDate, startDate, endDate))
    }

    @Test
    fun `should return false for startDate being 1st day in inDate month`() {
        val inDate = LocalDate.now()
        val startDate = LocalDate.now().plusMonths(1).yearMonth.atDay(1)
        val endDate = LocalDate.now().plusMonths(2)
        assertFalse(CalendarDayBinderHelper.isInDateBetween(inDate, startDate, endDate))
    }

    @Test
    fun `should return false for startDate and endDate in the same month for outDate`() {
        val outDate = LocalDate.now()
        val startDate = LocalDate.now()
        val endDate = LocalDate.now()
        assertFalse(CalendarDayBinderHelper.isOutDateBetween(outDate, startDate, endDate))
    }

    @Test
    fun `should return true for outDate and endDate in the same month`() {
        val outDate = LocalDate.now()
        val startDate = LocalDate.now().minusMonths(1)
        val endDate = LocalDate.now()
        assertTrue(CalendarDayBinderHelper.isOutDateBetween(outDate, startDate, endDate))
    }

    @Test
    fun `should return true for outDate between startDate and endDate (different months)`() {
        val outDate = LocalDate.now()
        val startDate = LocalDate.now().minusMonths(1)
        val endDate = LocalDate.now().plusMonths(2)
        assertTrue(CalendarDayBinderHelper.isOutDateBetween(outDate, startDate, endDate))
    }

    @Test
    fun `should return false for outDate between startDate and endDate (same month)`() {
        val outDate = LocalDate.now()
        val startDate = LocalDate.now().minusDays(2)
        val endDate = LocalDate.now().plusDays(2)
        assertFalse(CalendarDayBinderHelper.isInDateBetween(outDate, startDate, endDate))
    }

    @Test
    fun `should return false for outDate before startDate and endDate`() {
        val outDate = LocalDate.now()
        val startDate = LocalDate.now().plusMonths(2)
        val endDate = LocalDate.now().plusMonths(3)
        assertFalse(CalendarDayBinderHelper.isOutDateBetween(outDate, startDate, endDate))
    }

    @Test
    fun `should return false for outDate after endDate`() {
        val outDate = LocalDate.now()
        val startDate = LocalDate.now().minusMonths(2)
        val endDate = LocalDate.now().minusMonths(1)
        assertFalse(CalendarDayBinderHelper.isOutDateBetween(outDate, startDate, endDate))
    }

    @Test
    fun `should return false for endDate being last day in outDate month`() {
        val outDate = LocalDate.now()
        val startDate = LocalDate.now().minusMonths(2)
        val endDate = LocalDate.now().minusMonths(1).yearMonth.atEndOfMonth()
        assertFalse(CalendarDayBinderHelper.isOutDateBetween(outDate, startDate, endDate))
    }
}