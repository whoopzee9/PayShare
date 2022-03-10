package ru.spbstu.feature.event.presentation

import io.reactivex.Single
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.kotlin.mock
import ru.spbstu.common.error.PayShareResult
import ru.spbstu.common.model.EventError
import ru.spbstu.common.model.EventState
import ru.spbstu.common.utils.BundleDataWrapper
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.RxBeforeAllRule
import ru.spbstu.feature.domain.DomainStubClasses
import ru.spbstu.feature.domain.model.EventInfo
import ru.spbstu.feature.domain.usecase.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(RxBeforeAllRule::class)
class EventViewModelTest {
    private val router = mock<FeatureRouter>()
    private val bundleDataWrapper = mock<BundleDataWrapper>()
    private val createPurchaseUseCase = mock<CreatePurchaseUseCase>()
    private val getEventInfoUseCase = mock<GetEventInfoUseCase>()
    private val getRoomCodeUseCase = mock<GetRoomCodeUseCase>()
    private val deletePurchaseUseCase = mock<DeletePurchaseUseCase>()
    private val deleteRoomUseCase = mock<DeleteRoomUseCase>()
    private val setPurchaseJoinUseCase = mock<SetPurchaseJoinUseCase>()

    private val viewModel = EventViewModel(
        router = router,
        bundleDataWrapper = bundleDataWrapper,
        createPurchaseUseCase = createPurchaseUseCase,
        getEventInfoUseCase = getEventInfoUseCase,
        getRoomCodeUseCase = getRoomCodeUseCase,
        deletePurchaseUseCase = deletePurchaseUseCase,
        deleteRoomUseCase = deleteRoomUseCase,
        setPurchaseJoinUseCase = setPurchaseJoinUseCase
    )

    private val eventId = 1332L
    private val roomId = 12L
    private val eventInfo = DomainStubClasses.eventInfo

    //for purchase test
    private val purchaseId = 132222L
    private val participantId = 9292L
    private val purchase = DomainStubClasses.expense

    @BeforeEach
    fun before() {
        viewModel.setEventState(EventState.Initial)
        viewModel.roomId = roomId
        viewModel.event = eventInfo
    }

    @Test
    fun `should successfully load purchases`() {
        val rxSingleTest: Single<PayShareResult<EventInfo>> =
            Single.just(PayShareResult.Success(EventInfo()))
        Mockito.`when`(getEventInfoUseCase.invoke(id = roomId)).thenReturn(rxSingleTest)

        viewModel.loadPurchases()

        assertEquals(viewModel.eventState.value, EventState.Success)
    }

    @Test
    fun `should return failure when load purchases`() {
        val rxSingleTest: Single<PayShareResult<EventInfo>> =
            Single.just(PayShareResult.Error(EventError.UnknownError))
        Mockito.`when`(getEventInfoUseCase.invoke(id = roomId)).thenReturn(rxSingleTest)

        viewModel.loadPurchases()

        assertEquals(viewModel.eventState.value, EventState.Failure(EventError.UnknownError))
    }

    @Test
    fun `should return connection error when load purchases`() {
        val rxSingleTest: Single<PayShareResult<EventInfo>> =
            Single.just(PayShareResult.Error(EventError.ConnectionError))
        Mockito.`when`(getEventInfoUseCase.invoke(id = roomId)).thenReturn(rxSingleTest)

        viewModel.loadPurchases()

        assertEquals(viewModel.eventState.value, EventState.Failure(EventError.ConnectionError))
    }

    @Test
    fun `should successfully delete room`() {
        val rxSingleTest: Single<PayShareResult<Any>> =
            Single.just(PayShareResult.Success(Any()))
        Mockito.`when`(deleteRoomUseCase.invoke(roomId = roomId)).thenReturn(rxSingleTest)

        viewModel.deleteRoom()

        assertEquals(viewModel.eventState.value, EventState.Success)
    }

    @Test
    fun `should return failure when delete room`() {
        val rxSingleTest: Single<PayShareResult<Any>> =
            Single.just(PayShareResult.Error(EventError.UnknownError))
        Mockito.`when`(deleteRoomUseCase.invoke(roomId = roomId)).thenReturn(rxSingleTest)

        viewModel.deleteRoom()

        assertEquals(viewModel.eventState.value, EventState.Failure(EventError.UnknownError))
    }

    @Test
    fun `should return connection error when delete room`() {
        val rxSingleTest: Single<PayShareResult<Any>> =
            Single.just(PayShareResult.Error(EventError.ConnectionError))
        Mockito.`when`(deleteRoomUseCase.invoke(roomId = roomId)).thenReturn(rxSingleTest)

        viewModel.deleteRoom()

        assertEquals(viewModel.eventState.value, EventState.Failure(EventError.ConnectionError))
    }

    //TODO why NPE
/*    @Test
    fun `should return successfully set bought status`() {
        val rxSingleTest: Single<PayShareResult<Any>> =
            Single.just(PayShareResult.Success(purchase))
        Mockito.`when`(
            setPurchaseJoinUseCase.invoke(
                roomId = roomId,
                purchaseId = purchase.id,
                participantId = eventInfo.yourParticipantId,
                isJoined = true
            )
        ).thenReturn(rxSingleTest)

        viewModel.setBoughtStatus(purchase = purchase, isClicked = true)

        assertEquals(viewModel.eventState.value, EventState.Success)
    }*/
}