package ru.spbstu.feature.expense.presentation

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
import ru.spbstu.feature.domain.DomainStubClasses
import ru.spbstu.feature.domain.model.EventInfo
import ru.spbstu.feature.domain.usecase.GetEventInfoUseCase

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(RxBeforeAllRule::class)
class ExpenseViewModelTest {
    private val router = mock<FeatureRouter>()
    private val getEventInfoUseCase = mock<GetEventInfoUseCase>()
    private val viewModel = ExpenseViewModel(router, getEventInfoUseCase = getEventInfoUseCase)

    private val roomId = 56954L
    private val eventInfo = DomainStubClasses.eventInfo
    private val expenseId = 23L

    @BeforeEach
    fun before() {
        viewModel.setEventState(EventState.Initial)
    }

    @Test
    fun `should return event info successfully`() {
        val rxSingleTest: Single<PayShareResult<EventInfo>> =
            Single.just(PayShareResult.Success(eventInfo))

        Mockito.`when`(getEventInfoUseCase.invoke(id = roomId)).thenReturn(rxSingleTest)

        viewModel.getData(roomId = roomId, expenseId = expenseId) {}

        assertEquals(EventState.Success, viewModel.eventState.value)
    }

    @Test
    fun `should return connection error when get event info`() {
        val rxSingleTest: Single<PayShareResult<EventInfo>> =
            Single.just(PayShareResult.Error(EventError.ConnectionError))

        Mockito.`when`(getEventInfoUseCase.invoke(id = roomId)).thenReturn(rxSingleTest)

        viewModel.getData(roomId = roomId, expenseId = expenseId) {}

        assertEquals(EventState.Failure(EventError.ConnectionError), viewModel.eventState.value)
    }

    @Test
    fun `should return failure when get event info`() {
        val rxSingleTest: Single<PayShareResult<EventInfo>> =
            Single.just(PayShareResult.Error(EventError.UnknownError))

        Mockito.`when`(getEventInfoUseCase.invoke(id = roomId)).thenReturn(rxSingleTest)

        viewModel.getData(roomId = roomId, expenseId = expenseId) {}

        assertEquals(EventState.Failure(EventError.UnknownError), viewModel.eventState.value)
    }
}