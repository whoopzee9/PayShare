package ru.spbstu.feature.expense.presentation

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.spbstu.common.utils.BackViewModel
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.domain.model.Expense
import ru.spbstu.feature.domain.model.Shop
import ru.spbstu.feature.domain.model.User
import ru.spbstu.feature.domain.model.UserBuyed
import java.time.LocalDateTime

class ExpenseViewModel(router: FeatureRouter) : BackViewModel(router) {

    private val _purchase: MutableStateFlow<Expense> = MutableStateFlow(Expense())
    val purchase get(): StateFlow<Expense> = _purchase

    private val _users: MutableStateFlow<List<UserBuyed>> = MutableStateFlow(emptyList())
    val users get(): StateFlow<List<UserBuyed>> = _users

    private val _mapShopCoordinates: MutableStateFlow<List<LatLng>> =
        MutableStateFlow(emptyList())
    val mapShopCoordinates: MutableStateFlow<List<LatLng>> get() = _mapShopCoordinates

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
            userList.map { it to false }.toMap(),
            Shop(1, "fsdfsdf", 59.986505, 30.348305, listOf())
        )
        val list = _purchase.value.users.map { UserBuyed(it.key, it.value) }
        // TODO DELETE. this for test
        _users.value = list.mapIndexed { index, userBuyed ->
            if (index == 1) {
                userBuyed.copy(isBought = true)
            } else {
                userBuyed
            }
        }
        _mapShopCoordinates.value = listOf(
            LatLng(
                _purchase.value.purchaseShop.latitude,
                _purchase.value.purchaseShop.longitude
            )
        )
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
