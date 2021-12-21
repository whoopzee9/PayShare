package ru.spbstu.feature.calendar.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import com.kizitonwose.calendarview.utils.yearMonth
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.spbstu.feature.calendar.presentation.calendar_binder.CalendarDayBinder
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extenstions.addBackPressedCallback
import ru.spbstu.common.extenstions.setDebounceClickListener
import ru.spbstu.common.extenstions.setStandaloneMonthString
import ru.spbstu.common.extenstions.setTextColorFromRes
import ru.spbstu.common.utils.FullScreenBottomSheetDialogFragment
import ru.spbstu.feature.R
import ru.spbstu.feature.databinding.FragmentCalendarBinding
import ru.spbstu.feature.databinding.IncludeCalendarMonthLayoutBinding
import ru.spbstu.feature.di.FeatureApi
import ru.spbstu.feature.di.FeatureComponent
import ru.spbstu.feature.domain.model.CalendarDateRange
import ru.spbstu.feature.domain.model.CalendarSelectionMode
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class CalendarFragment(
    private val mode: CalendarSelectionMode
) : FullScreenBottomSheetDialogFragment() {

    @Inject
    lateinit var viewModel: CalendarViewModel

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = requireNotNull(_binding)

    private lateinit var calendarDayBinder: CalendarDayBinder

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        inject()
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        setupFromArguments()

        binding.frgCalendarMbDone.setDebounceClickListener {
            viewModel.selectRange(
                CalendarDateRange(
                    viewModel.getStartDate(),
                    viewModel.getEndDate()
                )
            )
            hide()
        }
        binding.frgCalendarIbBack.setDebounceClickListener {
            hide()
        }
        addBackPressedCallback { binding.frgCalendarIbBack.callOnClick() }
        initCalendar()
        subscribe()
        return binding.root
    }

    override fun onDialogShown() {
        super.onDialogShown()
        binding.frgCalendarCalendarLayout.includeCalendarView.scrollToMonth(LAST_CALENDAR_MONTH)
    }

    private fun setupFromArguments() {
        viewModel.calendarMode = mode
        when (mode) {
            CalendarSelectionMode.SINGLE_DAY -> {
                binding.frgCalendarTvTitle.text = getString(R.string.event_date)
                binding.frgCalendarTvCalendarSelectDate.text =
                    getString(R.string.select_date)
            }
            else -> {
            }
        }
    }

    private fun subscribe() {
        lifecycleScope.launch {
            viewModel.calendarSelectionState.collect {
                handleCalendarSelectionState(it)
            }
        }
    }

    @SuppressLint("NewApi")
    private fun handleCalendarSelectionState(state: CalendarViewModel.CalendarSelectionState) {
        val selectionTitle = binding.frgCalendarTvCalendarSelectDate.apply {
            visibility = View.GONE
        }
        val arrowIcon = binding.frgCalendarIvArrow.apply { visibility = View.GONE }
        val startDate = binding.frgCalendarTvStartDate.apply { visibility = View.GONE }
        val singleDate = binding.frgCalendarTvSingleDay.apply { visibility = View.GONE }
        val endDate = binding.frgCalendarTvEndDate.apply { visibility = View.GONE }
        when (state) {
            is CalendarViewModel.CalendarSelectionState.Initial -> {
                // Empty state
                viewModel.setSelectionLayoutState()
            }
            is CalendarViewModel.CalendarSelectionState.SingleDay -> {
                startDate.visibility = View.GONE
                arrowIcon.visibility = View.GONE
                endDate.visibility = View.GONE
                singleDate.apply {
                    visibility = View.VISIBLE
                    text = datePattern.format(viewModel.getStartDate())
                }
            }
            is CalendarViewModel.CalendarSelectionState.NotSelected -> {
                selectionTitle.visibility = View.VISIBLE
            }
            is CalendarViewModel.CalendarSelectionState.StartDate -> {
                arrowIcon.visibility = View.VISIBLE
                startDate.apply {
                    visibility = View.VISIBLE
                    text = datePattern.format(viewModel.getStartDate())
                }
                endDate.apply {
                    visibility = View.VISIBLE
                    text = getString(R.string.end_date)
                    setTextColorFromRes(R.color.text_color_primary)
                }
            }
            is CalendarViewModel.CalendarSelectionState.EndDate -> {
                arrowIcon.visibility = View.VISIBLE
                startDate.visibility = View.VISIBLE
                endDate.apply {
                    visibility = View.VISIBLE
                    text = datePattern.format(viewModel.getEndDate())
                    setTextColorFromRes(R.color.black)
                }
            }
        }
    }

    @SuppressLint("NewApi")
    private fun initCalendar() {
        setDoneButtonIsClickable(CalendarDateRange())
        calendarDayBinder = CalendarDayBinder(
            requireContext(),
            onDayClickListener = {
                viewModel.selectDate(it) { calendarDateRange ->
                    setDoneButtonIsClickable(calendarDateRange)
                    calendarDayBinder.setSelectedDate(calendarDateRange)
                    binding.frgCalendarCalendarLayout.includeCalendarView.notifyCalendarChanged()
                }
            }
        )

        class MonthViewContainer(view: View) : ViewContainer(view) {
            val textView =
                IncludeCalendarMonthLayoutBinding.bind(view).includeCalendarDayLayoutTvDay
        }

        val currentMonth = YearMonth.now()
        val lastMonth = currentMonth.plusMonths(THREE_MONTH)
        val firstDayOfWeek = DayOfWeek.MONDAY
        with(binding.frgCalendarCalendarLayout.includeCalendarView) {
            dayBinder = calendarDayBinder
            monthHeaderBinder = object :
                MonthHeaderFooterBinder<MonthViewContainer> {
                override fun create(view: View) = MonthViewContainer(view)
                override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                    container.textView.setStandaloneMonthString(
                        month.yearMonth,
                        month.year.toString()
                    )
                }
            }
            setup(currentMonth.minusMonths(THREE_MONTH), lastMonth, firstDayOfWeek)
            scrollToMonth(LAST_CALENDAR_MONTH)
            updateMonthRangeAsync(INITIAL_CALENDAR_MONTH, LAST_CALENDAR_MONTH.plusMonths(ONE_MONTH))
        }
    }

    @SuppressLint("NewApi")
    private fun setDoneButtonIsClickable(calendarDateRange: CalendarDateRange) {
        if (calendarDateRange.endDate != null || (
                    calendarDateRange.startDate != null &&
                            viewModel.calendarMode == CalendarSelectionMode.SINGLE_DAY
                    )
        ) {
            binding.frgCalendarMbDone.isEnabled = true
            binding.frgCalendarMbDone.setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.button_tint_primary)
            )
        } else {
            binding.frgCalendarMbDone.isEnabled = false
            binding.frgCalendarMbDone.setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.button_disabled_tint)
            )
        }
    }

    private fun inject() {
        FeatureUtils.getFeature<FeatureComponent>(this, FeatureApi::class.java)
            .calendarComponentFactory()
            .create(this)
            .inject(this)
    }

    companion object {
        private val TAG: String = CalendarFragment::class.java.simpleName
        private const val THREE_MONTH: Long = 3
        private const val ONE_MONTH: Long = 1

        @SuppressLint("NewApi")
        private val INITIAL_CALENDAR_MONTH: YearMonth = YearMonth.of(2000, 1)

        @SuppressLint("NewApi")
        private val LAST_CALENDAR_MONTH: YearMonth = LocalDate.now().yearMonth

        @SuppressLint("NewApi")
        private val datePattern: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
        val DATA_KEY = "${TAG}DATA_KEY"
        val TYPE_KEY = "${TAG}TYPE_KEY"
    }
}
