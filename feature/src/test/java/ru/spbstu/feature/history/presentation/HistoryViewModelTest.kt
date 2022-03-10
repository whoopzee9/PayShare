package ru.spbstu.feature.history.presentation

import io.reactivex.Single
import org.junit.jupiter.api.Assertions
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
import ru.spbstu.feature.domain.model.Event
import ru.spbstu.feature.domain.usecase.GetHistoryUseCase

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(RxBeforeAllRule::class)
class HistoryViewModelTest {
    private val router = mock<FeatureRouter>()
    private val getHistoryUseCase = mock<GetHistoryUseCase>()
    private val viewModel = HistoryViewModel(router, getHistoryUseCase = getHistoryUseCase)
    private val event = Event(id = 123)
    private val code = "12345"

    @BeforeEach
    fun before() {
        viewModel.setEventState(EventState.Initial)
    }

    @Test
    fun `should successfully load history`() {
        val events = emptyList<Event>()
        val rxSingleTest: Single<PayShareResult<List<Event>>> =
            Single.just(PayShareResult.Success(events))

        Mockito.`when`(getHistoryUseCase.invoke()).thenReturn(rxSingleTest)

        viewModel.getEvents()
        Assertions.assertEquals(viewModel.eventState.value, EventState.Success)
    }

    @Test
    fun `should return connection error when load history`() {
        val rxSingleTest: Single<PayShareResult<List<Event>>> =
            Single.just(PayShareResult.Error(EventError.ConnectionError))

        Mockito.`when`(getHistoryUseCase.invoke()).thenReturn(rxSingleTest)

        viewModel.getEvents()
        Assertions.assertEquals(
            viewModel.eventState.value,
            EventState.Failure(EventError.ConnectionError)
        )
    }

    @Test
    fun `should return failure when load history`() {
        val rxSingleTest: Single<PayShareResult<List<Event>>> =
            Single.just(PayShareResult.Error(EventError.UnknownError))

        Mockito.`when`(getHistoryUseCase.invoke()).thenReturn(rxSingleTest)

        viewModel.getEvents()
        Assertions.assertEquals(
            viewModel.eventState.value,
            EventState.Failure(EventError.UnknownError)
        )
    }

    @Test
    fun `should navigate to event screen`() {
        viewModel.openEvent(id = event.id, title = event.name)
        Mockito.verify(router).openEventFragment(id = event.id, title = event.name)
    }
}
