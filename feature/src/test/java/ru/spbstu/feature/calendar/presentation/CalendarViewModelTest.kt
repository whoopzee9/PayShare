package ru.spbstu.feature.calendar.presentation

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.kotlin.mock
import ru.spbstu.common.utils.BundleDataWrapper
import ru.spbstu.feature.domain.model.CalendarSelectionMode
import java.time.LocalDate

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CalendarViewModelTest {
    private val dataWrapper = mock<BundleDataWrapper>()
    private lateinit var viewModel: CalendarViewModel

    @BeforeAll
    fun setUp() {
        viewModel = CalendarViewModel(dataWrapper)
    }

    @Test
    fun `should set single day mode`() {
        val date = LocalDate.now()
        viewModel.setSingleDayMode(date)
        assertEquals(date, viewModel.getStartDate())
    }

    @Test
    fun `should set startDate in two days mode`() {
        viewModel.setStartDate(null)
        viewModel.setEndDate(null)
        val date = LocalDate.now()
        viewModel.setTwoDaysMode(date)
        assertEquals(date, viewModel.getStartDate())
        assertEquals(null, viewModel.getEndDate())
    }

    @Test
    fun `should update startDate in two days mode`() {
        viewModel.setStartDate(LocalDate.now())
        viewModel.setEndDate(null)
        val date = LocalDate.now().minusWeeks(1)
        viewModel.setTwoDaysMode(date)
        assertEquals(date, viewModel.getStartDate())
        assertEquals(null, viewModel.getEndDate())
    }

    @Test
    fun `should set endDate in two days mode`() {
        val startDate = LocalDate.now()
        viewModel.setStartDate(startDate)
        viewModel.setEndDate(null)
        val date = LocalDate.now().plusDays(2)
        viewModel.setTwoDaysMode(date)
        assertEquals(startDate, viewModel.getStartDate())
        assertEquals(date, viewModel.getEndDate())
    }

    @Test
    fun `should update startDate and reset endDate in two days mode`() {
        val startDate = LocalDate.now()
        val endDate = LocalDate.now().plusDays(2)
        viewModel.setStartDate(startDate)
        viewModel.setEndDate(endDate)
        viewModel.setTwoDaysMode(startDate)
        assertEquals(startDate, viewModel.getStartDate())
        assertEquals(null, viewModel.getEndDate())
    }

    @Test
    fun `should not update anything in two days mode`() {
        val startDate = LocalDate.now()
        viewModel.setStartDate(startDate)
        viewModel.setEndDate(null)
        viewModel.setTwoDaysMode(startDate)
        assertEquals(startDate, viewModel.getStartDate())
        assertEquals(null, viewModel.getEndDate())
    }

    //tests for layout state

    @Test
    fun `should set SingleDay calendar state`() {
        viewModel.setStartDate(LocalDate.now())
        viewModel.setEndDate(null)
        viewModel.calendarMode = CalendarSelectionMode.SINGLE_DAY
        viewModel.setSelectionLayoutState()
        assertEquals(
            CalendarViewModel.CalendarSelectionState.SingleDay,
            viewModel.calendarSelectionState.value
        )
    }

    @Test
    fun `should set SingleDay calendar state 2`() {
        viewModel.setStartDate(LocalDate.now())
        viewModel.setEndDate(LocalDate.now().plusMonths(1))
        viewModel.calendarMode = CalendarSelectionMode.SINGLE_DAY
        viewModel.setSelectionLayoutState()
        assertEquals(
            CalendarViewModel.CalendarSelectionState.SingleDay,
            viewModel.calendarSelectionState.value
        )
    }

    @Test
    fun `should set startDate calendar state`() {
        viewModel.setStartDate(LocalDate.now())
        viewModel.setEndDate(null)
        viewModel.calendarMode = CalendarSelectionMode.TWO_DAYS
        viewModel.setSelectionLayoutState()
        assertEquals(
            CalendarViewModel.CalendarSelectionState.StartDate,
            viewModel.calendarSelectionState.value
        )
    }

    @Test
    fun `should set endDate calendar state`() {
        viewModel.setStartDate(LocalDate.now())
        viewModel.setEndDate(LocalDate.now().plusDays(2))
        viewModel.calendarMode = CalendarSelectionMode.TWO_DAYS
        viewModel.setSelectionLayoutState()
        assertEquals(
            CalendarViewModel.CalendarSelectionState.EndDate,
            viewModel.calendarSelectionState.value
        )
    }

    @Test
    fun `should set notSelected calendar state`() {
        viewModel.setStartDate(null)
        viewModel.setEndDate(null)
        viewModel.calendarMode = CalendarSelectionMode.SINGLE_DAY
        viewModel.setSelectionLayoutState()
        assertEquals(
            CalendarViewModel.CalendarSelectionState.NotSelected,
            viewModel.calendarSelectionState.value
        )
    }
}