package ru.spbstu.feature.event.presentation

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import coil.load
import coil.transform.CircleCropTransformation
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extenstions.setDebounceClickListener
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.common.utils.ToolbarFragment
import ru.spbstu.feature.R
import ru.spbstu.feature.databinding.FragmentEventBinding
import ru.spbstu.feature.di.FeatureApi
import ru.spbstu.feature.di.FeatureComponent
import ru.spbstu.feature.event.presentation.adapter.PurchaseAdapter

class EventFragment : ToolbarFragment<EventViewModel>(
    R.layout.fragment_event,
    R.string.error_connection,
    ToolbarType.EMPTY
) {

    private val purchaseAdapter by lazy {
        PurchaseAdapter(viewModel::setBoughtStatus)
    }
    override val binding by viewBinding(FragmentEventBinding::bind)

    override fun getToolbarLayout(): ViewGroup = binding.frgEventLayoutToolbar.root

    override fun setupViews() {
        super.setupViews()
        binding.frgEventLayoutToolbar.includeToolbarTvTitle.text = "Комната 228"
        binding.frgEventRvPurchases.adapter = purchaseAdapter
        binding.frgEventLayoutUser.itemUserIv.load("https://images.pexels.com/photos/5493447/pexels-photo-5493447.jpeg?cs=srgb&dl=pexels-olia-danilevich-5493447.jpg&fm=jpg") {
            transformations(CircleCropTransformation())
        }
        binding.frgEventLayoutUser.itemUserIbDelete.visibility = View.GONE
        binding.frgEventLayoutUser.itemUserIbDelete.setDebounceClickListener {
            Toast.makeText(requireContext(), "Clicked", Toast.LENGTH_SHORT).show()
        }
    }

    override fun subscribe() {
        super.subscribe()

        viewModel.purchases.observe {
            purchaseAdapter.bindData(it)
        }
    }

    override fun inject() {
        FeatureUtils.getFeature<FeatureComponent>(this, FeatureApi::class.java)
            .eventComponentFactory()
            .create(this)
            .inject(this)
    }

    companion object {
        private val TAG = EventFragment::class.java.simpleName
        val BUNDLE_KEY = "${TAG}_BUNDLE_KEY"

        // TODO add parcel to class Event
        fun makeBundle(): Bundle {
            val bundle = Bundle()
            return bundle
        }
    }
}
