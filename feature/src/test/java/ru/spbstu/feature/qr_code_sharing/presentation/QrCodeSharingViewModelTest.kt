package ru.spbstu.feature.qr_code_sharing.presentation

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.mock
import ru.spbstu.common.model.EventState
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.RxBeforeAllRule

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(RxBeforeAllRule::class)
class QrCodeSharingViewModelTest {
    private val router = mock<FeatureRouter>()
    private val viewModel = QrCodeSharingViewModel(router = router)

    @BeforeEach
    fun before() {
        viewModel.setEventState(EventState.Initial)
    }

    @Test
    fun `should setup correct room code`() {
        val expectedRoomCode = "123"

        viewModel.setupRoomCode(roomCode = expectedRoomCode)

        assertEquals(expectedRoomCode, viewModel.code)
    }
}