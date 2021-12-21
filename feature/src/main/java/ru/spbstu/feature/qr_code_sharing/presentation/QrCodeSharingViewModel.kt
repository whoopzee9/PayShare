package ru.spbstu.feature.qr_code_sharing.presentation

import ru.spbstu.common.utils.BackViewModel
import ru.spbstu.feature.FeatureRouter

class QrCodeSharingViewModel(val router: FeatureRouter) : BackViewModel(router) {
    var code = ""
    fun setupRoomCode(roomCode: String) {
        code = roomCode
    }
}
