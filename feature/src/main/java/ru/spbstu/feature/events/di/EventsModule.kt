package ru.spbstu.feature.events.di

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
import ru.spbstu.feature.events.presentation.EventsViewModel


@Module(
    includes = [
        ViewModelModule::class
    ]
)
class EventsModule {

    @Provides
    @IntoMap
    @ViewModelKey(EventsViewModel::class)
    fun provideViewModel(router: FeatureRouter, bundleDataWrapper: BundleDataWrapper): ViewModel {
        return EventsViewModel(router, bundleDataWrapper)
    }

    @Provides
    fun provideViewModelCreator(
        fragment: Fragment,
        viewModelFactory: ViewModelProvider.Factory
    ): EventsViewModel {
        return ViewModelProvider(fragment, viewModelFactory).get(EventsViewModel::class.java)
    }
}