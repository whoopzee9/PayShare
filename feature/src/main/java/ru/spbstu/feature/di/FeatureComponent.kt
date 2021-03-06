package ru.spbstu.feature.di

import dagger.BindsInstance
import dagger.Component
import ru.spbstu.common.di.CommonApi
import ru.spbstu.common.di.scope.FeatureScope
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.calendar.di.CalendarComponent
import ru.spbstu.feature.debt.di.DebtComponent
import ru.spbstu.feature.event.di.EventComponent
import ru.spbstu.feature.events.di.EventsComponent
import ru.spbstu.feature.expense.di.ExpenseComponent
import ru.spbstu.feature.history.di.HistoryComponent
import ru.spbstu.feature.login.di.LoginComponent
import ru.spbstu.feature.mapSelect.di.ShopMapComponent
import ru.spbstu.feature.profile.di.ProfileComponent
import ru.spbstu.feature.qr_code.di.QrCodeComponent
import ru.spbstu.feature.qr_code_sharing.di.QrCodeSharingComponent

@Component(
    dependencies = [
        FeatureDependencies::class,
    ],
    modules = [
        FeatureModule::class,
        FeatureDataModule::class
    ]
)
@FeatureScope
interface FeatureComponent {

    fun loginComponentFactory(): LoginComponent.Factory
    fun eventsComponentFactory(): EventsComponent.Factory
    fun eventComponentFactory(): EventComponent.Factory
    fun expenseComponentFactory(): ExpenseComponent.Factory
    fun debtComponentFactory(): DebtComponent.Factory
    fun historyComponentFactory(): HistoryComponent.Factory
    fun profileComponentFactory(): ProfileComponent.Factory
    fun calendarComponentFactory(): CalendarComponent.Factory
    fun qrCodeComponentFactory(): QrCodeComponent.Factory
    fun qrCodeSharingComponentFactory(): QrCodeSharingComponent.Factory
    fun shopMapComponentFactory(): ShopMapComponent.Factory

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance featureRouter: FeatureRouter,
            deps: FeatureDependencies
        ): FeatureComponent
    }

    @Component(
        dependencies = [
            CommonApi::class,
        ]
    )
    interface FeatureDependenciesComponent : FeatureDependencies
}
