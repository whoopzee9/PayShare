package ru.spbstu.feature.events.presentation

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.spbstu.common.utils.BackViewModel
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.domain.model.Event
import ru.spbstu.feature.domain.model.Expense
import ru.spbstu.feature.domain.model.Shop
import ru.spbstu.feature.domain.model.User
import timber.log.Timber
import java.time.LocalDateTime

class EventsViewModel(private val router: FeatureRouter) : BackViewModel(router) {

    private val _events: MutableStateFlow<List<Event>> = MutableStateFlow(listOf())
    val events get(): StateFlow<List<Event>> = _events

    fun setEvents(events: List<Event>) {
        _events.value = events
    }

    fun openEvent(event: Event) {
        Timber.i("??? Added")
        Log.d("??","clicked")
        router.openEventFragment(event)
    }

    init {
        _events.value = listOf(
            Event(
                id = 1,
                code = "sdfsdf",
                "qweqeqweqwe",
                LocalDateTime.now(),
                expenses = listOf(
                    Expense(
                        id = 1,
                        "cchuifvbcyh",
                        "sdfsdfsf",
                        false,
                        User(1, "dasd", "dasd", "dasd"),
                        LocalDateTime.now(),
                        123.0,
                        listOf(),
                        Shop(1, "fsdfsdf", 12.0, 23.9, listOf())
                    ),
                    Expense(
                        id = 2,
                        "qwe",
                        "sdfsdfsf",
                        false,
                        User(1, "dasd", "dasd", "dasd"),
                        LocalDateTime.now(),
                        1230.0,
                        listOf(),
                        Shop(1, "fsdfsdf", 12.0, 23.9, listOf())
                    )
                ),
                users = listOf(),
                true
            ),
            Event(
                id = 2,
                code = "sdfsdf",
                "kjkljkg",
                LocalDateTime.now(),
                expenses = listOf(
                    Expense(
                        id = 3,
                        "cc",
                        "sdfsdfsf",
                        false,
                        User(1, "dasd", "dasd", "dasd"),
                        LocalDateTime.now(),
                        13.0,
                        listOf(),
                        Shop(1, "fsdfsdf", 12.0, 23.9, listOf())
                    )
                ),
                users = listOf(),
                true
            ),
            Event(
                id = 3,
                code = "sdfsdf",
                "qweqwe",
                LocalDateTime.now(),
                expenses = listOf(),
                users = listOf(),
                false
            )
        )
    }
}
