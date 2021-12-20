package ru.spbstu.common.error

import ru.spbstu.common.model.EventError

sealed class PayShareResult<T> {
    data class Success<T>(val data: T): PayShareResult<T>()
    data class Error<T>(val error: EventError): PayShareResult<T>()
}