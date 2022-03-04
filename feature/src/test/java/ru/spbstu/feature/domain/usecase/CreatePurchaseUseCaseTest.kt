package ru.spbstu.feature.domain.usecase

import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import ru.spbstu.feature.domain.repository.FeatureRepository

class CreatePurchaseUseCaseTest {
    val repository = mock<FeatureRepository>()

    @Test
    fun `should return created purchase id`() {
    }
}
