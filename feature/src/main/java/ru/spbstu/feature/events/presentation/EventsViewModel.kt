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
import ru.spbstu.feature.domain.usecase.ShowJoinEventUseCase


class EventsViewModel(
    private val router: FeatureRouter,
    val bundleDataWrapper: BundleDataWrapper,
    val tokenRepository: TokenRepository,
    private val getEventsUseCase: GetEventsUseCase,
    private val createEventUseCase: CreateEventUseCase,
    private val joinEventUseCase: JoinEventUseCase,
    private val showJoinEventUseCase: ShowJoinEventUseCase
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

    fun createEvent(name: String, date: String, callback: () -> Unit) {
        setEventState(EventState.Progress)
        createEventUseCase.invoke(name, date)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                when (it) {
                    is PayShareResult.Success -> {
                        setEventState(EventState.Success)
                        callback()
                        openEvent(it.data, name)
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

    fun joinEvent(event: Event) {
        setEventState(EventState.Progress)
        joinEventUseCase.invoke(event.id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                when (it) {
                    is PayShareResult.Success -> {
                        setEventState(EventState.Success)
                        openEvent(event.id, event.name)
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

    fun showJoinEvent(code: String, callback: (Event) -> Unit) {
        setEventState(EventState.Progress)
        showJoinEventUseCase.invoke(code)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                when (it) {
                    is PayShareResult.Success -> {
                        setEventState(EventState.Success)
                        callback(it.data)
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

    fun openEvent(id: Long, title: String) {
        router.openEventFragment(id, title)
    }

    fun openQrCodeScanner() {
        router.openQrCodeFragment()
    }


}