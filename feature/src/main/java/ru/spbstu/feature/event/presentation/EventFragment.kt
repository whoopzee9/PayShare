package ru.spbstu.feature.event.presentation

import android.os.Bundle
import android.view.ViewGroup
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.common.utils.ToolbarFragment
import ru.spbstu.feature.R
import ru.spbstu.feature.databinding.FragmentEventBinding
import ru.spbstu.feature.di.FeatureApi
import ru.spbstu.feature.di.FeatureComponent

class EventFragment : ToolbarFragment<EventViewModel>(
    R.layout.fragment_event,
    R.string.error_connection,
    ToolbarType.EMPTY
) {

    override val binding by viewBinding(FragmentEventBinding::bind)

    override fun getToolbarLayout(): ViewGroup = binding.frgEventLayoutToolbar.root

    override fun setupViews() {
        super.setupViews()
    }

    override fun inject() {
        FeatureUtils.getFeature<FeatureComponent>(this, FeatureApi::class.java)
            .eventComponentFactory()
            .create(this)
            .inject(this)
    }

    override fun subscribe() {
        super.subscribe()
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
