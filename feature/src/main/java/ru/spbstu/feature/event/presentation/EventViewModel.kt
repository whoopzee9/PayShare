package ru.spbstu.feature.event.presentation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.spbstu.common.utils.BackViewModel
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.domain.model.Expense
import ru.spbstu.feature.domain.model.Shop
import ru.spbstu.feature.domain.model.User
import java.time.LocalDateTime

class EventViewModel(router: FeatureRouter) : BackViewModel(router) {
    private val _purchases: MutableStateFlow<List<Expense>> = MutableStateFlow(listOf())
    val purchases get(): StateFlow<List<Expense>> = _purchases

    init {
        _purchases.value = listOf(
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
        )
    }

    fun setBoughtStatus(purchase: Expense) {
    }
}
