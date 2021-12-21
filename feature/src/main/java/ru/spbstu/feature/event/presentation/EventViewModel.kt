package ru.spbstu.feature.event.presentation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.spbstu.common.utils.BackViewModel
import ru.spbstu.common.utils.BundleDataWrapper
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.domain.model.Expense
import ru.spbstu.feature.domain.model.Shop
import ru.spbstu.feature.domain.model.User
import ru.spbstu.feature.mapSelect.ShopMapFragment
import timber.log.Timber
import java.time.LocalDateTime

class EventViewModel(val router: FeatureRouter, val bundleDataWrapper: BundleDataWrapper) :
    BackViewModel(router) {
    private val _purchases: MutableStateFlow<List<Expense>> = MutableStateFlow(listOf())
    val purchases get(): StateFlow<List<Expense>> = _purchases

    private val _users: MutableStateFlow<List<User>> = MutableStateFlow(listOf())
    val users get(): StateFlow<List<User>> = _users

    private val _toolbarState: MutableStateFlow<ToolbarState> =
        MutableStateFlow(ToolbarState.Initial)
    val toolbarState get(): StateFlow<ToolbarState> = _toolbarState

    init {
//        _purchases.value = listOf(
//            Expense(
//                id = 1,
//                "cchuifvbcyh",
//                "sdfsdfsf",
//                false,
//                User(1, "dasd", "dasd", "dasd"),
//                LocalDateTime.now(),
//                123.0,
//                listOf(
//                    1,
//                    2,
//                ).map { it to false }.toMap(),
//                Shop(1, "fsdfsdf", 12.0, 23.9, listOf())
//            ),
//            Expense(
//                id = 2,
//                "qwe",
//                "sdfsdfsf",
//                false,
//                User(1, "dasd", "dasd", "dasd"),
//                LocalDateTime.now(),
//                1230.0,
//                listOf(
//                    User(
//                        1, "dasd", "dasd", "dasd",
//                        "https://images.pexels.com/photos/614810/pexels-photo-614810.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260"
//                    ),
//                    User(
//                        2, "Egor", "Egorov", "dasd",
//                        "https://images.pexels.com/photos/4556737/pexels-photo-4556737.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260"
//                    ),
//                    User(
//                        3,
//                        "Anna",
//                        "Vatlin",
//                        "dasd",
//                        "https://images.pexels.com/photos/1840608/pexels-photo-1840608.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260"
//                    ),
//                    User(
//                        4,
//                        "Veronika",
//                        "Zemskaya",
//                        "dasd",
//                        "https://images.pexels.com/photos/2120114/pexels-photo-2120114.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260"
//                    ),
//                    User(
//                        5,
//                        "John",
//                        "Martin",
//                        "dasd",
//                        "https://images.pexels.com/photos/2379005/pexels-photo-2379005.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260"
//                    ),
//                    User(
//                        6,
//                        "Demnos",
//                        "Luter",
//                        "dasd",
//                        "https://images.pexels.com/photos/2071881/pexels-photo-2071881.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260"
//                    ),
//                    User(
//                        7,
//                        "Pavel",
//                        "Pauls",
//                        "dasd",
//                        "https://images.pexels.com/photos/2743754/pexels-photo-2743754.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260"
//                    ),
//                ).map { it to false }.toMap(),
//                Shop(1, "fsdfsdf", 12.0, 23.9, listOf())
//            )
//        )
//        _users.value = _purchases.value.map { it.users.keys }.flatten().distinctBy { it.id }
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

        val shop =
            (bundleDataWrapper.bundleData.value.getParcelable<Shop>(ShopMapFragment.DATA_KEY))

        if (shop != null) {
            Timber.i("???? ${shop.latitude}, ${shop.longitude}}")
        }

        // TODO send info by useCase
        _purchases.value = _purchases.value + Expense(
            444,
            "",
            textTitle,
            false,
            currentUser,
            LocalDateTime.now(),
            textPrice.toDouble(),
            emptyMap(),
            Shop(1, "fsdfsdf", 12.0, 23.9, listOf())
        )
        updateUsers()
    }

    private fun updateUsers() {
        //_users.value = _purchases.value.map { it.users.keys }.flatten().distinctBy { it.id }
    }

    fun selectAllPurchases() {
        // TODO select all and send by usecase
        _purchases.value = _purchases.value.map { it.copy(isBought = true) }
    }

    fun openPurchase(expense: Expense) {
        router.openExpenseFragment(expense)
    }

    fun shareRoomCode() {
        router.openQrCodeSharingFragment("12588")
    }

    fun changeToolbarState() {
        when (_toolbarState.value) {
            is ToolbarState.Initial -> _toolbarState.value = ToolbarState.EditMode
            is ToolbarState.EditMode -> _toolbarState.value = ToolbarState.Initial
        }
    }

    // TODO Delete success if user room owner
    fun deleteRoom() {
    }

    // TODO leave from room
    fun leaveFromRoom() {
    }

    // TODO add method to delete purchase from list
    fun deletePurchase(expense: Expense) {
    }

    // TODO add method to close purchase
    fun closePurchase(expense: Expense) {
    }

    sealed class ToolbarState {
        object Initial : ToolbarState()
        object EditMode : ToolbarState()
    }

    companion object {
        // TODO add method to get current user
        val currentUser = User(
            11,
            "Tolstolobik",
            "Georgiy",
            "https://avt-19.foto.mail.ru/mail/gt230800/_avatar180?1479972314&mrim=1"
        )
    }
}
