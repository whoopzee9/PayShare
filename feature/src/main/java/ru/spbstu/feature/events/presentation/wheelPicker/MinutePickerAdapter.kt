package ru.spbstu.feature.events.presentation.wheelPicker

import studio.clapp.wheelpicker.adapters.WheelAdapter
import studio.clapp.wheelpicker.extensions.checkSixtyMinutes
import studio.clapp.wheelpicker.extensions.formatLeadingZero
import kotlin.math.abs

class MinutePickerAdapter : WheelAdapter() {

    companion object {
        const val MINUTES = 60
        const val FIVE_MINUTES = 12
    }

    override fun getValue(position: Int): String {
        return if (position >= 0) (abs(position) % FIVE_MINUTES * 5).formatLeadingZero() else (MINUTES - abs(
            position
        ) % FIVE_MINUTES * 5).checkSixtyMinutes().formatLeadingZero()
    }

    override fun getPosition(vale: String): Int {
        return vale.toInt() / 5
    }

    override fun getTextWithMaximumLength(): String {
        return "60"
    }
}