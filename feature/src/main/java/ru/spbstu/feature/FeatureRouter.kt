package ru.spbstu.feature

import ru.spbstu.common.base.BaseBackRouter
import ru.spbstu.feature.domain.model.Event

interface FeatureRouter : BaseBackRouter {
    fun openMainFragment()
    fun openEventFragment(event: Event)
}
