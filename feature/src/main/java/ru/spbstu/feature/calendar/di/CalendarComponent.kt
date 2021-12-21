package ru.spbstu.feature.calendar.di

import androidx.fragment.app.Fragment
import dagger.BindsInstance
import dagger.Subcomponent
import ru.spbstu.common.di.scope.ScreenScope
import ru.spbstu.feature.calendar.presentation.CalendarFragment

@Subcomponent(
    modules = [
        CalendarModule::class,
    ]
)
@ScreenScope
interface CalendarComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance fragment: Fragment): CalendarComponent
    }

    fun inject(calendarFragment: CalendarFragment)
}
