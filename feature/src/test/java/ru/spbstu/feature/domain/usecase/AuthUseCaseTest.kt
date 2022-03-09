package ru.spbstu.feature.domain.usecase

import io.reactivex.Single
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.mock
import ru.spbstu.common.error.PayShareResult
import ru.spbstu.common.model.EventError
import ru.spbstu.feature.domain.model.Tokens
import ru.spbstu.feature.domain.repository.FeatureRepository

class AuthUseCaseTest {
    private val repository = mock<FeatureRepository>()
    private val apiGoogle = "google"
    private val token = "google_123"

    @Test
    fun `check not empty token`() {
        val expectedTokens = Tokens(
            accessToken = "access_token",
            refreshToken = "refresh_token"
        )
        val rxSingleTestTokens: Single<PayShareResult<Tokens>> =
            Single.just(
                PayShareResult.Success(
                    data = expectedTokens
                )
            )
        Mockito.`when`(repository.auth(api = apiGoogle, token = token))
            .thenReturn(rxSingleTestTokens)
        AuthUseCase(repository)
            .invoke(api = apiGoogle, token = token).test().assertValue {
                val tokens = when (it) {
                    is PayShareResult.Success -> it.data
                    else -> throw IllegalStateException("Tokens null")
                }
                expectedTokens == tokens
            }
    }

    @Test
    fun `should return empty tokens`() {
        val emptyApi = ""
        val emptyToken = ""
        val expectedTokensEmpty = Tokens(accessToken = "", refreshToken = "")
        val rxSingleTestTokens: Single<PayShareResult<Tokens>> =
            Single.just(
                PayShareResult.Success(
                    data = expectedTokensEmpty
                )
            )
        Mockito.`when`(repository.auth(api = emptyApi, token = emptyToken))
            .thenReturn(rxSingleTestTokens)
        AuthUseCase(repository)
            .invoke(api = emptyApi, token = emptyToken).test().assertValue {
                val tokens = when (it) {
                    is PayShareResult.Success -> it.data
                    else -> throw IllegalStateException("Tokens null")
                }
                expectedTokensEmpty == tokens
            }
    }

    @Test
    fun `return connection error state`() {
        executeStateTest(
            PayShareResult.Error(error = EventError.ConnectionError)
        )
    }

    @Test
    fun `return connection error state Unknown`() {
        executeStateTest(
            PayShareResult.Error(error = EventError.UnknownError)
        )
    }

    private fun executeStateTest(stateType: PayShareResult<Tokens>) {
        val rxSingleTestTokens: Single<PayShareResult<Tokens>> =
            Single.just(stateType)
        Mockito.`when`(repository.auth(api = apiGoogle, token = token))
            .thenReturn(rxSingleTestTokens)
        AuthUseCase(repository)
            .invoke(api = apiGoogle, token = token).test().assertValue {
                it == stateType
            }
    }
}