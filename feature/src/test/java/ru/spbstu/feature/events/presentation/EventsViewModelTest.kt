package ru.spbstu.feature.events.presentation

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
import ru.spbstu.common.token.TokenRepository
import ru.spbstu.common.utils.BundleDataWrapper
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.RxBeforeAllRule
import ru.spbstu.feature.domain.DomainStubClasses
import ru.spbstu.feature.domain.model.Event
import ru.spbstu.feature.domain.usecase.CreateEventUseCase
import ru.spbstu.feature.domain.usecase.GetEventsUseCase
import ru.spbstu.feature.domain.usecase.JoinEventUseCase
import ru.spbstu.feature.domain.usecase.ShowJoinEventUseCase

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(RxBeforeAllRule::class)
class EventsViewModelTest {
    private val router = mock<FeatureRouter>()
    private val bundleDataWrapper = mock<BundleDataWrapper>()
    private val tokenRepository = mock<TokenRepository>()
    private val createEventUseCase = mock<CreateEventUseCase>()
    private val getEventsUseCase = mock<GetEventsUseCase>()
    private val joinEventUseCase = mock<JoinEventUseCase>()
    private val showJoinEventUseCase = mock<ShowJoinEventUseCase>()

    private val viewModel = EventsViewModel(
        router = router,
        bundleDataWrapper = bundleDataWrapper,
        tokenRepository = tokenRepository,
        getEventsUseCase = getEventsUseCase,
        createEventUseCase = createEventUseCase,
        joinEventUseCase = joinEventUseCase,
        showJoinEventUseCase = showJoinEventUseCase
    )

    private val eventName = "event1"
    private val date = "18.01.23 12:22"
    private val createdEventId = 12122122L
    private val event = DomainStubClasses.event
    private val eventJoinCode = "239991"

    @BeforeEach
    fun before() {
        viewModel.setEventState(EventState.Initial)
    }

    @Test
    fun `should successfully get events`() {
        val events = emptyList<Event>()
        val rxSingleTest: Single<PayShareResult<List<Event>>> =
            Single.just(PayShareResult.Success(events))

        Mockito.`when`(getEventsUseCase.invoke()).thenReturn(rxSingleTest)

        viewModel.getEvents()

        Assertions.assertEquals(viewModel.eventState.value, EventState.Success)
    }

    @Test
    fun `should return connection error when get events`() {
        val rxSingleTest: Single<PayShareResult<List<Event>>> =
            Single.just(PayShareResult.Error(EventError.ConnectionError))

        Mockito.`when`(getEventsUseCase.invoke()).thenReturn(rxSingleTest)

        viewModel.getEvents()

        Assertions.assertEquals(
            viewModel.eventState.value,
            EventState.Failure(EventError.ConnectionError)
        )
    }

    @Test
    fun `should return failure error when get events`() {
        val rxSingleTest: Single<PayShareResult<List<Event>>> =
            Single.just(PayShareResult.Error(EventError.UnknownError))

        Mockito.`when`(getEventsUseCase.invoke()).thenReturn(rxSingleTest)

        viewModel.getEvents()

        Assertions.assertEquals(
            viewModel.eventState.value,
            EventState.Failure(EventError.UnknownError)
        )
    }

    @Test
    fun `should successfully create event`() {
        val rxSingleTest: Single<PayShareResult<Long>> =
            Single.just(PayShareResult.Success(createdEventId))

        Mockito.`when`(createEventUseCase.invoke(name = eventName, date = date))
            .thenReturn(rxSingleTest)

        viewModel.createEvent(name = eventName, date = date) {}

        Assertions.assertEquals(viewModel.eventState.value, EventState.Success)
    }

    @Test
    fun `should return failure error when create event`() {
        val rxSingleTest: Single<PayShareResult<Long>> =
            Single.just(PayShareResult.Error(EventError.UnknownError))

        Mockito.`when`(createEventUseCase.invoke(name = eventName, date = date))
            .thenReturn(rxSingleTest)

        viewModel.createEvent(name = eventName, date = date) {}

        Assertions.assertEquals(
            viewModel.eventState.value,
            EventState.Failure(EventError.UnknownError)
        )
    }

