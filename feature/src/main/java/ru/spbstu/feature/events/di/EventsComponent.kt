package ru.spbstu.feature.events.di

import androidx.fragment.app.Fragment
import dagger.BindsInstance
import dagger.Subcomponent
import ru.spbstu.common.di.scope.ScreenScope
import ru.spbstu.feature.events.presentation.EventsFragment


@Subcomponent(
    modules = [
        EventsModule::class
    ]
)
@ScreenScope
interface EventsComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance fragment: Fragment): EventsComponent
    }

    fun inject(eventsFragment: EventsFragment)
}