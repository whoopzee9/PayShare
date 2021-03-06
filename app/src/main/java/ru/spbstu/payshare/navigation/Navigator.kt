package ru.spbstu.payshare.navigation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import org.greenrobot.eventbus.EventBus
import ru.spbstu.common.events.SetBottomNavVisibility
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.debt.presentation.DebtFragment
import ru.spbstu.feature.domain.model.Expense
import ru.spbstu.feature.event.presentation.EventFragment
import ru.spbstu.feature.expense.presentation.ExpenseFragment
import ru.spbstu.feature.qr_code_sharing.presentation.QrCodeSharingFragment
import ru.spbstu.payshare.R
import timber.log.Timber

class Navigator : FeatureRouter {

    private var navController: NavController? = null
    private var activity: AppCompatActivity? = null

    override fun back() {
        val popped = navController!!.popBackStack()

        if (!popped) {
            activity!!.finish()
        }
    }

    fun attachActivity(activity: AppCompatActivity) {
        this.activity = activity
    }

    fun attachNavController(navController: NavController) {
        this.navController = navController.apply {
            this.addOnDestinationChangedListener { _: NavController, _: NavDestination, _: Bundle? ->
                checkBottomBar()
            }
        }
    }

    fun detachActivity() {
        activity = null
    }

    fun detachNavController() {
        navController = null
    }

    fun clearBackStackAndOpenLogin() {
        if (navController?.currentDestination?.id == R.id.loginFragment) {
            return
        }
        while (navController?.popBackStack() == true) {
            Timber.tag(TAG).d("Skipped backstack entry")
        }
        navController?.navigate(R.id.open_login_fragment)
    }

    override fun openMainFragment() {
        when (navController?.currentDestination?.id) {
            R.id.loginFragment -> navController?.navigate(R.id.action_loginFragment_to_eventsFragment)
        }
    }

    override fun openQrCodeFragment() {
        when (navController?.currentDestination?.id) {
            R.id.eventsFragment -> navController?.navigate(R.id.action_eventsFragment_to_qrCodeFragment)
        }
    }

    override fun openLoginFragment() {
        clearBackStackAndOpenLogin()
    }

    override fun openEventFragment(id: Long, title: String) {
        val bundle = EventFragment.makeBundle(id, title)
        when (navController?.currentDestination?.id) {
            R.id.qrCodeFragment -> navController?.navigate(
                R.id.action_qrCodeFragment_to_eventFragment,
                bundle
            )
            R.id.eventsFragment -> navController?.navigate(
                R.id.action_eventsFragment_to_eventFragment,
                bundle
            )
            R.id.historyFragment -> navController?.navigate(
                R.id.action_historyFragment_to_eventFragment,
                bundle
            )
        }
    }

    override fun openQrCodeSharingFragment(code: String) {
        when (navController?.currentDestination?.id) {
            R.id.eventFragment -> {
                val bundle = QrCodeSharingFragment.makeBundle(code)
                navController?.navigate(
                    R.id.action_eventFragment_to_shareQrCodeFragment, bundle
                )
            }
        }
    }

    override fun openExpenseFragment(roomId: Long, expense: Expense, title: String) {
        when (navController?.currentDestination?.id) {
            R.id.eventFragment -> {
                val bundle = ExpenseFragment.makeBundle(roomId, expense.id, title)
                navController?.navigate(
                    R.id.action_eventFragment_to_expenseFragment, bundle
                )
            }
        }
    }

    override fun openDebtFragment(roomId: Long) {
        when (navController?.currentDestination?.id) {
            R.id.eventFragment -> {
                val bundle = DebtFragment.makeBundle(roomId)
                navController?.navigate(
                    R.id.action_eventFragment_to_debtFragment, bundle
                )
            }
        }
    }

    fun checkBottomBar() {
        EventBus.getDefault().post(
            SetBottomNavVisibility(
                !navBarHiddenIdsList.contains(navController?.currentDestination?.id),
                true
            )
        )
    }

    private companion object {
        val TAG = Navigator::class.simpleName
        val navBarHiddenIdsList = listOf(
            R.id.loginFragment,
            R.id.qrCodeFragment,
            R.id.eventFragment,
            R.id.shareQrCodeFragment,
            R.id.expenseFragment,
            R.id.debtFragment
        )
    }
}
