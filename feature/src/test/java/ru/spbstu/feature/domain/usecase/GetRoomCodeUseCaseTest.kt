package ru.spbstu.feature.domain.usecase

import io.reactivex.Single
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.mock
import ru.spbstu.common.error.PayShareResult
import ru.spbstu.common.model.EventError
import ru.spbstu.feature.domain.repository.FeatureRepository

class GetRoomCodeUseCaseTest {
    private val repository = mock<FeatureRepository>()
    private val roomId = 56L

    @Test
    fun `should return room code`() {
        val expectedCode = 3445647L
        val rxSingleResponse: Single<PayShareResult<Long>> =
            Single.just(PayShareResult.Success(expectedCode))

        Mockito.`when`(repository.getRoomCode(id = roomId)).thenReturn(rxSingleResponse)

        GetRoomCodeUseCase(repository).invoke(roomId).test().assertValue {
            val roomCode = when (it) {
                is PayShareResult.Success -> it.data
                else -> throw IllegalArgumentException("Not success status")
            }
            roomCode == expectedCode
        }
    }

    @Test
    fun `should return connection error`() {
        val rxSingleResponse: Single<PayShareResult<Long>> =
            Single.just(PayShareResult.Error(EventError.ConnectionError))

        Mockito.`when`(repository.getRoomCode(id = roomId)).thenReturn(rxSingleResponse)

        GetRoomCodeUseCase(repository).invoke(roomId).test().assertValue {
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