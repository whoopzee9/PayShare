package ru.spbstu.feature.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
data class CalendarDateRange(
    val startDate: LocalDate?,
    val endDate: LocalDate?
) : Parcelable {
    constructor() : this(null, null)
}
