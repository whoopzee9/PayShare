package ru.spbstu.feature.mapSelect.presentation

import com.google.android.gms.maps.model.LatLng
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.mock
import ru.spbstu.common.utils.BundleDataWrapper
import ru.spbstu.feature.RxBeforeAllRule


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(RxBeforeAllRule::class)
class ShopMapViewModelTest {
    private val dataWrapper = mock<BundleDataWrapper>()
    private val viewModel = ShopMapViewModel(dataWrapper = dataWrapper)
    private val testCoordinates: LatLng = LatLng(37.45, 36.77)

    @Test
    fun `should return same latLng`() {
        viewModel.setCoordinates(testCoordinates)

        assertEquals(testCoordinates, LatLng(viewModel.shop.latitude, viewModel.shop.longitude))
    }

    @Test
    fun `should return incorrect latLng because test incorrect`() {
        viewModel.setCoordinates(testCoordinates)

        assertNotEquals(
            testCoordinates,
            LatLng(viewModel.shop.latitude, viewModel.shop.longitude + 5)
        )
    }

    @Test
    fun `should correct set shop name`() {
        val shopName = "Raduga"

        viewModel.setName(shopName)

        assertEquals(shopName, viewModel.shop.name)
    }
}