package ru.spbstu.feature.login.presentation

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import io.reactivex.Single
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.kotlin.mock
import ru.spbstu.common.error.PayShareResult
import ru.spbstu.common.model.EventState
import ru.spbstu.common.token.TokenRepository
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.RxBeforeAllRule
import ru.spbstu.feature.domain.model.Tokens
import ru.spbstu.feature.domain.usecase.AuthUseCase

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(RxBeforeAllRule::class)
class LoginViewModelTest {
    private val router = mock<FeatureRouter>()
    private val authUseCase = mock<AuthUseCase>()
    private val tokenRepository = mock<TokenRepository>()
    private val viewModel = LoginViewModel(
        router = router,
        authUseCase = authUseCase,
        tokenRepository = tokenRepository
    )
    private val apiGoogle = "google_api"
    private val apiVk = "vk_api"
    private val token = "some_token11-23-3-23-3-1"
    private val testTokens =
        Tokens(accessToken = "test_access_token1212", refreshToken = "test_refresh_token_er343")

    private val googleAccount = mock<GoogleSignInAccount>()

    @BeforeEach
    fun before() {
        viewModel.setEventState(EventState.Initial)
    }
/*    @Test
    fun `should successfully auth with google`() {
        val rxSingleTest: Single<PayShareResult<Tokens>> =
            Single.just(PayShareResult.Success(testTokens))

        Mockito.`when`(googleAccount.idToken).thenReturn(token)
        Mockito.`when`(authUseCase.invoke(api = "google", token = token)).thenReturn(rxSingleTest)

        viewModel.authWithGoogle(GoogleSignInAccount.createDefault())
        assertEquals(viewModel.eventState.value, EventState.Success)
    }*/
}