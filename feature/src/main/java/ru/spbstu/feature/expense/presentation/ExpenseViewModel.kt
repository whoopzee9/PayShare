package ru.spbstu.feature.expense.presentation

import com.google.android.gms.maps.model.LatLng
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.spbstu.common.error.PayShareResult
import ru.spbstu.common.model.EventError
import ru.spbstu.common.model.EventState
import ru.spbstu.common.utils.BackViewModel
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.domain.model.Expense
import ru.spbstu.feature.domain.model.User
import ru.spbstu.feature.domain.usecase.GetEventInfoUseCase

class ExpenseViewModel(
    val router: FeatureRouter,
    private val getEventInfoUseCase: GetEventInfoUseCase
) : BackViewModel(router) {

    private val _purchase: MutableStateFlow<Expense> = MutableStateFlow(Expense())
    val purchase get(): StateFlow<Expense> = _purchase

    private val _users: MutableStateFlow<List<User>> = MutableStateFlow(listOf())
    val users get(): StateFlow<List<User>> = _users

    private val _mapShopCoordinates: MutableStateFlow<List<LatLng>> =
        MutableStateFlow(listOf())
    val mapShopCoordinates: MutableStateFlow<List<LatLng>> get() = _mapShopCoordinates

    fun getData(roomId: Long, expenseId: Long) {
        getEventInfoUseCase.invoke(roomId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                when (it) {
                    is PayShareResult.Success -> {
                        _purchase.value =
                            it.data.purchases.first { expense -> expense.id == expenseId }
                        _users.value = it.data.participants.filter { user ->
                            purchase.value.users.containsKey(user.id)
                        }
                        val shop = purchase.value.purchaseShop
                        _mapShopCoordinates.value = listOf(LatLng(shop.latitude, shop.longitude))
                        setEventState(EventState.Success)
                    }
                    is PayShareResult.Error -> {
                        setEventState(EventState.Failure(it.error))
                    }
                }
            }, {
                setEventState(EventState.Failure(EventError.ConnectionError))
            })
            .addTo(disposable)
    }

    companion object {
        // TODO add method to get current user

    }
}
