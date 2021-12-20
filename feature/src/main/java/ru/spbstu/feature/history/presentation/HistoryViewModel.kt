package ru.spbstu.feature.history.presentation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.spbstu.common.utils.BackViewModel
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.domain.model.Event
import ru.spbstu.feature.domain.model.Expense
import ru.spbstu.feature.domain.model.Shop
import ru.spbstu.feature.domain.model.User
import java.time.LocalDateTime

class HistoryViewModel(val router: FeatureRouter) : BackViewModel(router) {
    private val _events: MutableStateFlow<List<Event>> = MutableStateFlow(listOf())
    val events get(): StateFlow<List<Event>> = _events

    fun setEvents(events: List<Event>) {
        _events.value = events
    }

    fun openEvent(id: Long) {
        router.openEventFragment(id)
    }
}
