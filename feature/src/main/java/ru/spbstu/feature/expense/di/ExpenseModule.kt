package ru.spbstu.feature.expense.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import ru.spbstu.common.di.viewmodel.ViewModelKey
import ru.spbstu.common.di.viewmodel.ViewModelModule
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.domain.usecase.GetEventInfoUseCase
import ru.spbstu.feature.expense.presentation.ExpenseViewModel


@Module(
    includes = [
        ViewModelModule::class
    ]
)
class ExpenseModule {

    @Provides
    @IntoMap
    @ViewModelKey(ExpenseViewModel::class)
    fun provideViewModel(
        router: FeatureRouter,
        getEventInfoUseCase: GetEventInfoUseCase
    ): ViewModel {
        return ExpenseViewModel(router, getEventInfoUseCase)
    }

    @Provides
    fun provideViewModelCreator(
        fragment: Fragment,
        viewModelFactory: ViewModelProvider.Factory
    ): ExpenseViewModel {
        return ViewModelProvider(fragment, viewModelFactory).get(ExpenseViewModel::class.java)
    }
}