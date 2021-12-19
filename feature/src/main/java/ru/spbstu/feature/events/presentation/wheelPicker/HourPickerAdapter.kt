package ru.spbstu.feature.events.presentation.wheelPicker

import studio.clapp.wheelpicker.adapters.WheelAdapter
import studio.clapp.wheelpicker.extensions.formatLeadingZero
import kotlin.math.abs

class HourPickerAdapter : WheelAdapter() {
    companion object {
        const val HOURS = 24
    }

    override fun getValue(position: Int): String {
        return if (position >= 0) (abs(position) % HOURS).formatLeadingZero()
        else (HOURS - 1 - abs(position + 1) % HOURS).formatLeadingZero()
    }

    override fun getPosition(vale: String): Int {
        return vale.toInt()
    }

    override fun getTextWithMaximumLength(): String {
        return "24"
    }
}