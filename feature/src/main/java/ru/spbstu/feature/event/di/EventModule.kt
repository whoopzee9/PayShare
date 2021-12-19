package ru.spbstu.feature.event.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import ru.spbstu.common.di.viewmodel.ViewModelKey
import ru.spbstu.common.di.viewmodel.ViewModelModule
import ru.spbstu.common.utils.BundleDataWrapper
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.event.presentation.EventViewModel

@Module(
    includes = [
        ViewModelModule::class
    ]
)
class EventModule {

    @Provides
    @IntoMap
    @ViewModelKey(EventViewModel::class)
    fun provideViewModel(router: FeatureRouter, bundleDataWrapper: BundleDataWrapper): ViewModel {
        return EventViewModel(router, bundleDataWrapper)
    }

    @Provides
    fun provideViewModelCreator(
        fragment: Fragment,
        viewModelFactory: ViewModelProvider.Factory
    ): EventViewModel {
        return ViewModelProvider(fragment, viewModelFactory).get(EventViewModel::class.java)
    }
}
