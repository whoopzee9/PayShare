package ru.spbstu.feature.calendar.presentation.calendar_binder

import android.annotation.SuppressLint
import android.view.View
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.ViewContainer
import ru.spbstu.feature.databinding.IncludeCalendarDayLayoutBinding
import java.time.LocalDate

@SuppressLint("NewApi")
class DayViewContainer(
    view: View,
    private val onDayClickListener: (date: LocalDate) -> Unit,
) :
    ViewContainer(view) {
    lateinit var day: CalendarDay // Will be set when this container is bound.
    val binding = IncludeCalendarDayLayoutBinding.bind(view)

    init {
        view.setOnClickListener {
            if (day.owner == DayOwner.THIS_MONTH &&
                (day.date == LocalDate.now() || day.date.isBefore(LocalDate.now()))
            ) {
                onDayClickListener(day.date)
            }
        }
    }
}
