package ru.spbstu.feature.history.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import ru.spbstu.common.di.viewmodel.ViewModelKey
import ru.spbstu.common.di.viewmodel.ViewModelModule
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.domain.usecase.GetHistoryUseCase
import ru.spbstu.feature.history.presentation.HistoryViewModel


@Module(
    includes = [
        ViewModelModule::class
    ]
)
class HistoryModule {

    @Provides
    @IntoMap
    @ViewModelKey(HistoryViewModel::class)
    fun provideViewModel(router: FeatureRouter, getHistoryUseCase: GetHistoryUseCase): ViewModel {
        return HistoryViewModel(router, getHistoryUseCase)
    }

    @Provides
    fun provideViewModelCreator(
        fragment: Fragment,
        viewModelFactory: ViewModelProvider.Factory
    ): HistoryViewModel {
        return ViewModelProvider(fragment, viewModelFactory).get(HistoryViewModel::class.java)
    }
}