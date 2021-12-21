package ru.spbstu.feature.qr_code.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import ru.spbstu.common.di.viewmodel.ViewModelKey
import ru.spbstu.common.di.viewmodel.ViewModelModule
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.domain.usecase.JoinEventUseCase
import ru.spbstu.feature.domain.usecase.ShowJoinEventUseCase
import ru.spbstu.feature.qr_code.presentation.QrCodeViewModel


@Module(
    includes = [
        ViewModelModule::class
    ]
)
class QrCodeModule {

    @Provides
    @IntoMap
    @ViewModelKey(QrCodeViewModel::class)
    fun provideViewModel(
        router: FeatureRouter,
        joinEventUseCase: JoinEventUseCase,
        showJoinEventUseCase: ShowJoinEventUseCase
    ): ViewModel {
        return QrCodeViewModel(router, joinEventUseCase, showJoinEventUseCase)
    }

    @Provides
    fun provideViewModelCreator(
        fragment: Fragment,
        viewModelFactory: ViewModelProvider.Factory
    ): QrCodeViewModel {
        return ViewModelProvider(fragment, viewModelFactory).get(QrCodeViewModel::class.java)
    }
}