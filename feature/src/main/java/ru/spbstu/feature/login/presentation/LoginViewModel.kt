package ru.spbstu.feature.login.presentation

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.vk.api.sdk.auth.VKAccessToken
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import ru.spbstu.common.error.PayShareResult
import ru.spbstu.common.model.EventError
import ru.spbstu.common.model.EventState
import ru.spbstu.common.token.TokenRepository
import ru.spbstu.common.utils.BackViewModel
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.domain.usecase.AuthUseCase
import timber.log.Timber

class LoginViewModel(
    private val router: FeatureRouter,
    private val authUseCase: AuthUseCase,
    private val tokenRepository: TokenRepository
) :
    BackViewModel(router) {


    fun authWithGoogle(account: GoogleSignInAccount) {
        auth("google", account.idToken ?: "")
    }

    fun authWithVK(token: VKAccessToken) {
        auth("vk", token.accessToken)
    }

    private fun auth(api: String, token: String) {
        setEventState(EventState.Progress)
        authUseCase.invoke(api, token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                when (it) {
                    is PayShareResult.Success -> {
                        tokenRepository.saveToken(it.data.accessToken)
                        tokenRepository.saveRefresh(it.data.refreshToken)
                        Timber.tag(TAG).d("Success login with tokens=$it")
                        router.openMainFragment()
                        setEventState(EventState.Success)
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

    private companion object {
        private val TAG = LoginViewModel::class.simpleName
    }
}