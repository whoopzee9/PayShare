package ru.spbstu.feature.profile.di

import androidx.fragment.app.Fragment
import dagger.BindsInstance
import dagger.Subcomponent
import ru.spbstu.common.di.scope.ScreenScope
import ru.spbstu.feature.profile.presentation.ProfileFragment


@Subcomponent(
    modules = [
        ProfileModule::class
    ]
)
@ScreenScope
interface ProfileComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance fragment: Fragment): ProfileComponent
    }

    fun inject(profileFragment: ProfileFragment)
}