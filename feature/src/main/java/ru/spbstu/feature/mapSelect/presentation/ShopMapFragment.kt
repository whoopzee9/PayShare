package ru.spbstu.feature.mapSelect.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extenstions.addBackPressedCallback
import ru.spbstu.common.extenstions.setDebounceClickListener
import ru.spbstu.common.utils.FullScreenBottomSheetDialogFragment
import ru.spbstu.feature.databinding.FragmentShopMapBinding
import ru.spbstu.feature.di.FeatureApi
import ru.spbstu.feature.di.FeatureComponent
import javax.inject.Inject

class ShopMapFragment() : FullScreenBottomSheetDialogFragment(), OnMapReadyCallback {

    @Inject
    lateinit var viewModel: ShopMapViewModel

    private lateinit var mMap: GoogleMap

    private var _binding: FragmentShopMapBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onResume() {
        super.onResume()
        binding.frgShopMapDialogMap.onResume()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        inject()
        _binding = FragmentShopMapBinding.inflate(inflater, container, false)
        binding.frgShopMapDialogMap.onCreate(savedInstanceState)
        binding.frgShopMapDialogMbDone.setDebounceClickListener {
            viewModel.setName(binding.frgShopMapDialogEtTitle.text.toString())
            hide()
        }

        addBackPressedCallback {
            hide()
        }
        subscribe()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.frgShopMapDialogMap.getMapAsync(this)
    }

    override fun onDialogShown() {
        super.onDialogShown()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                spb,
                MAP_ZOOM_NEAR
            )
        )
        mMap.setOnMapClickListener {
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(it))
            viewModel.setCoordinates(it)
        }
    }

    private fun setupFromArguments() {
    }

    private fun subscribe() {
    }

    private fun inject() {
        FeatureUtils.getFeature<FeatureComponent>(this, FeatureApi::class.java)
            .shopMapComponentFactory()
            .create(this)
            .inject(this)
    }

    companion object {
        private val spb = LatLng(59.986505, 30.348305)
        private const val MAP_ZOOM_NEAR = 15F
        private var TAG = ShopMapFragment::class.java.simpleName
        val DATA_KEY = "${TAG}_DATA_KEY"
    }
}
