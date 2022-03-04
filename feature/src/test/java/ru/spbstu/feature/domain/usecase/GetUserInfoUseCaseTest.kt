package ru.spbstu.feature.domain.usecase

import io.reactivex.Single
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.mock
import ru.spbstu.common.error.PayShareResult
import ru.spbstu.common.model.EventError
import ru.spbstu.feature.domain.model.User
import ru.spbstu.feature.domain.repository.FeatureRepository

class GetUserInfoUseCaseTest {
    private val repository = mock<FeatureRepository>()
    private val testUser = User(id = 1, firstName = "Georgy", "Tolstolbikov", "")
    private val rxSingleTestUser: Single<PayShareResult<User>> =
        Single.just(PayShareResult.Success(testUser))


    @Test
    fun `should return user info`() {
        Mockito.`when`(repository.getUserInfo()).thenReturn(rxSingleTestUser)
        GetUserInfoUseCase(repository)
            .invoke().test().assertValue {
                (it as PayShareResult.Success).data == testUser
            }
    }

    @Test
    fun `should return user info method 2`() {
        Mockito.`when`(repository.getUserInfo()).thenReturn(rxSingleTestUser)
        GetUserInfoUseCase(repository)
            .invoke().test().assertValue {
                val repoUser = when (it) {
                    is PayShareResult.Success -> it.data
                    else -> throw IllegalArgumentException("Not success status")
                }
                repoUser == testUser
            }
    }

    @Test
    fun `should return error`() {
        val rxSingleTestUser: Single<PayShareResult<User>> =
            Single.just(PayShareResult.Error(EventError.UnknownError))

        Mockito.`when`(repository.getUserInfo()).thenReturn(rxSingleTestUser)
        GetUserInfoUseCase(repository)
            .invoke().test().assertValue {
                val isErrorReturned = when (it) {
                    is PayShareResult.Success -> true
                    else -> false
                }
                !isErrorReturned
            }
    }
}
