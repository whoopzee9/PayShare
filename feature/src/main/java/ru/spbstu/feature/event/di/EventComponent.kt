package ru.spbstu.feature.event.di

import androidx.fragment.app.Fragment
import dagger.BindsInstance
import dagger.Subcomponent
import ru.spbstu.common.di.scope.ScreenScope
import ru.spbstu.feature.event.presentation.EventFragment

@Subcomponent(
    modules = [
        EventModule::class
    ]
)
@ScreenScope
interface EventComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance fragment: Fragment): EventComponent
    }

    fun inject(eventFragment: EventFragment)
}