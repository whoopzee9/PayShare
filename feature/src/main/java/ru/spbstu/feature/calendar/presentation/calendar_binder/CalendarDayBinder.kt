package ru.spbstu.feature.calendar.presentation.calendar_binder

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.TextView
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.utils.yearMonth
import ru.spbstu.common.extenstions.getDrawableCompat
import ru.spbstu.common.extenstions.margin
import ru.spbstu.common.extenstions.setTextColorFromRes
import ru.spbstu.feature.R
import ru.spbstu.feature.calendar.presentation.helper.DotViewCreator
import ru.spbstu.feature.domain.model.CalendarDateRange
import java.time.LocalDate

@SuppressLint("NewApi")
class CalendarDayBinder(
    val context: Context,
    val onDayClickListener: (day: LocalDate) -> Unit
) :
    DayBinder<DayViewContainer> {
    private val currentDay = LocalDate.now()
    private var startDate: LocalDate? = null
    private var endDate: LocalDate? = null

    private val middleBackground: GradientDrawable by lazy {
        context.getDrawableCompat(R.drawable.calendar_selected_bg_middle) as GradientDrawable
    }
    private val dotViewCreator by lazy { DotViewCreator(context) }

    override fun bind(container: DayViewContainer, day: CalendarDay) {
        container.day = day
        val textView = container.binding.includeCalendarDayLayoutTvDay
        val textOverlayView = container.binding.includeCalendarDayLayoutTvOverlayDay
        val roundBgView = container.binding.includeCalendarDayLayoutRoundView
        val roundOverlayBgView = container.binding.includeCalendarDayLayoutRoundOverlayView

        textView.text = null
        textOverlayView.text = null
        textView.background = null

        roundBgView.visibility = View.GONE
        roundOverlayBgView.visibility = View.GONE

        val startDate = startDate
        val endDate = endDate
        textView.margin(left = 0F, right = 0F)
        container.binding.includeCalendarDayContainerDots.removeAllViews()

        when (day.owner) {
            DayOwner.THIS_MONTH -> {
                textView.text = day.day.toString()
                if (day.date.isAfter(currentDay)) {
                    textView.setTextColorFromRes(R.color.calendar_day_color_disabled)
                } else {
                    when {
                        startDate == day.date && endDate == null -> {
                            textView.setTextColorFromRes(R.color.white)
                            roundBgView.visibility = View.VISIBLE
                            roundBgView.setBackgroundResource(R.drawable.calendar_selected_bg_single)
                        }
                        day.date == startDate -> {
                            setExtremeViews(textView, textOverlayView, roundOverlayBgView, true)
                        }
                        startDate != null && endDate != null && (day.date > startDate && day.date < endDate) -> {
                            textView.setTextColorFromRes(R.color.black)
                            textView.setBackgroundResource(R.drawable.calendar_selected_bg_middle)
                        }
                        day.date == endDate -> {
                            setExtremeViews(textView, textOverlayView, roundOverlayBgView, false)
                        }
                        day.date == currentDay -> {
                            textView.setTextColorFromRes(R.color.black)
                            roundBgView.visibility = View.GONE
                            val createdViewList = dotViewCreator.generateView(1)
                            createdViewList.forEach { pair ->
                                container.binding.includeCalendarDayContainerDots.addView(
                                    pair.first, pair.second
                                )
                            }
                        }
                        else -> textView.setTextColorFromRes(R.color.calendar_day_color_enabled)
                    }
                }
            }
            // Make the coloured selection background continuous on the invisible in and out dates across various months.
            DayOwner.PREVIOUS_MONTH -> if (startDate != null && endDate != null &&
                isInDateBetween(day.date, startDate, endDate)
            ) {
                textView.setBackgroundResource(R.drawable.calendar_selected_bg_middle)
            }
            DayOwner.NEXT_MONTH -> if (startDate != null && endDate != null &&
                isOutDateBetween(day.date, startDate, endDate)
            ) {
                textView.setBackgroundResource(R.drawable.calendar_selected_bg_middle)
            }
        }
    }

    override fun create(view: View) = DayViewContainer(
        view, onDayClickListener = { this.onDayClickListener(it) }
    )

    private fun isInDateBetween(
        inDate: LocalDate,
        startDate: LocalDate,
        endDate: LocalDate
    ): Boolean {
        if (startDate.yearMonth == endDate.yearMonth) return false
        if (inDate.yearMonth == startDate.yearMonth) return true
        val firstDateInThisMonth = inDate.plusMonths(1).yearMonth.atDay(1)
        return firstDateInThisMonth >= startDate && firstDateInThisMonth <= endDate && startDate != firstDateInThisMonth
    }

    private fun isOutDateBetween(
        outDate: LocalDate,
        startDate: LocalDate,
        endDate: LocalDate
    ): Boolean {
        if (startDate.yearMonth == endDate.yearMonth) return false
        if (outDate.yearMonth == endDate.yearMonth) return true
        val lastDateInThisMonth = outDate.minusMonths(1).yearMonth.atEndOfMonth()
        return lastDateInThisMonth >= startDate && lastDateInThisMonth <= endDate && endDate != lastDateInThisMonth
    }

    private fun setExtremeViews(
        textView: TextView,
        textOverlayView: TextView,
        roundOverlayBgView: View,
        isStartView: Boolean
    ) {
        if (isStartView) {
            textView.margin(left = VIEW_MARGIN)
        } else {
            textView.margin(right = VIEW_MARGIN)
        }
        textView.background = middleBackground
        textOverlayView.text = textView.text
        textOverlayView.setTextColorFromRes(R.color.white)
        roundOverlayBgView.visibility = View.VISIBLE
        roundOverlayBgView.setBackgroundResource(R.drawable.calendar_selected_bg_single)
    }

    fun setSelectedDate(calendarDateRange: CalendarDateRange) {
        startDate = calendarDateRange.startDate
        endDate = calendarDateRange.endDate
    }

    companion object {
        private const val VIEW_MARGIN = 20F
    }
}
