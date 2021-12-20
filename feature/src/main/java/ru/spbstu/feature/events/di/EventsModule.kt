package ru.spbstu.feature.events.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import ru.spbstu.common.di.viewmodel.ViewModelKey
import ru.spbstu.common.di.viewmodel.ViewModelModule
import ru.spbstu.common.token.TokenRepository
import ru.spbstu.common.utils.BundleDataWrapper
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.domain.usecase.CreateEventUseCase
import ru.spbstu.feature.domain.usecase.GetEventsUseCase
import ru.spbstu.feature.domain.usecase.JoinEventUseCase
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
    fun provideViewModel(
        router: FeatureRouter,
        bundleDataWrapper: BundleDataWrapper,
        tokenRepository: TokenRepository,
        getEventsUseCase: GetEventsUseCase,
        createEventUseCase: CreateEventUseCase,
        joinEventUseCase: JoinEventUseCase
    ): ViewModel {
        return EventsViewModel(
            router,
            bundleDataWrapper,
            tokenRepository,
            getEventsUseCase,
            createEventUseCase,
            joinEventUseCase
        )
    }

    @Provides
    fun provideViewModelCreator(
        fragment: Fragment,
        viewModelFactory: ViewModelProvider.Factory
    ): EventsViewModel {
        return ViewModelProvider(fragment, viewModelFactory).get(EventsViewModel::class.java)
    }
}