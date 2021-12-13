package ru.spbstu.feature.events.presentation

import android.view.View
import android.view.ViewGroup
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extenstions.clearLightStatusBar
import ru.spbstu.common.extenstions.setDebounceClickListener
import ru.spbstu.common.extenstions.setStatusBarColor
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.common.utils.ToolbarFragment
import ru.spbstu.feature.R
import ru.spbstu.feature.databinding.FragmentEventsBinding
import ru.spbstu.feature.di.FeatureApi
import ru.spbstu.feature.di.FeatureComponent
import ru.spbstu.feature.events.presentation.adapter.EventsAdapter
import ru.spbstu.feature.events.presentation.dialogs.SearchEventDialogFragment


class EventsFragment : ToolbarFragment<EventsViewModel>(
    R.layout.fragment_events,
    R.string.purchases,
    ToolbarType.PURCHASES
) {

    override val binding by viewBinding(FragmentEventsBinding::bind)

    private lateinit var adapter: EventsAdapter

    private var searchEventDialog: SearchEventDialogFragment? = null

    override fun getToolbarLayout(): ViewGroup = binding.frgEventsLayoutToolbar.root

    override fun setupViews() {
        super.setupViews()
        requireActivity().setStatusBarColor(R.color.toolbar_background_color_primary)
        requireView().clearLightStatusBar()
        initAdapter()
        setToolbar(firstClickListener = {

        }, secondClickListener = {

        })

        //search
        binding.frgEventsLayoutToolbar.includeToolbarIbSecondButton.setDebounceClickListener {
            showSearchEventDialog()
        }
    }

    override fun inject() {
        FeatureUtils.getFeature<FeatureComponent>(this, FeatureApi::class.java)
            .eventsComponentFactory()
            .create(this)
            .inject(this)
    }

    override fun subscribe() {
        super.subscribe()
        viewModel.events.observe {
            if (it.isEmpty()) {
                binding.frgEventsIvShare.visibility = View.VISIBLE
                binding.frgEventsTvNoActiveEvents.visibility = View.VISIBLE
                binding.frgEventsRvEvents.visibility = View.GONE
            } else {
                binding.frgEventsIvShare.visibility = View.GONE
                binding.frgEventsTvNoActiveEvents.visibility = View.GONE
                binding.frgEventsRvEvents.visibility = View.VISIBLE
            }
            adapter.bindData(it)
        }
    }

    private fun initAdapter() {
        adapter = EventsAdapter(viewModel::openEvent)
        binding.frgEventsRvEvents.adapter = adapter
    }

    private fun showSearchEventDialog() {
        var dialogFragment = searchEventDialog
        if (dialogFragment == null) {
            dialogFragment = SearchEventDialogFragment.newInstance()
        } else {
            //setup dialog views, if necessary
        }
        searchEventDialog = dialogFragment
        dialogFragment.setOnOKClickListener {
            
        }
        dialogFragment.setOnQRClickListener {

        }
        dialogFragment.show(parentFragmentManager, SEARCH_DIALOG_TAG)
    }

    companion object {
        private const val SEARCH_DIALOG_TAG = "ru.spbstu.payshare.SEARCH_EVENT_DIALOG"
    }
}