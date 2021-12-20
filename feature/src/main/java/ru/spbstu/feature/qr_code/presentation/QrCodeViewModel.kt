package ru.spbstu.feature.qr_code.presentation

import ru.spbstu.common.utils.BackViewModel
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.domain.model.Event

class QrCodeViewModel(val router: FeatureRouter) : BackViewModel(router) {

    fun getEventInfo(barCode:String, callback: (Event) -> Unit) {
        callback(Event(1, "sdfsdf"))
    }

    fun openEventFragment(id: Long) {
        router.openEventFragment(id)
    }
}