    @Test
    fun `should return connection error when create event`() {
        val rxSingleTest: Single<PayShareResult<Long>> =
            Single.just(PayShareResult.Error(EventError.ConnectionError))

        Mockito.`when`(createEventUseCase.invoke(name = eventName, date = date))
            .thenReturn(rxSingleTest)

        viewModel.createEvent(name = eventName, date = date) {}

        Assertions.assertEquals(
            viewModel.eventState.value,
            EventState.Failure(EventError.ConnectionError)
        )
    }

    @Test
    fun `should successfully join to event`() {
        val rxSingleTest: Single<PayShareResult<Any>> =
            Single.just(PayShareResult.Success(Any()))

        Mockito.`when`(joinEventUseCase.invoke(id = event.id))
            .thenReturn(rxSingleTest)

        viewModel.joinEvent(event = event)

        Assertions.assertEquals(viewModel.eventState.value, EventState.Success)
    }

    @Test
    fun `should return error when join to event`() {
        val rxSingleTest: Single<PayShareResult<Any>> =
            Single.just(PayShareResult.Error(EventError.UnknownError))

        Mockito.`when`(joinEventUseCase.invoke(event.id)).thenReturn(rxSingleTest)

        viewModel.joinEvent(event = event)

        Assertions.assertEquals(
            viewModel.eventState.value,
            EventState.Failure(EventError.UnknownError)
        )
    }

    @Test
    fun `should return connection error when join to event`() {
        val rxSingleTest: Single<PayShareResult<Any>> =
            Single.just(PayShareResult.Error(EventError.ConnectionError))

        Mockito.`when`(joinEventUseCase.invoke(event.id)).thenReturn(rxSingleTest)

        viewModel.joinEvent(event = event)

        Assertions.assertEquals(
            viewModel.eventState.value,
            EventState.Failure(EventError.ConnectionError)
        )
    }

    @Test
    fun `should successfully show join event`() {
        val rxSingleTest: Single<PayShareResult<Event>> =
            Single.just(PayShareResult.Success(event))

        Mockito.`when`(showJoinEventUseCase.invoke(code = eventJoinCode))
            .thenReturn(rxSingleTest)

        viewModel.showJoinEvent(code = eventJoinCode) {}

        Assertions.assertEquals(viewModel.eventState.value, EventState.Success)
    }

    @Test
    fun `should return connection error when show join event`() {
        val rxSingleTest: Single<PayShareResult<Event>> =
            Single.just(PayShareResult.Error(EventError.ConnectionError))

        Mockito.`when`(showJoinEventUseCase.invoke(code = eventJoinCode))
            .thenReturn(rxSingleTest)

        viewModel.showJoinEvent(code = eventJoinCode) {}

        Assertions.assertEquals(
            viewModel.eventState.value,
            EventState.Failure(EventError.ConnectionError)
        )
    }

    @Test
    fun `should return failure when show join event`() {
        val rxSingleTest: Single<PayShareResult<Event>> =
            Single.just(PayShareResult.Error(EventError.UnknownError))

        Mockito.`when`(showJoinEventUseCase.invoke(code = eventJoinCode))
            .thenReturn(rxSingleTest)

        viewModel.showJoinEvent(code = eventJoinCode) {}

        Assertions.assertEquals(
            viewModel.eventState.value,
            EventState.Failure(EventError.UnknownError)
        )
    }

    @Test
    fun `should navigate to login screen`() {
        viewModel.openLogin()
        Mockito.verify(router).openLoginFragment()
    }

    @Test
    fun `should navigate to qr code scanner screen`() {
        viewModel.openQrCodeScanner()
        Mockito.verify(router).openQrCodeFragment()
    }

    @Test
    fun `should navigate to event screen`() {
        viewModel.openEvent(id = event.id, title = event.name)
        Mockito.verify(router).openEventFragment(id = event.id, title = event.name)
    }
}