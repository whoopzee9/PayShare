package ru.spbstu.feature.debt.presentation

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
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.RxBeforeAllRule
import ru.spbstu.feature.domain.model.EventInfo
import ru.spbstu.feature.domain.model.Expense
import ru.spbstu.feature.domain.usecase.GetEventInfoUseCase
import ru.spbstu.feature.domain.usecase.SetPurchasePaidUseCase

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(RxBeforeAllRule::class)
class DebtViewModelTest {
    private val router = mock<FeatureRouter>()
    private val getEventInfoUseCase = mock<GetEventInfoUseCase>()
    private val setPurchasePaidUseCase = mock<SetPurchasePaidUseCase>()
    private val viewModel = DebtViewModel(router, getEventInfoUseCase, setPurchasePaidUseCase)
    private val roomId = 123L
    private val purchaseId = 12L
    private val participantId = 13L
    private val isPaid = false
    private val eventInfo = EventInfo(1, 1)

    @BeforeEach
    fun before() {
        viewModel.setEventState(EventState.Initial)
    }

    @Test
    fun `should error on get event`() {
        viewModel.setDebts(EventInfo())
        val rxSingleTestError: Single<PayShareResult<EventInfo>> =
            Single.just(PayShareResult.Error(EventError.UnknownError))
        Mockito.`when`(getEventInfoUseCase.invoke(roomId)).thenReturn(rxSingleTestError)

        viewModel.getData(roomId)
        assertEquals(viewModel.eventState.value, EventState.Failure(EventError.UnknownError))
        assertEquals(viewModel.debts.value, EventInfo())
    }

    @Test
    fun `should throw exception on get event`() {
        viewModel.setDebts(EventInfo())
        val rxSingleTestError: Single<PayShareResult<EventInfo>> =
            Single.error(Exception())
        Mockito.`when`(getEventInfoUseCase.invoke(roomId)).thenReturn(rxSingleTestError)

        viewModel.getData(roomId)
        assertEquals(viewModel.eventState.value, EventState.Failure(EventError.ConnectionError))
        assertEquals(viewModel.debts.value, EventInfo())
    }

    @Test
    fun `should successfully get event`() {
        val rxSingleTest: Single<PayShareResult<EventInfo>> =
            Single.just(PayShareResult.Success(eventInfo))

        Mockito.`when`(getEventInfoUseCase.invoke(roomId)).thenReturn(rxSingleTest)

        viewModel.getData(roomId)
        assertEquals(viewModel.eventState.value, EventState.Success)
        assertEquals(viewModel.debts.value, eventInfo)
    }

    @Test
    fun `should successfully set purchase paid`() {
        viewModel.setDebts(EventInfo(yourParticipantId = participantId))
        viewModel.roomId = roomId
        val rxSingleTest: Single<PayShareResult<Any>> =
            Single.just(PayShareResult.Success(Any()))

        Mockito.`when`(setPurchasePaidUseCase.invoke(roomId, purchaseId, participantId, isPaid))
            .thenReturn(rxSingleTest)

        val rxSingleTest2: Single<PayShareResult<EventInfo>> =
            Single.just(PayShareResult.Success(eventInfo))
        Mockito.`when`(getEventInfoUseCase.invoke(roomId)).thenReturn(rxSingleTest2)

        viewModel.onDebtChecked(Expense(id = purchaseId), isPaid)
        assertEquals(viewModel.eventState.value, EventState.Success)
    }

    @Test
    fun `should error on set purchase paid`() {
        viewModel.setDebts(EventInfo(yourParticipantId = participantId))
        viewModel.roomId = roomId
        val rxSingleTestError: Single<PayShareResult<Any>> =
            Single.just(PayShareResult.Error(EventError.UnknownError))
        Mockito.`when`(setPurchasePaidUseCase.invoke(roomId, purchaseId, participantId, isPaid))
            .thenReturn(rxSingleTestError)

        viewModel.onDebtChecked(Expense(id = purchaseId), isPaid)
        assertEquals(viewModel.eventState.value, EventState.Failure(EventError.UnknownError))
    }

    @Test
    fun `should throw exception on set purchase paid`() {
        viewModel.setDebts(EventInfo(yourParticipantId = participantId))
        viewModel.roomId = roomId
        val rxSingleTestError: Single<PayShareResult<Any>> =
            Single.error(Exception())
        Mockito.`when`(setPurchasePaidUseCase.invoke(roomId, purchaseId, participantId, isPaid))
            .thenReturn(rxSingleTestError)

        viewModel.onDebtChecked(Expense(id = purchaseId), isPaid)
        assertEquals(viewModel.eventState.value, EventState.Failure(EventError.ConnectionError))
    }
}