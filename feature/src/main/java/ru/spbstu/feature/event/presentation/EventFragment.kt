package ru.spbstu.feature.event.presentation

import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extenstions.setDebounceClickListener
import ru.spbstu.common.extenstions.setFilledButtonClickability
import ru.spbstu.common.extenstions.setLightStatusBar
import ru.spbstu.common.extenstions.setStatusBarColor
import ru.spbstu.common.extenstions.setTextLengthWatcher
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.common.utils.ToolbarFragment
import ru.spbstu.feature.R
import ru.spbstu.feature.calendar.presentation.CalendarFragment
import ru.spbstu.feature.databinding.FragmentAddPurchaseDialogBinding
import ru.spbstu.feature.databinding.FragmentEventBinding
import ru.spbstu.feature.di.FeatureApi
import ru.spbstu.feature.di.FeatureComponent
import ru.spbstu.feature.domain.model.CalendarDateRange
import ru.spbstu.feature.domain.model.CalendarSelectionMode
import ru.spbstu.feature.domain.model.Expense
import ru.spbstu.feature.event.presentation.adapter.ParticipantUserAdapter
import ru.spbstu.feature.event.presentation.adapter.PurchaseAdapter
import ru.spbstu.feature.event.presentation.dialog.PurchaseOptionsDialogFragment
import java.time.LocalDate

