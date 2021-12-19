package ru.spbstu.feature.qr_code.di

import androidx.fragment.app.Fragment
import dagger.BindsInstance
import dagger.Subcomponent
import ru.spbstu.common.di.scope.ScreenScope
import ru.spbstu.feature.qr_code.presentation.QrCodeFragment


@Subcomponent(
    modules = [
        QrCodeModule::class
    ]
)
@ScreenScope
interface QrCodeComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance fragment: Fragment): QrCodeComponent
    }

    fun inject(qrCodeFragment: QrCodeFragment)
}