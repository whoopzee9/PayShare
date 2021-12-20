package ru.spbstu.feature.expense.presentation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.spbstu.common.utils.BackViewModel
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.domain.model.Expense
import ru.spbstu.feature.domain.model.Shop
import ru.spbstu.feature.domain.model.User
import java.time.LocalDateTime

class ExpenseViewModel(router: FeatureRouter) : BackViewModel(router) {

    private val _purchase: MutableStateFlow<Expense> = MutableStateFlow(Expense())
    val purchase get(): StateFlow<Expense> = _purchase

    private val _users: MutableStateFlow<List<User>> = MutableStateFlow(listOf())
    val users get(): StateFlow<List<User>> = _users

    init {
        val userList = listOf(
            User(
                3,
                "Anna",
                "Vatlin",
                "dasd",
                "https://images.pexels.com/photos/1840608/pexels-photo-1840608.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260"
            ),
            User(
                4,
                "Veronika",
                "Zemskaya",
                "dasd",
                "https://images.pexels.com/photos/2120114/pexels-photo-2120114.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260"
            ),
            User(
                5,
                "John",
                "Martin",
                "dasd",
                "https://images.pexels.com/photos/2379005/pexels-photo-2379005.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260"
            ),
            User(
                6,
                "Demnos",
                "Luter",
                "dasd",
                "https://images.pexels.com/photos/2071881/pexels-photo-2071881.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260"
            ),
            User(
                7,
                "Pavel",
                "Pauls",
                "dasd",
                "https://images.pexels.com/photos/2743754/pexels-photo-2743754.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260"
            )
        )
        _purchase.value = Expense(
            10,
            "Фрукты",
            "",
            true,
            currentUser,
            LocalDateTime.now(),
            512.55,
            userList,
            Shop(1, "fsdfsdf", 12.0, 23.9, listOf())
        )
        _users.value = userList
    }

    companion object {
        // TODO add method to get current user
        val currentUser = User(
            11,
            "Толстолобиков",
            "Григорий",
            "tolstolobik1337@bk.ru",
            "https://avt-19.foto.mail.ru/mail/gt230800/_avatar180?1479972314&mrim=1"
        )
    }
}
