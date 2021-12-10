package ru.spbstu.feature.debt.di

import androidx.fragment.app.Fragment
import dagger.BindsInstance
import dagger.Subcomponent
import ru.spbstu.common.di.scope.ScreenScope
import ru.spbstu.feature.debt.presentation.DebtFragment


@Subcomponent(
    modules = [
        DebtModule::class
    ]
)
@ScreenScope
interface DebtComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance fragment: Fragment): DebtComponent
    }

    fun inject(debtFragment: DebtFragment)
}