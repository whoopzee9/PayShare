package ru.spbstu.feature.domain.usecase

import io.reactivex.Single
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.mock
import ru.spbstu.common.error.PayShareResult
import ru.spbstu.common.model.EventError
import ru.spbstu.feature.domain.repository.FeatureRepository

class DeleteRoomUseCaseTest {
    private val repository = mock<FeatureRepository>()
    private val rxSingleTestResponse: Single<PayShareResult<Any>> =
        Single.just(PayShareResult.Success(Any()))


    @Test
    fun `should successfully delete the room`() {
        val roomId = 2332L

        Mockito.`when`(repository.deleteRoom(roomId = roomId)).thenReturn(rxSingleTestResponse)

        DeleteRoomUseCase(repository)
            .invoke(roomId = roomId).test().assertValue {
                it is PayShareResult.Success
            }
    }

    @Test
    fun `should return connection error`() {
        val roomId = 2112L

        val rxSingleTestRoomResponse: Single<PayShareResult<Any>> =
            Single.just(PayShareResult.Error(EventError.ConnectionError))

        Mockito.`when`(repository.deleteRoom(roomId = roomId))
            .thenReturn(rxSingleTestRoomResponse)

        DeleteRoomUseCase(repository)
            .invoke(roomId = roomId).test().assertValue {
                val result = when (it) {
                    is PayShareResult.Success -> {
                        false
                    }
                    is PayShareResult.Error -> {
                        it.error == EventError.ConnectionError
                    }
                }
                result
            }
    }
}