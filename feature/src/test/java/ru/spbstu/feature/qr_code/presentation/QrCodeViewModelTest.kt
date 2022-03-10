package ru.spbstu.feature.qr_code.presentation

import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.disposables.Disposable
import io.reactivex.internal.schedulers.ExecutorScheduler.ExecutorWorker
import io.reactivex.plugins.RxJavaPlugins
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.Mockito
import org.mockito.kotlin.mock
import ru.spbstu.common.error.PayShareResult
import ru.spbstu.common.model.EventError
import ru.spbstu.common.model.EventState
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.domain.model.Event
import ru.spbstu.feature.domain.usecase.JoinEventUseCase
import ru.spbstu.feature.domain.usecase.ShowJoinEventUseCase
import java.lang.Exception
import java.util.concurrent.TimeUnit

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class QrCodeViewModelTest {
    private val router = mock<FeatureRouter>()
    private val joinEventUseCase = mock<JoinEventUseCase>()
    private val showJoinEventUseCase = mock<ShowJoinEventUseCase>()
    private val viewModel = QrCodeViewModel(router, joinEventUseCase, showJoinEventUseCase)
    private val event = Event(id = 123)
    private val code = "12345"

    @BeforeAll
    fun setUp() {
        val immediate = object : Scheduler() {
            override fun createWorker(): Worker {
                return ExecutorWorker({ obj: Runnable -> obj.run() }, true)
            }

            override fun scheduleDirect(run: Runnable, delay: Long, unit: TimeUnit): Disposable {
                return super.scheduleDirect(run, 0, unit)
            }
        }
        RxJavaPlugins.setInitIoSchedulerHandler { immediate }
        RxJavaPlugins.setInitComputationSchedulerHandler { immediate }
        RxJavaPlugins.setInitNewThreadSchedulerHandler { immediate }
        RxJavaPlugins.setInitSingleSchedulerHandler { immediate }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { immediate }
    }

    @BeforeEach
    fun before() {
        viewModel.setEventState(EventState.Initial)
    }

    @Test
    fun `should successfully join event`() {
        val rxSingleTest: Single<PayShareResult<Any>> =
            Single.just(PayShareResult.Success(Any()))
        Mockito.`when`(joinEventUseCase.invoke(event.id)).thenReturn(rxSingleTest)

        viewModel.joinEvent(event)
        assertEquals(viewModel.eventState.value, EventState.Success)
    }

    @Test
    fun `should error on join event`() {
        val rxSingleTestError: Single<PayShareResult<Any>> =
            Single.just(PayShareResult.Error(EventError.UnknownError))
        Mockito.`when`(joinEventUseCase.invoke(event.id)).thenReturn(rxSingleTestError)

        viewModel.joinEvent(event)
        assertEquals(viewModel.eventState.value, EventState.Failure(EventError.UnknownError))
    }

    @Test
    fun `should throw exception on join event`() {
        val rxSingleTestError: Single<PayShareResult<Any>> =
            Single.error(Exception())
        Mockito.`when`(joinEventUseCase.invoke(event.id)).thenReturn(rxSingleTestError)

        viewModel.joinEvent(event)
        assertEquals(viewModel.eventState.value, EventState.Failure(EventError.ConnectionError))
    }

    @Test
    fun `should successfully show join event`() {
        val rxSingleTest: Single<PayShareResult<Event>> =
            Single.just(PayShareResult.Success(Event()))
        Mockito.`when`(showJoinEventUseCase.invoke(code)).thenReturn(rxSingleTest)

        viewModel.showJoinEvent(code) {}
        assertEquals(viewModel.eventState.value, EventState.Success)
    }

    @Test
    fun `should successfully fail show join event`() {
        val rxSingleTest: Single<PayShareResult<Event>> =
            Single.just(PayShareResult.Error(EventError.UnknownError))
        Mockito.`when`(showJoinEventUseCase.invoke(code)).thenReturn(rxSingleTest)

        viewModel.showJoinEvent(code) {}
        assertEquals(viewModel.eventState.value, EventState.Failure(EventError.UnknownError))
    }

    @Test
    fun `should throw exception on show join event`() {
        val rxSingleTestError: Single<PayShareResult<Event>> =
            Single.error(Exception())
        Mockito.`when`(showJoinEventUseCase.invoke(code)).thenReturn(rxSingleTestError)

        viewModel.showJoinEvent(code) {}
        assertEquals(viewModel.eventState.value, EventState.Failure(EventError.ConnectionError))
    }
}