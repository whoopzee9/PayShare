package ru.spbstu.feature.domain.usecase

import io.reactivex.Single
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.mock
import ru.spbstu.common.error.PayShareResult
import ru.spbstu.common.model.EventError
import ru.spbstu.feature.domain.repository.FeatureRepository

class LogoutUseCaseTest {
    private val repository = mock<FeatureRepository>()
    private val rxSingleTestResponse: Single<PayShareResult<Any>> =
        Single.just(PayShareResult.Success(Any()))

    @Test
    fun `should successfully logout`() {
        val refreshToken = "refresh_token"

        Mockito.`when`(repository.logout(refreshToken = refreshToken))
            .thenReturn(rxSingleTestResponse)

        LogoutUseCase(repository)
            .invoke(refreshToken = refreshToken).test().assertValue {
                it is PayShareResult.Success
            }
    }

    @Test
    fun `error logout connection problem`() {
        val refreshToken = "refresh_token"
        val rxSingleErrorTestResponse: Single<PayShareResult<Any>> =
            Single.just(PayShareResult.Error(EventError.ConnectionError))

        Mockito.`when`(repository.logout(refreshToken = refreshToken))
            .thenReturn(rxSingleErrorTestResponse)

        LogoutUseCase(repository)
            .invoke(refreshToken = refreshToken).test().assertValue {
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