class EventFragment : ToolbarFragment<EventViewModel>(
    R.layout.fragment_event,
    R.string.error_connection,
    ToolbarType.ROOM
) {
    private var purchaseOptionsDialog: PurchaseOptionsDialogFragment? = null

    private var purchaseItemAddingDialog: BottomSheetDialog? = null

    private val calendarFragment by lazy { CalendarFragment(CalendarSelectionMode.SINGLE_DAY) }

    private val purchaseAdapter by lazy {
        PurchaseAdapter(onItemClick = {
            viewModel.setBoughtStatus(it)
        }, onLongItemClick = {
            showPurchaseOptionsDialog(it)
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
        requireActivity().setStatusBarColor(R.color.white)
        requireView().setLightStatusBar()
        binding.frgEventLayoutToolbar.includeToolbarTvTitle.text = "Комната 228"
        binding.frgEventRvPurchases.adapter = purchaseAdapter
        binding.frgEventRvUsers.adapter = participantUserAdapter
        binding.frgEventLayoutToolbar.includeToolbarIbFirstButton.setDebounceClickListener {
            if (viewModel.toolbarState.value == EventViewModel.ToolbarState.Initial) {
                viewModel.selectAllPurchases()
            } else {
                // todo delete dialog
            }
        }
        binding.frgEventFabAdd.setDebounceClickListener {
            showPurchaseItemAddingDialog()
        }
        binding.frgEventLayoutToolbar.includeToolbarIbSecondButton.setDebounceClickListener {
            if (viewModel.toolbarState.value == EventViewModel.ToolbarState.Initial) {
                viewModel.shareRoomCode()
            } else {
                // todo delete dialog
            }
        }
        binding.frgEventFabEdit.setDebounceClickListener {
            handleEditButtonClick()
        }
        initPurchaseOptionsDialog()
    }

    override fun subscribe() {
        super.subscribe()

        viewModel.purchases.observe { it ->
            binding.frgEventTvPurchaseSumValue.text =
                it.sumOf { purchase -> purchase.price }.toString()
            purchaseAdapter.bindData(it)
        }
        viewModel.users.observe {
            participantUserAdapter.bindData(it)
        }
        viewModel.toolbarState.observe {
            handleToolbarState(it)
        }
    }

    private fun handleToolbarState(state: EventViewModel.ToolbarState) {
        when (state) {
            is EventViewModel.ToolbarState.Initial -> {
                binding.frgEventLayoutToolbar.includeToolbarIbFirstButton.setImageResource(R.drawable.ic_is_select_all_24)
                binding.frgEventLayoutToolbar.includeToolbarIbSecondButton.setImageResource(R.drawable.ic_info_24)
                binding.frgEventFabEdit.setImageResource(R.drawable.ic_edit_24)
            }
            is EventViewModel.ToolbarState.EditMode -> {
                binding.frgEventLayoutToolbar.includeToolbarIbFirstButton.setImageResource(R.drawable.ic_is_delete_2_24)
                binding.frgEventLayoutToolbar.includeToolbarIbSecondButton.setImageResource(R.drawable.ic_close_24)
                binding.frgEventFabEdit.setImageResource(R.drawable.ic_close_24)
            }
        }
    }

    private fun handleEditButtonClick() {
        viewModel.changeToolbarState()
    }

    private fun showPurchaseItemAddingDialog(expense: Expense = Expense()) {
        if (purchaseItemAddingDialog == null) {
            purchaseItemAddingDialog =
                BottomSheetDialog(requireContext(), R.style.BottomSheetDialog_Theme)
            val dialogBinding =
                FragmentAddPurchaseDialogBinding.inflate(layoutInflater, binding.root, false)
            dialogBinding.frgAddPurchaseDialogEtDate.setDebounceClickListener {
                calendarFragment.show(parentFragmentManager, DATE_DIALOG_TAG)
            }
            changeSavePurchaseButtonIsClickable(dialogBinding)
            dialogBinding.frgAddPurchaseDialogEtTitle.setText(expense.description)
            dialogBinding.frgAddPurchaseDialogEtPrice.setText(expense.price.toString())
            dialogBinding.frgAddPurchaseDialogEtDate.setText(expense.date.toString())
            dialogBinding.frgAddPurchaseDialogEtShop.setText(expense.purchaseShop.name)

            dialogBinding.frgAddPurchaseDialogEtTitle.setTextLengthWatcher(
                runOnTextEntered = { changeSavePurchaseButtonIsClickable(dialogBinding) }
            )
            dialogBinding.frgAddPurchaseDialogEtPrice.setTextLengthWatcher(
                runOnTextEntered = { changeSavePurchaseButtonIsClickable(dialogBinding) }
            )
            dialogBinding.frgAddPurchaseDialogEtDate.setTextLengthWatcher(
                runOnTextEntered = { changeSavePurchaseButtonIsClickable(dialogBinding) }
            )
            dialogBinding.frgAddPurchaseDialogEtShop.setTextLengthWatcher(
                runOnTextEntered = { changeSavePurchaseButtonIsClickable(dialogBinding) }
            )
            dialogBinding.frgAddPurchaseDialogMbSave.setDebounceClickListener {
                val textTitle = dialogBinding.frgAddPurchaseDialogEtTitle
                val textPrice = dialogBinding.frgAddPurchaseDialogEtPrice
                val textDate = dialogBinding.frgAddPurchaseDialogEtDate
                val textShop = dialogBinding.frgAddPurchaseDialogEtShop
                viewModel.createNewPurchase(
                    textTitle.text.toString(), textPrice.text.toString(),
                    textDate.text.toString(), textShop.text.toString()
                )
                // TODO fix clearing when edit opened after adding action
                textTitle.text?.clear()
                textPrice.text?.clear()
                textShop.text?.clear()
                purchaseItemAddingDialog?.dismiss()
            }
            viewModel.bundleDataWrapper.bundleData.observe {
                val text = (it.get(CalendarFragment.DATA_KEY) as? CalendarDateRange)?.startDate
                    ?: LocalDate.now().toString()
                dialogBinding.frgAddPurchaseDialogEtDate.setText(text.toString())
            }
            purchaseItemAddingDialog?.setContentView(dialogBinding.root)
            purchaseItemAddingDialog?.behavior?.state = BottomSheetBehavior.STATE_EXPANDED
        }
        purchaseItemAddingDialog?.behavior?.state = BottomSheetBehavior.STATE_EXPANDED
        purchaseItemAddingDialog?.show()
    }

    private fun changeSavePurchaseButtonIsClickable(binding: FragmentAddPurchaseDialogBinding) {
        val textTitle = binding.frgAddPurchaseDialogEtTitle.text.toString()
        val textPrice = binding.frgAddPurchaseDialogEtPrice.text.toString()
        val textDate = binding.frgAddPurchaseDialogEtDate.text.toString()
        val textShop = binding.frgAddPurchaseDialogEtShop.text.toString()
        binding.frgAddPurchaseDialogMbSave.setFilledButtonClickability(
            (textTitle.isNotEmpty() && textPrice.isNotEmpty() && textDate.isNotEmpty() && textShop.isNotEmpty())
        )
    }

    private fun initPurchaseOptionsDialog() {
        if (purchaseOptionsDialog == null) {
            purchaseOptionsDialog = PurchaseOptionsDialogFragment.newInstance()
        }
    }

    private fun showPurchaseOptionsDialog(expense: Expense) {
        val dialog = purchaseOptionsDialog
        if (dialog != null) {
            dialog.setOnDeletePurchaseClick {
                dialog.dismiss()
            }
            dialog.setOnEditPurchaseClick {
                dialog.dismiss()
                showPurchaseItemAddingDialog(expense)
            }
            dialog.setOnInfoPurchaseClick {
                dialog.dismiss()
                viewModel.openPurchase(expense)
            }
            dialog.setOnCLosePurchaseClick {
                dialog.dismiss()
            }
            dialog.show(parentFragmentManager, PURCHASE_OPTIONS_DIALOG_TAG)
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
        private val DATE_DIALOG_TAG = "${TAG}_DATE_DIALOG"
        private val PURCHASE_OPTIONS_DIALOG_TAG = "${TAG}_PURCHASE_OPTIONS_DIALOG_TAG"

        // TODO add parcel to class Event
        fun makeBundle(): Bundle {
            val bundle = Bundle()
            return bundle
        }
    }
}
