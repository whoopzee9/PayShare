package ru.spbstu.feature.qr_code.presentation

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import ru.spbstu.common.error.PayShareResult
import ru.spbstu.common.model.EventError
import ru.spbstu.common.model.EventState
import ru.spbstu.common.utils.BackViewModel
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.domain.model.Event
import ru.spbstu.feature.domain.usecase.JoinEventUseCase
import ru.spbstu.feature.domain.usecase.ShowJoinEventUseCase

class QrCodeViewModel(
    val router: FeatureRouter,
    private val joinEventUseCase: JoinEventUseCase,
    private val showJoinEventUseCase: ShowJoinEventUseCase
) : BackViewModel(router) {

    fun joinEvent(id: Long) {
        setEventState(EventState.Progress)
        joinEventUseCase.invoke(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                when (it) {
                    is PayShareResult.Success -> {
                        setEventState(EventState.Success)
                        openEventFragment(id)
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

    fun openEventFragment(id: Long) {
        router.openEventFragment(id)
    }
}