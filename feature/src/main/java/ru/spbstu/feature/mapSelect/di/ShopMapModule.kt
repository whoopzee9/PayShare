package ru.spbstu.feature.mapSelect.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import ru.spbstu.common.di.viewmodel.ViewModelKey
import ru.spbstu.common.di.viewmodel.ViewModelModule
import ru.spbstu.common.utils.BundleDataWrapper
import ru.spbstu.feature.mapSelect.ShopMapViewModel

@Module(
    includes = [
        ViewModelModule::class
    ]
)
class ShopMapModule {

    @Provides
    @IntoMap
    @ViewModelKey(ShopMapViewModel::class)
    fun provideViewModel(dataWrapper: BundleDataWrapper): ViewModel {
        return ShopMapViewModel(dataWrapper)
    }

    @Provides
    fun provideViewModelCreator(
        fragment: Fragment,
        viewModelFactory: ViewModelProvider.Factory
    ): ShopMapViewModel {
        return ViewModelProvider(
            fragment,
            viewModelFactory
        ).get(ShopMapViewModel::class.java)
    }
}
