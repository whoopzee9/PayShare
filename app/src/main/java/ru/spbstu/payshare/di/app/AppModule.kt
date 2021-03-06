package ru.spbstu.payshare.di.app

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.spbstu.common.di.scope.ApplicationScope
import ru.spbstu.payshare.App

@Module
class AppModule {

    @Provides
    @ApplicationScope
    fun provideContext(application: App): Context {
        return application
    }
}
