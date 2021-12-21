package ru.spbstu.feature.debt.presentation

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
import ru.spbstu.feature.domain.model.EventInfo
import ru.spbstu.feature.domain.model.Expense
import ru.spbstu.feature.domain.usecase.GetEventInfoUseCase
import ru.spbstu.feature.domain.usecase.SetPurchasePaidUseCase


class DebtViewModel(
    val router: FeatureRouter,
    private val getEventInfoUseCase: GetEventInfoUseCase,
    private val setPurchasePaidUseCase: SetPurchasePaidUseCase
) : BackViewModel(router) {
    private val _debts: MutableStateFlow<EventInfo> = MutableStateFlow(EventInfo())
    val debts get() :StateFlow<EventInfo> = _debts

    var roomId: Long = 0

    fun getData(id: Long) {
        setEventState(EventState.Progress)
        getEventInfoUseCase.invoke(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                when (it) {
                    is PayShareResult.Success -> {
                        _debts.value = it.data
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

    fun onDebtChecked(item: Expense, isChecked: Boolean) {
        setEventState(EventState.Progress)
        setPurchasePaidUseCase.invoke(roomId, item.id, debts.value.yourParticipantId, isChecked)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                when (it) {
                    is PayShareResult.Success -> {
                        setEventState(EventState.Success)
                        getData(roomId) //todo maybe change idk
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