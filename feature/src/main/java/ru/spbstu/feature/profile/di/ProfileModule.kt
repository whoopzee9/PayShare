package ru.spbstu.feature.profile.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import ru.spbstu.common.di.viewmodel.ViewModelKey
import ru.spbstu.common.di.viewmodel.ViewModelModule
import ru.spbstu.common.token.TokenRepository
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.domain.usecase.GetUserInfoUseCase
import ru.spbstu.feature.domain.usecase.LogoutUseCase
import ru.spbstu.feature.profile.presentation.ProfileViewModel


@Module(
    includes = [
        ViewModelModule::class
    ]
)
class ProfileModule {

    @Provides
    @IntoMap
    @ViewModelKey(ProfileViewModel::class)
    fun provideViewModel(
        router: FeatureRouter,
        logoutUseCase: LogoutUseCase,
        getUserInfoUseCase: GetUserInfoUseCase,
        tokenRepository: TokenRepository
    ): ViewModel {
        return ProfileViewModel(router, logoutUseCase, getUserInfoUseCase, tokenRepository)
    }

    @Provides
    fun provideViewModelCreator(
        fragment: Fragment,
        viewModelFactory: ViewModelProvider.Factory
    ): ProfileViewModel {
        return ViewModelProvider(fragment, viewModelFactory).get(ProfileViewModel::class.java)
    }
}