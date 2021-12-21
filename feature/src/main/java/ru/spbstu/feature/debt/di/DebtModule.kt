package ru.spbstu.feature.debt.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import ru.spbstu.common.di.viewmodel.ViewModelKey
import ru.spbstu.common.di.viewmodel.ViewModelModule
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.debt.presentation.DebtViewModel
import ru.spbstu.feature.domain.usecase.GetEventInfoUseCase
import ru.spbstu.feature.domain.usecase.SetPurchasePaidUseCase


@Module(
    includes = [
        ViewModelModule::class
    ]
)
class DebtModule {

    @Provides
    @IntoMap
    @ViewModelKey(DebtViewModel::class)
    fun provideViewModel(
        router: FeatureRouter,
        getEventInfoUseCase: GetEventInfoUseCase,
        setPurchasePaidUseCase: SetPurchasePaidUseCase
    ): ViewModel {
        return DebtViewModel(router, getEventInfoUseCase, setPurchasePaidUseCase)
    }

    @Provides
    fun provideViewModelCreator(
        fragment: Fragment,
        viewModelFactory: ViewModelProvider.Factory
    ): DebtViewModel {
        return ViewModelProvider(fragment, viewModelFactory).get(DebtViewModel::class.java)
    }
}