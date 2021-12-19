package ru.spbstu.feature.event.presentation

import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extenstions.setDebounceClickListener
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.common.utils.ToolbarFragment
import ru.spbstu.feature.R
import ru.spbstu.feature.calendar.presentation.CalendarFragment
import ru.spbstu.feature.databinding.FragmentAddEventDialogBinding
import ru.spbstu.feature.databinding.FragmentEventBinding
import ru.spbstu.feature.databinding.FragmentTimePickerDialogBinding
import ru.spbstu.feature.di.FeatureApi
import ru.spbstu.feature.di.FeatureComponent
import ru.spbstu.feature.domain.model.CalendarSelectionMode
import ru.spbstu.feature.event.presentation.adapter.ParticipantUserAdapter
import ru.spbstu.feature.event.presentation.adapter.PurchaseAdapter
import ru.spbstu.feature.events.presentation.wheelPicker.HourPickerAdapter
import ru.spbstu.feature.events.presentation.wheelPicker.MinutePickerAdapter
import studio.clapp.wheelpicker.extensions.formatLeadingZero
import java.time.LocalTime
import kotlin.math.ceil

class EventFragment : ToolbarFragment<EventViewModel>(
    R.layout.fragment_event,
    R.string.error_connection,
    ToolbarType.EMPTY
) {
    private var purchaseItemAddingDialog: BottomSheetDialog? = null
    private var timePickerDialog: BottomSheetDialog? = null

    private lateinit var timePickerDialogBinding: FragmentTimePickerDialogBinding
    private lateinit var timePickerMinuteAdapter: MinutePickerAdapter
    private lateinit var timePickerHourAdapter: HourPickerAdapter
    private val calendarFragment by lazy { CalendarFragment(CalendarSelectionMode.SINGLE_DAY) }

    private val purchaseAdapter by lazy {
        PurchaseAdapter(onItemClick = {
            viewModel.setBoughtStatus(it)
        }, onLongItemClick = {
            Toast.makeText(requireContext(), "Long press", Toast.LENGTH_SHORT).show()
        })
    }

    // TODO delete tosts
    private val participantUserAdapter by lazy {
        ParticipantUserAdapter(onItemClick = {
            Toast.makeText(requireContext(), "On user click", Toast.LENGTH_SHORT).show()
        }, onDeleteClick = {
            Toast.makeText(requireContext(), "On delete user click", Toast.LENGTH_SHORT).show()
        })
    }
    override val binding by viewBinding(FragmentEventBinding::bind)

    override fun getToolbarLayout(): ViewGroup = binding.frgEventLayoutToolbar.root

    override fun setupViews() {
        super.setupViews()
        binding.frgEventLayoutToolbar.includeToolbarTvTitle.text = "Комната 228"
        binding.frgEventRvPurchases.adapter = purchaseAdapter
        binding.frgEventRvUsers.adapter = participantUserAdapter

        binding.frgEventFabAdd.setDebounceClickListener {
            showEventAddingDialog()
        }
    }

    override fun subscribe() {
        super.subscribe()

        viewModel.purchases.observe {
            purchaseAdapter.bindData(it)
        }
        viewModel.users.observe {
            participantUserAdapter.bindData(it)
        }
    }

    private fun showEventAddingDialog() {
        if (purchaseItemAddingDialog == null) {
            purchaseItemAddingDialog =
                BottomSheetDialog(requireContext(), R.style.BottomSheetDialog_Theme)
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
            /*        viewModel.bundleDataWrapper.bundleData.observe {
                        val text = (it.get(CalendarFragment.DATA_KEY) as? CalendarDateRange)?.startDate
                            ?: LocalDate.now().toString()
                        dialogBinding.frgAddEventDialogEtDate.setText(text.toString())
                    }
        */
            purchaseItemAddingDialog?.setContentView(dialogBinding.root)
            purchaseItemAddingDialog?.behavior?.state = BottomSheetBehavior.STATE_EXPANDED
        }
        purchaseItemAddingDialog?.behavior?.state = BottomSheetBehavior.STATE_EXPANDED
        purchaseItemAddingDialog?.show()
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

    override fun inject() {
        FeatureUtils.getFeature<FeatureComponent>(this, FeatureApi::class.java)
            .eventComponentFactory()
            .create(this)
            .inject(this)
    }

    companion object {
        private val TAG = EventFragment::class.java.simpleName
        val BUNDLE_KEY = "${TAG}_BUNDLE_KEY"
        private val DATE_DIALOG_TAG = "${TAG}_DATE_DIALOG"

        // TODO add parcel to class Event
        fun makeBundle(): Bundle {
            val bundle = Bundle()
            return bundle
        }
    }
}
