package ru.spbstu.feature.mapSelect.di

import androidx.fragment.app.Fragment
import dagger.BindsInstance
import dagger.Subcomponent
import ru.spbstu.common.di.scope.ScreenScope
import ru.spbstu.feature.mapSelect.presentation.ShopMapFragment

@Subcomponent(
    modules = [
        ShopMapModule::class,
    ]
)
@ScreenScope
interface ShopMapComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance fragment: Fragment): ShopMapComponent
    }

    fun inject(shopMapFragment: ShopMapFragment)
}
