package ru.spbstu.feature.expense.di

import androidx.fragment.app.Fragment
import dagger.BindsInstance
import dagger.Subcomponent
import ru.spbstu.common.di.scope.ScreenScope
import ru.spbstu.feature.expense.presentation.ExpenseFragment


@Subcomponent(
    modules = [
        ExpenseModule::class
    ]
)
@ScreenScope
interface ExpenseComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance fragment: Fragment): ExpenseComponent
    }

    fun inject(expenseFragment: ExpenseFragment)
}