package ru.spbstu.feature.expense.presentation

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extenstions.setLightStatusBar
import ru.spbstu.common.extenstions.setStatusBarColor
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.common.utils.ToolbarFragment
import ru.spbstu.feature.R
import ru.spbstu.feature.databinding.FragmentExpenseBinding
import ru.spbstu.feature.di.FeatureApi
import ru.spbstu.feature.di.FeatureComponent
import ru.spbstu.feature.domain.model.Expense
import ru.spbstu.feature.expense.presentation.adapter.PayedUserAdapter
import java.time.format.DateTimeFormatter

class ExpenseFragment :
    ToolbarFragment<ExpenseViewModel>(
        R.layout.fragment_expense,
        R.string.empty_toolbar,
        ToolbarType.BACK
    ),
    OnMapReadyCallback {

    override val binding by viewBinding(FragmentExpenseBinding::bind)
    private lateinit var mMap: GoogleMap

    private val participantUserAdapter by lazy {
        PayedUserAdapter()
    }

    override fun getToolbarLayout(): ViewGroup = binding.frgExpenseLayoutToolbar.root

    override fun setupViews() {
        super.setupViews()
        requireActivity().setStatusBarColor(R.color.background_primary)
        requireView().setLightStatusBar()
        binding.frgExpenseRvUsers.adapter = participantUserAdapter
        binding.includeCarStatisticsMap.getMapAsync(this)
    }

    override fun onResume() {
        super.onResume()
        binding.includeCarStatisticsMap.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.includeCarStatisticsMap.onCreate(savedInstanceState)
    }

    override fun subscribe() {
        super.subscribe()

        viewModel.purchase.observe {
            setPurchaseInfo(it)
        }
        viewModel.users.observe {
            participantUserAdapter.bindData(it)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnMarkerClickListener { marker ->
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.position, MAP_ZOOM_NEAR))
            if (marker.isInfoWindowShown) {
                marker.hideInfoWindow()
            } else {
                marker.showInfoWindow()
            }
            true
        }
        lifecycleScope.launch {
            viewModel.mapShopCoordinates.observe {
                pinMarkerOnMap(it)
            }
        }
    }

    private fun pinMarkerOnMap(list: List<LatLng>) {
        list.forEach {
            mMap.addMarker(MarkerOptions().position(it))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(it, MAP_ZOOM_NEAR))
        }
    }

    private fun setPurchaseInfo(expense: Expense) {
        binding.frgExpenseLayoutPurchaseInfo.itemPurchaseInfoPrice.text = expense.price.toString()
        binding.frgExpenseLayoutPurchaseInfo.itemPurchaseInfoTvBuyerName.text =
            expense.buyer.getFullName()
        binding.frgExpenseLayoutPurchaseInfo.itemPurchaseInfoTvDate.text =
            expense.date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        binding.frgExpenseLayoutPurchaseInfo.itemPurchaseInfoTvDescription.text = expense.name
        binding.frgExpenseLayoutPurchaseInfo.itemPurchaseInfoCbPaid.isVisible = false
    }

    override fun inject() {
        FeatureUtils.getFeature<FeatureComponent>(this, FeatureApi::class.java)
            .expenseComponentFactory()
            .create(this)
            .inject(this)
    }

    companion object {
        private const val MAP_ZOOM_FAR = 10F
        private const val MAP_ZOOM_NEAR = 15F
        private const val KEYBOARD_DELAY = 50L

        // TODO set bundle
        fun makeBundle(expense: Expense): Bundle {
            val bundle = Bundle()
            return bundle
        }
    }
}
