package ru.spbstu.payshare.navigation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import org.greenrobot.eventbus.EventBus
import ru.spbstu.common.events.SetBottomNavVisibility
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.domain.model.Event
import ru.spbstu.feature.event.presentation.EventFragment
import ru.spbstu.payshare.R

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
        when (navController?.currentDestination?.id) {
            R.id.eventsFragment -> navController?.navigate(R.id.action_eventsFragment_to_loginFragment)
        }
    }

    override fun openEventFragment(event: Event) {
        // todo send bundle
        when (navController?.currentDestination?.id) {
            R.id.qrCodeFragment -> navController?.navigate(R.id.action_qrCodeFragment_to_eventFragment)
            // R.id.eventsFragment -> navController?.navigate(R.id.action_eventsFragment_to_eventFragment)
            R.id.eventsFragment -> {
                val bundle = EventFragment.makeBundle()
                navController?.navigate(
                    R.id.action_eventsFragment_to_eventFragment, bundle
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
        val navBarHiddenIdsList = listOf(
            R.id.loginFragment,
            R.id.qrCodeFragment,
            R.id.eventFragment
        )
    }
}
