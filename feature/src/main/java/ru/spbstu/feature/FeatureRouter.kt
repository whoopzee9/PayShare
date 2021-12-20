package ru.spbstu.feature

import ru.spbstu.common.base.BaseBackRouter
import ru.spbstu.feature.domain.model.Event
import ru.spbstu.feature.domain.model.Expense

interface FeatureRouter : BaseBackRouter {
    fun openMainFragment()
    fun openQrCodeFragment()
    fun openLoginFragment()
    fun openEventFragment(id: Long)
    fun openQrCodeSharingFragment(code: String)
    fun openExpenseFragment(expense: Expense)
}
