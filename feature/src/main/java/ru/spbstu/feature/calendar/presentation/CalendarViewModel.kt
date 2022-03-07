package ru.spbstu.feature.calendar.presentation

import android.annotation.SuppressLint
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.spbstu.common.utils.BundleDataWrapper
import ru.spbstu.feature.domain.model.CalendarDateRange
import ru.spbstu.feature.domain.model.CalendarSelectionMode
import java.time.LocalDate

@SuppressLint("NewApi")
class CalendarViewModel(private val dataWrapper: BundleDataWrapper) :
    ViewModel() {
    private var startDate: LocalDate? = null
    private var endDate: LocalDate? = null
    private val _calendarSelectionState: MutableStateFlow<CalendarSelectionState> =
        MutableStateFlow(CalendarSelectionState.Initial)
    val calendarSelectionState: StateFlow<CalendarSelectionState> get() = _calendarSelectionState
    var calendarMode = CalendarSelectionMode.SINGLE_DAY

    fun selectDate(
        day: LocalDate,
        calendarDateRangeCallback: (calendarDateRange: CalendarDateRange) -> Unit
    ) {
        if (calendarMode == CalendarSelectionMode.SINGLE_DAY) {
            setSingleDayMode(day)
        } else {
            setTwoDaysMode(day)
        }
        setSelectionLayoutState()
        calendarDateRangeCallback(CalendarDateRange(startDate, endDate))
    }

    fun setSingleDayMode(day: LocalDate) {
        startDate = day
    }

    fun setTwoDaysMode(day: LocalDate) {
        if (startDate != null) {
            if (day < startDate || endDate != null) {
                startDate = day
                endDate = null
            } else if (day != startDate) {
                endDate = day
            }
        } else {
            startDate = day
        }
    }

    fun setSelectionLayoutState() {
        _calendarSelectionState.value = CalendarSelectionState.Initial
        _calendarSelectionState.value = when {
            startDate != null && calendarMode == CalendarSelectionMode.SINGLE_DAY -> {
                CalendarSelectionState.SingleDay
            }
            startDate != null && endDate == null -> {
                CalendarSelectionState.StartDate
            }
            startDate != null && endDate != null -> {
                CalendarSelectionState.EndDate
            }
            else -> CalendarSelectionState.NotSelected
        }
    }

    fun setStartDate(date: LocalDate?) {
        startDate = date
    }

    fun setEndDate(date: LocalDate?) {
        endDate = date
    }

    fun getStartDate(): LocalDate? {
        return startDate
    }

    fun getEndDate(): LocalDate? {
        return endDate
    }

    fun selectRange(range: CalendarDateRange) {
        dataWrapper.setBundle(
            bundleOf(
                CalendarFragment.DATA_KEY to range
            )
        )
    }

    sealed class CalendarSelectionState {
        object Initial : CalendarSelectionState()
        object SingleDay : CalendarSelectionState()
        object NotSelected : CalendarSelectionState()
        object StartDate : CalendarSelectionState()
        object EndDate : CalendarSelectionState()
    }
}
