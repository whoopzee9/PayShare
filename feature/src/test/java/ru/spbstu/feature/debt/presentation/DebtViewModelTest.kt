package ru.spbstu.feature.debt.presentation

import io.reactivex.Scheduler
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.disposables.Disposable
import io.reactivex.internal.schedulers.ExecutorScheduler
import io.reactivex.plugins.RxJavaPlugins
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.mockito.kotlin.mock
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.domain.model.Event
import ru.spbstu.feature.domain.usecase.GetEventInfoUseCase
import ru.spbstu.feature.domain.usecase.JoinEventUseCase
import ru.spbstu.feature.domain.usecase.SetPurchasePaidUseCase
import ru.spbstu.feature.domain.usecase.ShowJoinEventUseCase
import ru.spbstu.feature.qr_code.presentation.QrCodeViewModel
import java.util.concurrent.TimeUnit

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DebtViewModelTest {
    private val router = mock<FeatureRouter>()
    private val getEventInfoUseCase = mock<GetEventInfoUseCase>()
    private val setPurchasePaidUseCase = mock<SetPurchasePaidUseCase>()
    private val viewModel = DebtViewModel(router, getEventInfoUseCase, setPurchasePaidUseCase)

    @BeforeAll
    fun setUp() {
        val immediate = object : Scheduler() {
            override fun createWorker(): Worker {
                return ExecutorScheduler.ExecutorWorker({ obj: Runnable -> obj.run() }, true)
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


}