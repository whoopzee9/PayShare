package ru.spbstu.payshare.di.app

import dagger.BindsInstance
import dagger.Component
import ru.spbstu.common.di.CommonApi
import ru.spbstu.common.di.modules.CommonModule
import ru.spbstu.common.di.modules.NetworkModule
import ru.spbstu.common.di.scope.ApplicationScope
import ru.spbstu.payshare.di.deps.ComponentHolderModule
import ru.spbstu.payshare.App

@ApplicationScope
@Component(
    modules = [
        AppModule::class,
        CommonModule::class,
        NetworkModule::class,
        ComponentHolderModule::class,
        FeatureManagerModule::class,
        NavigationModule::class
    ]
)
interface AppComponent : CommonApi {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: App): Builder

        fun build(): AppComponent
    }

    fun inject(app: App)
}
