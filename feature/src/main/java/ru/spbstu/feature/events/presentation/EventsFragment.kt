package ru.spbstu.feature.events.presentation

import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extenstions.clearLightStatusBar
import ru.spbstu.common.extenstions.setDebounceClickListener
import ru.spbstu.common.extenstions.setStatusBarColor
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.common.utils.ToolbarFragment
import ru.spbstu.feature.R
import ru.spbstu.feature.calendar.presentation.CalendarFragment
import ru.spbstu.feature.databinding.FragmentAddEventDialogBinding
import ru.spbstu.feature.databinding.FragmentEventsBinding
import ru.spbstu.feature.databinding.FragmentTimePickerDialogBinding
import ru.spbstu.feature.di.FeatureApi
import ru.spbstu.feature.di.FeatureComponent
import ru.spbstu.feature.domain.model.CalendarDateRange
import ru.spbstu.feature.domain.model.CalendarSelectionMode
import ru.spbstu.feature.events.presentation.adapter.EventsAdapter
import ru.spbstu.feature.events.presentation.dialogs.SearchEventDialogFragment
import ru.spbstu.feature.events.presentation.wheelPicker.HourPickerAdapter
import ru.spbstu.feature.events.presentation.wheelPicker.MinutePickerAdapter
import studio.clapp.wheelpicker.extensions.formatLeadingZero
import java.time.LocalDate
import java.time.LocalTime
import kotlin.math.ceil


class EventsFragment : ToolbarFragment<EventsViewModel>(
    R.layout.fragment_events,
    R.string.purchases,
    ToolbarType.PURCHASES
) {

    override val binding by viewBinding(FragmentEventsBinding::bind)

    private lateinit var adapter: EventsAdapter

    private var searchEventDialog: SearchEventDialogFragment? = null

    private var eventAddingDialog: BottomSheetDialog? = null
    private var timePickerDialog: BottomSheetDialog? = null

    private lateinit var timePickerDialogBinding: FragmentTimePickerDialogBinding
    private lateinit var timePickerMinuteAdapter: MinutePickerAdapter
    private lateinit var timePickerHourAdapter: HourPickerAdapter
    private val calendarFragment by lazy { CalendarFragment(CalendarSelectionMode.SINGLE_DAY) }

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
        //QrCode
        binding.frgEventsLayoutToolbar.includeToolbarIbFirstButton.setDebounceClickListener {
            viewModel.openQrCodeScanner()
        }
        binding.frgEventsFabAdd.setDebounceClickListener {
            showEventAddingDialog()
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

    private fun showEventAddingDialog() {
        if (eventAddingDialog == null) {
            eventAddingDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog_Theme)
            val dialogBinding =
                FragmentAddEventDialogBinding.inflate(layoutInflater, binding.root, false)
            dialogBinding.frgAddEventDialogEtTime.setDebounceClickListener {
                showTimePickerDialog(LocalTime.now()) { hour, min ->
                    dialogBinding.frgAddEventDialogEtTime.setText("${hour.formatLeadingZero()}:${min.formatLeadingZero()}")
                }
            }
            dialogBinding.frgAddEventDialogEtDate.setDebounceClickListener {
                calendarFragment.show(parentFragmentManager, DATE_DIALOG_TAG)
            }
            dialogBinding.frgAddEventDialogMbSave.setDebounceClickListener {

            }
            viewModel.bundleDataWrapper.bundleData.observe {
                val text = (it.get(CalendarFragment.DATA_KEY) as? CalendarDateRange)?.startDate
                    ?: LocalDate.now().toString()
                dialogBinding.frgAddEventDialogEtDate.setText(text.toString())
            }

            eventAddingDialog?.setContentView(dialogBinding.root)
            eventAddingDialog?.behavior?.state = BottomSheetBehavior.STATE_EXPANDED
        }
        eventAddingDialog?.behavior?.state = BottomSheetBehavior.STATE_EXPANDED
        eventAddingDialog?.show()
    }

    private fun showTimePickerDialog(
        currTime: LocalTime,
        onSaveClick: (hour: Int, min: Int) -> Unit
    ) {
        if (timePickerDialog == null) {
            timePickerDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog_Theme)
            timePickerDialogBinding =
                FragmentTimePickerDialogBinding.inflate(layoutInflater, binding.root, false)
            timePickerHourAdapter = HourPickerAdapter()
            timePickerMinuteAdapter = MinutePickerAdapter()

            with(timePickerDialogBinding.frgTimePickerDialogHourPicker) {
                setAdapter(timePickerHourAdapter)
                setOnUpListener { timePickerDialog?.behavior?.isDraggable = true }
                setOnDownListener { timePickerDialog?.behavior?.isDraggable = false }
            }

            with(timePickerDialogBinding.frgTimePickerDialogMinutePicker) {
                setAdapter(timePickerMinuteAdapter)
                setOnUpListener { timePickerDialog?.behavior?.isDraggable = true }
                setOnDownListener { timePickerDialog?.behavior?.isDraggable = false }
            }

            timePickerDialog?.setContentView(timePickerDialogBinding.root)
        }
        timePickerHourAdapter.picker?.scrollTo(currTime.hour)
        timePickerMinuteAdapter.picker?.scrollTo(ceil(currTime.minute / 5.0).toInt())

        timePickerDialogBinding.frgTimePickerDialogMbSave.setDebounceClickListener {
            val min = timePickerMinuteAdapter.picker?.getCurrentItem()?.toInt() ?: 0
            val hour = timePickerHourAdapter.picker?.getCurrentItem()?.toInt() ?: 0

            onSaveClick(hour, min)
            timePickerDialog?.dismiss()
        }
        timePickerDialog?.behavior?.state = BottomSheetBehavior.STATE_EXPANDED
        timePickerDialog?.show()
    }

    companion object {
        private const val SEARCH_DIALOG_TAG = "ru.spbstu.payshare.SEARCH_EVENT_DIALOG"
        private const val DATE_DIALOG_TAG = "ru.spbstu.payshare.DATE_DIALOG"
    }
}