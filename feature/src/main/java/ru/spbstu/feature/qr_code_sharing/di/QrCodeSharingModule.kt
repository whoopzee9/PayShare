package ru.spbstu.feature.qr_code_sharing.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import ru.spbstu.common.di.viewmodel.ViewModelKey
import ru.spbstu.common.di.viewmodel.ViewModelModule
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.qr_code_sharing.presentation.QrCodeSharingViewModel

@Module(
    includes = [
        ViewModelModule::class
    ]
)
class QrCodeSharingModule {
    @Provides
    @IntoMap
    @ViewModelKey(QrCodeSharingViewModel::class)
    fun provideViewModel(
        navigator: FeatureRouter,
    ): ViewModel {
        return QrCodeSharingViewModel(navigator)
    }

    @Provides
    fun provideViewModelCreator(
        fragment: Fragment,
        viewModelFactory: ViewModelProvider.Factory
    ): QrCodeSharingViewModel {
        return ViewModelProvider(fragment, viewModelFactory).get(QrCodeSharingViewModel::class.java)
    }
}
