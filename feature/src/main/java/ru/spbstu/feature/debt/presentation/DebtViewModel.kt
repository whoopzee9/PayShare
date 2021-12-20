package ru.spbstu.feature.debt.presentation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.spbstu.common.utils.BackViewModel
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.domain.model.Expense


class DebtViewModel(router: FeatureRouter) : BackViewModel(router) {
    private val _debts: MutableStateFlow<List<Expense>> = MutableStateFlow(listOf())
    val debts get() :StateFlow<List<Expense>> = _debts

    fun setDebts(debts: List<Expense>) {
        _debts.value = debts
    }
}