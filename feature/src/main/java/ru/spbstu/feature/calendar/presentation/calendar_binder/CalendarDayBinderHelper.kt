package ru.spbstu.feature.calendar.presentation.calendar_binder

import android.annotation.SuppressLint
import com.kizitonwose.calendarview.utils.yearMonth
import java.time.LocalDate

@SuppressLint("NewApi")
object CalendarDayBinderHelper {
    fun isInDateBetween(
        inDate: LocalDate,
        startDate: LocalDate,
        endDate: LocalDate
    ): Boolean {
        if (startDate.yearMonth == endDate.yearMonth) return false
        if (inDate.yearMonth == startDate.yearMonth) return true
        val firstDateInThisMonth = inDate.plusMonths(1).yearMonth.atDay(1)
        return firstDateInThisMonth >= startDate && firstDateInThisMonth <= endDate && startDate != firstDateInThisMonth
    }

    fun isOutDateBetween(
        outDate: LocalDate,
        startDate: LocalDate,
        endDate: LocalDate
    ): Boolean {
        if (startDate.yearMonth == endDate.yearMonth) return false
        if (outDate.yearMonth == endDate.yearMonth) return true
        val lastDateInThisMonth = outDate.minusMonths(1).yearMonth.atEndOfMonth()
        return lastDateInThisMonth >= startDate && lastDateInThisMonth <= endDate && endDate != lastDateInThisMonth
    }
}