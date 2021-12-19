package ru.spbstu.feature.profile.presentation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.spbstu.common.utils.BackViewModel
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.domain.model.Event
import ru.spbstu.feature.domain.model.User

class ProfileViewModel(router: FeatureRouter) : BackViewModel(router) {
    private val _user: MutableStateFlow<User> = MutableStateFlow(User())
    val user get() :StateFlow<User> = _user

    fun getUserInfo() {

    }

    fun logOut() {

    }
}