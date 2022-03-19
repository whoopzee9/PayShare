package ru.spbstu.feature.utils

import ru.spbstu.feature.domain.model.Expense

object Utils {

    fun calculateTotalDebt(list: List<Expense>, yourParticipantId: Long): Double {
        var total = 0.0
        list.forEach { expense ->
            if (expense.users.containsKey(yourParticipantId) &&
                expense.users[yourParticipantId] == false &&
                expense.buyer.id != yourParticipantId) {
                total += expense.price / expense.users.size
            }
        }
        return total
    }

    fun calculateTotalPrice(list: List<Expense>): Double {
        var totalPrice = 0.0
        list.forEach { totalPrice += it.price }
        return totalPrice
    }
}