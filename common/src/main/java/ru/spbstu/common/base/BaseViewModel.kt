package ru.spbstu.common.base

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.spbstu.common.model.EventState

open class BaseViewModel : ViewModel() {

    private val _eventState: MutableStateFlow<EventState> = MutableStateFlow(EventState.Initial)
    val eventState: StateFlow<EventState> = _eventState

    val disposable = CompositeDisposable()

    fun setEventState(state: EventState) {
        _eventState.value = state
    }

    override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }
}
