package ru.spbstu.feature.login.presentation

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import ru.spbstu.common.utils.BackViewModel
import ru.spbstu.feature.FeatureRouter

class LoginViewModel(router: FeatureRouter) : BackViewModel(router) {

    fun authWithGoogle(account: GoogleSignInAccount) {
        //TODO send token to back
    }
}