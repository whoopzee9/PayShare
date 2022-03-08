package ru.spbstu.feature.event.presentation

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.spbstu.common.error.PayShareResult
import ru.spbstu.common.model.EventError
import ru.spbstu.common.model.EventState
import ru.spbstu.common.utils.BackViewModel
import ru.spbstu.common.utils.BundleDataWrapper
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.domain.model.Event
import ru.spbstu.feature.domain.model.EventInfo
import ru.spbstu.feature.domain.model.Expense
import ru.spbstu.feature.domain.model.Shop
import ru.spbstu.feature.domain.model.User
import ru.spbstu.feature.domain.usecase.CreatePurchaseUseCase
import ru.spbstu.feature.domain.usecase.DeletePurchaseUseCase
import ru.spbstu.feature.domain.usecase.DeleteRoomUseCase
import ru.spbstu.feature.domain.usecase.GetEventInfoUseCase
import ru.spbstu.feature.domain.usecase.GetRoomCodeUseCase
import ru.spbstu.feature.domain.usecase.SetPurchaseJoinUseCase
import ru.spbstu.feature.mapSelect.ShopMapFragment
import timber.log.Timber
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
class EventViewModel(
    val router: FeatureRouter,
    val bundleDataWrapper: BundleDataWrapper,
    private val createPurchaseUseCase: CreatePurchaseUseCase,
    private val getEventInfoUseCase: GetEventInfoUseCase,
    private val getRoomCodeUseCase: GetRoomCodeUseCase,
    private val deleteRoomUseCase: DeleteRoomUseCase,
    private val deletePurchaseUseCase: DeletePurchaseUseCase,
    private val setPurchaseJoinUseCase: SetPurchaseJoinUseCase
) :
    BackViewModel(router) {
    var roomId = 0L

    var event: EventInfo = EventInfo()
    var title: String = ""

    private val _purchases: MutableStateFlow<List<Expense>> = MutableStateFlow(listOf())
    val purchases get(): StateFlow<List<Expense>> = _purchases

    private val _users: MutableStateFlow<List<User>> = MutableStateFlow(listOf())
    val users get(): StateFlow<List<User>> = _users

    private val _toolbarState: MutableStateFlow<ToolbarState> =
        MutableStateFlow(ToolbarState.Initial)
    val toolbarState get(): StateFlow<ToolbarState> = _toolbarState

    fun loadPurchases() {
        getEventInfoUseCase.invoke(roomId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                when (it) {
                    is PayShareResult.Success -> {
                        event = it.data
                        _purchases.value = it.data.purchases
                        _users.value = it.data.participants
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

    fun setBoughtStatus(purchase: Expense, isClicked: Boolean) {
        setPurchaseJoinUseCase.invoke(roomId, purchase.id, event.yourParticipantId, isClicked)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                when (it) {
                    is PayShareResult.Success -> {
                        loadPurchases()
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
//        _purchases.value = _purchases.value.map {
//            if (it.isContentEqual(purchase)) {
//                it.copy(isBought = !purchase.isBought)
//            } else {
//                it
//            }
//        }
    }

    fun createNewPurchase(
        textTitle: String,
        textPrice: String,
        textDate: String
    ) {
        val shop =
            (bundleDataWrapper.bundleData.value.getParcelable<Shop>(ShopMapFragment.DATA_KEY))
        if (shop != null) {
            createPurchaseUseCase.invoke(
                roomId,
                Expense(
                    name = textTitle,
                    date = LocalDateTime.now(),
                    purchaseShop = shop,
                    price = textPrice.toDouble()
                )
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when (it) {
                        is PayShareResult.Success -> {
                            loadPurchases()
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
    }

    fun selectAllPurchases() {
        // TODO select all and send by usecase
        _purchases.value = _purchases.value.map { it.copy(isBought = true) }
    }

    fun openPurchase(expense: Expense) {
        // TODO pass roomID
        router.openExpenseFragment(roomId, expense, title)
    }

    fun shareRoomCode() {
        getRoomCodeUseCase.invoke(roomId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                when (it) {
                    is PayShareResult.Success -> {
                        setEventState(EventState.Success)
                        Timber.i("CODE:${it.data}")
                        Log.d("CODE", "${it.data}")
                        router.openQrCodeSharingFragment(it.data.toString())
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

    fun changeToolbarState() {
        when (_toolbarState.value) {
            is ToolbarState.Initial -> _toolbarState.value = ToolbarState.EditMode
            is ToolbarState.EditMode -> _toolbarState.value = ToolbarState.Initial
        }
    }

    // TODO Delete success if user room owner
    fun deleteRoom() {
        deleteRoomUseCase.invoke(roomId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                when (it) {
                    is PayShareResult.Success -> {
                        router.back()
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

    // TODO leave from room
    fun leaveFromRoom() {
    }

    // TODO add method to delete purchase from list
    fun deletePurchase(expense: Expense) {
        deletePurchaseUseCase.invoke(roomId, expense.id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                when (it) {
                    is PayShareResult.Success -> {
                        loadPurchases()
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

    // TODO add method to close purchase
    fun closePurchase(expense: Expense) {
    }

    fun setupRoomId(id: Long) {
        roomId = id
    }

    fun openDebtFragment() {
        router.openDebtFragment(roomId)
    }

    sealed class ToolbarState {
        object Initial : ToolbarState()
        object EditMode : ToolbarState()
    }

    companion object {
        // TODO add method to get current user
    }
}
