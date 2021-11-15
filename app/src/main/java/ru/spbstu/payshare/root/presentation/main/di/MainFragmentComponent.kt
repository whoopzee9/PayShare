package ru.spbstu.payshare.root.presentation.main.di

import androidx.fragment.app.FragmentActivity
import dagger.BindsInstance
import dagger.Subcomponent
import ru.spbstu.payshare.root.presentation.main.MainFragment
import ru.spbstu.common.di.scope.ScreenScope

@Subcomponent(
    modules = [
        MainFragmentModule::class
    ]
)
@ScreenScope
interface MainFragmentComponent {

    @Subcomponent.Factory
    interface Factory {

        fun create(
            @BindsInstance activity: FragmentActivity
        ): MainFragmentComponent
    }

    fun inject(fragment: MainFragment)
}
