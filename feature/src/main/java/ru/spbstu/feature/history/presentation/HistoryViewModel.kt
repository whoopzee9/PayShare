package ru.spbstu.feature.history.presentation

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
import ru.spbstu.feature.domain.model.Event
import ru.spbstu.feature.domain.usecase.GetHistoryUseCase

class HistoryViewModel(val router: FeatureRouter, private val getHistoryUseCase: GetHistoryUseCase) : BackViewModel(router) {
    private val _events: MutableStateFlow<List<Event>> = MutableStateFlow(listOf())
    val events get(): StateFlow<List<Event>> = _events

    fun getEvents() {
        setEventState(EventState.Progress)
        getHistoryUseCase.invoke()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                when (it) {
                    is PayShareResult.Success -> {
                        _events.value = it.data
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

    fun openEvent(id: Long) {
        router.openEventFragment(id)
    }
}
