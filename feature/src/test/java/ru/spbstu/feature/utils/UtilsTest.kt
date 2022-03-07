package ru.spbstu.feature.utils

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import ru.spbstu.feature.domain.model.Expense
import ru.spbstu.feature.domain.model.User

class UtilsTest {

    @Test
    fun `should calculate participation in all items`() {
        val list = listOf(
            Expense(
                id = 1,
                users = mapOf(1L to false, 2L to false, 3L to false, 4L to false),
                buyer = User(id = 2),
                price = 100.0
            ),
            Expense(
                id = 2,
                users = mapOf(1L to false, 2L to false, 3L to false, 4L to false),
                buyer = User(id = 3),
                price = 200.0
            )
        )
        val debt = Utils.calculateTotalDebt(list, 1L)
        assertEquals(debt, 75.0)
    }

    @Test
    fun `should calculate no participation`() {
        val list = listOf(
            Expense(
                id = 1,
                users = mapOf(2L to false, 3L to false, 4L to false),
                buyer = User(id = 2),
                price = 100.0
            ),
            Expense(
                id = 2,
                users = mapOf(2L to false, 3L to false, 4L to false),
                buyer = User(id = 3),
                price = 200.0
            )
        )
        val debt = Utils.calculateTotalDebt(list, 1L)
        assertEquals(debt, 0.0)
    }

    @Test
    fun `should calculate debt in non-bought items`() {
        val list = listOf(
            Expense(
                id = 1,
                users = mapOf(1L to false, 2L to false, 3L to false, 4L to false),
                buyer = User(id = 2),
                price = 100.0
            ),
            Expense(
                id = 2,
                users = mapOf(1L to false, 2L to true, 3L to false, 4L to false),
                buyer = User(id = 1),
                price = 200.0
            )
        )
        val debt = Utils.calculateTotalDebt(list, 1L)
        assertEquals(debt, 25.0)
    }

    @Test
    fun `should calculate debt in only participated items`() {
        val list = listOf(
            Expense(
                id = 1,
                users = mapOf(2L to false, 3L to false, 4L to false),
                buyer = User(id = 2),
                price = 100.0
            ),
            Expense(
                id = 2,
                users = mapOf(1L to false, 2L to false, 3L to false, 4L to false),
                buyer = User(id = 3),
                price = 200.0
            )
        )
        val debt = Utils.calculateTotalDebt(list, 1L)
        assertEquals(debt, 50.0)
    }

    @Test
    fun `should calculate debt in only non-paid items`() {
        val list = listOf(
            Expense(
                id = 1,
                users = mapOf(1L to true, 2L to false, 3L to false, 4L to false),
                buyer = User(id = 2),
                price = 100.0
            ),
            Expense(
                id = 2,
                users = mapOf(1L to false, 2L to false, 3L to false, 4L to false),
                buyer = User(id = 3),
                price = 200.0
            )
        )
        val debt = Utils.calculateTotalDebt(list, 1L)
        assertEquals(debt, 50.0)
    }

    @Test
    fun `should calculate no debt because of all-paid items`() {
        val list = listOf(
            Expense(
                id = 1,
                users = mapOf(1L to true, 2L to false, 3L to false, 4L to false),
                buyer = User(id = 2),
                price = 100.0
            ),
            Expense(
                id = 2,
                users = mapOf(1L to true, 2L to false, 3L to false, 4L to false),
                buyer = User(id = 3),
                price = 200.0
            )
        )
        val debt = Utils.calculateTotalDebt(list, 1L)
        assertEquals(debt, 0.0)
    }
}