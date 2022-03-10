package ru.spbstu.feature.profile.presentation

import io.reactivex.Single
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.kotlin.mock
import ru.spbstu.common.error.PayShareResult
import ru.spbstu.common.model.EventError
import ru.spbstu.common.model.EventState
import ru.spbstu.common.token.RefreshToken
import ru.spbstu.common.token.TokenRepository
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.RxBeforeAllRule
import ru.spbstu.feature.domain.DomainStubClasses
import ru.spbstu.feature.domain.model.User
import ru.spbstu.feature.domain.usecase.GetUserInfoUseCase
import ru.spbstu.feature.domain.usecase.LogoutUseCase

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(RxBeforeAllRule::class)
class ProfileViewModelTest {
    private val router = mock<FeatureRouter>()
    private val logoutUseCase = mock<LogoutUseCase>()
    private val getUserInfoUseCase = mock<GetUserInfoUseCase>()
    private val tokenRepository = mock<TokenRepository>()
    private val viewModel =
        ProfileViewModel(router, logoutUseCase, getUserInfoUseCase, tokenRepository)
    private val refreshToken = "token"

    @BeforeEach
    fun before() {
        viewModel.setEventState(EventState.Initial)
    }

    @Test
    fun `should successfully logout`() {
        val rxSingleTest: Single<PayShareResult<Any>> =
            Single.just(PayShareResult.Success(Any()))
        Mockito.`when`(logoutUseCase.invoke(refreshToken)).thenReturn(rxSingleTest)
        Mockito.`when`(tokenRepository.getRefresh()).thenReturn(RefreshToken(refreshToken))

        viewModel.logout()
        assertEquals(viewModel.eventState.value, EventState.Success)
    }

    @Test
    fun `should error on logout`() {
        val rxSingleTestError: Single<PayShareResult<Any>> =
            Single.just(PayShareResult.Error(EventError.UnknownError))
        Mockito.`when`(logoutUseCase.invoke(refreshToken)).thenReturn(rxSingleTestError)
        Mockito.`when`(tokenRepository.getRefresh()).thenReturn(RefreshToken(refreshToken))

        viewModel.logout()
        assertEquals(
            viewModel.eventState.value,
            EventState.Failure(EventError.UnknownError)
        )
    }

    @Test
    fun `should throw exception on logout`() {
        val rxSingleTestError: Single<PayShareResult<Any>> =
            Single.error(Exception())
        Mockito.`when`(logoutUseCase.invoke(refreshToken)).thenReturn(rxSingleTestError)
        Mockito.`when`(tokenRepository.getRefresh()).thenReturn(RefreshToken(refreshToken))

        viewModel.logout()
        assertEquals(
            viewModel.eventState.value,
            EventState.Failure(EventError.ConnectionError)
        )
    }

    @Test
    fun `should successfully get user info`() {
        viewModel.setUser(User())
        val rxSingleTest: Single<PayShareResult<User>> =
            Single.just(PayShareResult.Success(DomainStubClasses.user))
        Mockito.`when`(getUserInfoUseCase.invoke()).thenReturn(rxSingleTest)

        viewModel.getUserInfo()
        assertEquals(viewModel.eventState.value, EventState.Success)
        assertEquals(viewModel.user.value, DomainStubClasses.user)
    }

    @Test
    fun `should error on get user info`() {
        viewModel.setUser(User())
        val rxSingleTestError: Single<PayShareResult<User>> =
            Single.just(PayShareResult.Error(EventError.UnknownError))
        Mockito.`when`(getUserInfoUseCase.invoke()).thenReturn(rxSingleTestError)

        viewModel.getUserInfo()
        assertEquals(
            viewModel.eventState.value,
            EventState.Failure(EventError.UnknownError)
        )
        assertEquals(viewModel.user.value, User())
    }

    @Test
    fun `should throw exception on get user info`() {
        viewModel.setUser(User())
        val rxSingleTestError: Single<PayShareResult<User>> =
            Single.error(Exception())
        Mockito.`when`(getUserInfoUseCase.invoke()).thenReturn(rxSingleTestError)

        viewModel.getUserInfo()
        assertEquals(
            viewModel.eventState.value,
            EventState.Failure(EventError.ConnectionError)
        )
        assertEquals(viewModel.user.value, User())
    }
}