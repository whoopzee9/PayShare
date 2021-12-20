package ru.spbstu.feature.events.presentation

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.spbstu.common.error.PayShareResult
import ru.spbstu.common.model.EventError
import ru.spbstu.common.model.EventState
import ru.spbstu.common.token.TokenRepository
import ru.spbstu.common.utils.BackViewModel
import ru.spbstu.common.utils.BundleDataWrapper
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.domain.model.Event
import ru.spbstu.feature.domain.usecase.CreateEventUseCase
import ru.spbstu.feature.domain.usecase.GetEventsUseCase
import ru.spbstu.feature.domain.usecase.JoinEventUseCase


class EventsViewModel(
    private val router: FeatureRouter,
    val bundleDataWrapper: BundleDataWrapper,
    val tokenRepository: TokenRepository,
    private val getEventsUseCase: GetEventsUseCase,
    private val createEventUseCase: CreateEventUseCase,
    private val joinEventUseCase: JoinEventUseCase
) : BackViewModel(router) {

    private val _events: MutableStateFlow<List<Event>> = MutableStateFlow(listOf())
    val events get() :StateFlow<List<Event>> = _events

    fun getEvents() {
        setEventState(EventState.Progress)
        getEventsUseCase.invoke()
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

    fun createEvent(name: String, date: String) {
        setEventState(EventState.Progress)
        createEventUseCase.invoke(name, date)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                when (it) {
                    is PayShareResult.Success -> {
                        setEventState(EventState.Success)
                        openEvent(it.data)
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

    fun joinEvent(code: String) {
        setEventState(EventState.Progress)
        joinEventUseCase.invoke(code)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                when (it) {
                    is PayShareResult.Success -> {
                        setEventState(EventState.Success)
                        openEvent(it.data)
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

    fun openLogin() {
        router.openLoginFragment()
    }

    fun openEvent(id: Long) {
        router.openEventFragment(id)
    }

    fun openQrCodeScanner() {
        router.openQrCodeFragment()
    }


}