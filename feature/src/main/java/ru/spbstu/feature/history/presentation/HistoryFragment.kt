package ru.spbstu.feature.history.presentation

import android.view.View
import android.view.ViewGroup
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extenstions.clearLightStatusBar
import ru.spbstu.common.extenstions.setDebounceClickListener
import ru.spbstu.common.extenstions.setStatusBarColor
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.common.utils.ToolbarFragment
import ru.spbstu.feature.R
import ru.spbstu.feature.databinding.FragmentHistoryBinding
import ru.spbstu.feature.di.FeatureApi
import ru.spbstu.feature.di.FeatureComponent
import ru.spbstu.feature.events.presentation.adapter.EventsAdapter
import ru.spbstu.feature.history.presentation.adapter.HistoryAdapter

class HistoryFragment: ToolbarFragment<HistoryViewModel>(
    R.layout.fragment_history,
    R.string.history,
    ToolbarType.EMPTY
) {

    override val binding by viewBinding(FragmentHistoryBinding::bind)

    override fun getToolbarLayout(): ViewGroup = binding.frgHistoryLayoutToolbar.root

    private lateinit var adapter: HistoryAdapter

    override fun setupViews() {
        super.setupViews()
        requireActivity().setStatusBarColor(R.color.toolbar_background_color_primary)
        requireView().clearLightStatusBar()
        initAdapter()
        viewModel.getEvents()
    }

    override fun inject() {
        FeatureUtils.getFeature<FeatureComponent>(this, FeatureApi::class.java)
            .historyComponentFactory()
            .create(this)
            .inject(this)
    }

    private fun initAdapter() {
        adapter = HistoryAdapter(viewModel::openEvent)
        binding.frgHistoryRvEvents.adapter = adapter
    }

    override fun subscribe() {
        super.subscribe()
        viewModel.events.observe {
            if (it.isEmpty()) {
                binding.frgHistoryIvShare.visibility = View.VISIBLE
                binding.frgHistoryTvNoHistory.visibility = View.VISIBLE
                binding.frgHistoryRvEvents.visibility = View.GONE
            } else {
                binding.frgHistoryIvShare.visibility = View.GONE
                binding.frgHistoryTvNoHistory.visibility = View.GONE
                binding.frgHistoryRvEvents.visibility = View.VISIBLE
            }
            adapter.bindData(it)
        }
    }
}