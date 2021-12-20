package ru.spbstu.feature.event.presentation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.spbstu.common.utils.BackViewModel
import ru.spbstu.common.utils.BundleDataWrapper
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.domain.model.Expense
import ru.spbstu.feature.domain.model.Shop
import ru.spbstu.feature.domain.model.User
import java.time.LocalDateTime

class EventViewModel(val router: FeatureRouter, val bundleDataWrapper: BundleDataWrapper) :
    BackViewModel(router) {
    private val _purchases: MutableStateFlow<List<Expense>> = MutableStateFlow(listOf())
    val purchases get(): StateFlow<List<Expense>> = _purchases

    private val _users: MutableStateFlow<List<User>> = MutableStateFlow(listOf())
    val users get(): StateFlow<List<User>> = _users

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
                listOf(
                    User(
                        1, "dasd", "dasd", "dasd",
                        "https://images.pexels.com/photos/614810/pexels-photo-614810.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260"
                    ),
                    User(
                        2, "Egor", "Egorov", "dasd",
                        "https://images.pexels.com/photos/4556737/pexels-photo-4556737.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260"
                    ),
                ),
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
                listOf(
                    User(
                        1, "dasd", "dasd", "dasd",
                        "https://images.pexels.com/photos/614810/pexels-photo-614810.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260"
                    ),
                    User(
                        2, "Egor", "Egorov", "dasd",
                        "https://images.pexels.com/photos/4556737/pexels-photo-4556737.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260"
                    ),
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
                    ),
                ),
                Shop(1, "fsdfsdf", 12.0, 23.9, listOf())
            )
        )
        _users.value = _purchases.value.map { it.users }.flatten().distinctBy { it.id }
    }

    fun setBoughtStatus(purchase: Expense) {
        _purchases.value = _purchases.value.map {
            if (it.isContentEqual(purchase)) {
                it.copy(isBought = !purchase.isBought)
            } else {
                it
            }
        }
    }

    fun createNewPurchase(
        textTitle: String,
        textPrice: String,
        textDate: String,
        textShop: String
    ) {
        // TODO send info by useCase
        _purchases.value = _purchases.value + Expense(
            444,
            "",
            textTitle,
            false,
            currentUser,
            LocalDateTime.now(),
            textPrice.toDouble(),
            emptyList(),
            Shop(1, "fsdfsdf", 12.0, 23.9, listOf())
        )
        updateUsers()
    }

    private fun updateUsers() {
        _users.value = _purchases.value.map { it.users }.flatten().distinctBy { it.id }
    }

    fun selectAllPurchases() {
        // TODO select all and send by usecase
        _purchases.value = _purchases.value.map { it.copy(isBought = true) }
    }

    fun openPurchase(expense: Expense) {
    }

    fun shareRoomCode() {
        router.openQrCodeSharingFragment("12588")
    }

    companion object {
        // TODO add method to get current user
        val currentUser = User(
            11,
            "Tolstolobik",
            "Georgiy",
            "tolstolobik1337@bk.ru",
            "https://avt-19.foto.mail.ru/mail/gt230800/_avatar180?1479972314&mrim=1"
        )
    }
}
