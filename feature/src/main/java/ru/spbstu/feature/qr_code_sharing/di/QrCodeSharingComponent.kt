package ru.spbstu.feature.qr_code_sharing.di

import androidx.fragment.app.Fragment
import dagger.BindsInstance
import dagger.Subcomponent
import ru.spbstu.common.di.scope.ScreenScope
import ru.spbstu.feature.qr_code_sharing.presentation.QrCodeSharingFragment

@Subcomponent(
    modules = [
        QrCodeSharingModule::class
    ]
)
@ScreenScope
interface QrCodeSharingComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance fragment: Fragment): QrCodeSharingComponent
    }

    fun inject(qrCodeSharingFragment: QrCodeSharingFragment)
}
