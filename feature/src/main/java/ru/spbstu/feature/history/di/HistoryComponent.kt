package ru.spbstu.feature.history.di

import androidx.fragment.app.Fragment
import dagger.BindsInstance
import dagger.Subcomponent
import ru.spbstu.common.di.scope.ScreenScope
import ru.spbstu.feature.history.presentation.HistoryFragment


@Subcomponent(
    modules = [
        HistoryModule::class
    ]
)
@ScreenScope
interface HistoryComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance fragment: Fragment): HistoryComponent
    }

    fun inject(historyFragment: HistoryFragment)
}