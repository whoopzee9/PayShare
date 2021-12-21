package ru.spbstu.feature.event.presentation

import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
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
import ru.spbstu.feature.domain.model.Shop
import ru.spbstu.feature.event.presentation.adapter.ParticipantUserAdapter
import ru.spbstu.feature.event.presentation.adapter.PurchaseAdapter
import ru.spbstu.feature.event.presentation.dialog.PurchaseOptionsDialogFragment
import ru.spbstu.feature.mapSelect.ShopMapFragment
import ru.spbstu.feature.shared_adapters.DeleteDialogFragment
import java.time.LocalDate

class EventFragment : ToolbarFragment<EventViewModel>(
    R.layout.fragment_event,
    R.string.error_connection,
    ToolbarType.ROOM
) {
    private var purchaseOptionsDialog: PurchaseOptionsDialogFragment? = null

    private var purchaseItemAddingDialog: BottomSheetDialog? = null

    private val calendarFragment by lazy { CalendarFragment(CalendarSelectionMode.SINGLE_DAY) }

    private val shopFragment by lazy { ShopMapFragment() }

    private val purchaseAdapter by lazy {
        PurchaseAdapter(event = viewModel.event, onItemClick = { expense, isClicked ->
            viewModel.setBoughtStatus(expense, isClicked)
        }, onLongItemClick = {
            showPurchaseOptionsDialog(it)
        })
    }

    private var deleteFileDialogFragment: DeleteDialogFragment? = null

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
        setupIdFromArguments()

        binding.frgEventLayoutToolbar.includeToolbarTvTitle.text = viewModel.title
        binding.frgEventRvPurchases.adapter = purchaseAdapter
        binding.frgEventRvUsers.adapter = participantUserAdapter
        binding.frgEventLayoutToolbar.includeToolbarIbFirstButton.setDebounceClickListener {
            if (viewModel.toolbarState.value == EventViewModel.ToolbarState.Initial) {
                viewModel.selectAllPurchases()
            } else {
                showDeleteDialog(actionOk = {
                    viewModel.deleteRoom()
                }, actionCancel = {}, text = getString(R.string.delete_room))
            }
        }
        binding.frgEventFabAdd.setDebounceClickListener {
            showPurchaseItemAddingDialog()
        }
        binding.frgEventLayoutToolbar.includeToolbarIbSecondButton.setDebounceClickListener {
            if (viewModel.toolbarState.value == EventViewModel.ToolbarState.Initial) {
                viewModel.shareRoomCode()
            } else {
                showDeleteDialog(actionOk = {
                    viewModel.leaveFromRoom()
                }, actionCancel = {}, text = getString(R.string.leave_room))
            }
        }
        binding.frgEventFabEdit.setDebounceClickListener {
            handleEditButtonClick()
        }
        binding.frgEventIbDebt.setDebounceClickListener {
            viewModel.openDebtFragment()
        }
        binding.frgEventTvPurchaseSum.setDebounceClickListener {
            viewModel.openDebtFragment()
        }
        initPurchaseOptionsDialog()
    }

    private fun setupIdFromArguments() {
        viewModel.setupRoomId(requireArguments().getLong(BUNDLE_KEY))
        requireArguments().getString(BUNDLE_KEY_TITLE)?.let {
            viewModel.title = it
        }
    }

    override fun subscribe() {
        super.subscribe()
        viewModel.loadPurchases()

        viewModel.purchases.observe {
            binding.frgEventTvPurchaseSumValue.text =
                it.sumOf { purchase -> purchase.price }.toString()
            purchaseAdapter.event = viewModel.event
            purchaseAdapter.bindData(it)
            var total = 0.0
            it.forEach { expense ->
                if (expense.users.containsKey(viewModel.event.yourParticipantId) &&
                    expense.users[viewModel.event.yourParticipantId] == false &&
                    expense.buyer.id != viewModel.event.yourParticipantId) {
                    total += expense.price / expense.users.size
                }
            }
            binding.frgEventTvPurchaseSumValue.text = "$total руб"
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
            dialogBinding.frgAddPurchaseDialogEtShop.setDebounceClickListener {
                shopFragment.show(parentFragmentManager, SHOP_DIALOG_TAG)
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
                    textDate.text.toString()
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
            viewModel.bundleDataWrapper.bundleData.observe {
                val shop = (it.get(ShopMapFragment.DATA_KEY) as? Shop)
                dialogBinding.frgAddPurchaseDialogEtShop.setText(
                    shop?.name
                )
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
                showDeleteDialog(actionOk = {
                    viewModel.deletePurchase(expense)
                }, actionCancel = {}, text = getString(R.string.delete_purchase_item))
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
                viewModel.closePurchase(expense)
                dialog.dismiss()
            }
            dialog.show(parentFragmentManager, PURCHASE_OPTIONS_DIALOG_TAG)
        }
    }

    private fun showDeleteDialog(actionOk: () -> Unit, actionCancel: () -> Unit, text: String) {
        if (deleteFileDialogFragment == null) {
            deleteFileDialogFragment =
                DeleteDialogFragment.newInstance(getString(R.string.delete_room))
        }
        val dialog = deleteFileDialogFragment
        if (dialog != null) {
            dialog.setDialogWarningText(text)
            dialog.setOnOkClickListener {
                Toast.makeText(requireContext(), "Выхожу", Toast.LENGTH_SHORT).show()
                actionOk.invoke()
                dialog.dismiss()
            }
            dialog.setOnCancelClickListener {
                Toast.makeText(requireContext(), "Не Выхожу", Toast.LENGTH_SHORT).show()
                actionCancel.invoke()
                dialog.dismiss()
            }
            dialog.show(parentFragmentManager, DELETE_DIALOG_TAG)
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
        val BUNDLE_KEY_TITLE = "${TAG}_BUNDLE_KEY_TITILE"
        private val DATE_DIALOG_TAG = "${TAG}_DATE_DIALOG"
        private val SHOP_DIALOG_TAG = "${TAG}_SHOP_DIALOG_TAG"
        private val PURCHASE_OPTIONS_DIALOG_TAG = "${TAG}_PURCHASE_OPTIONS_DIALOG_TAG"
        private val DELETE_DIALOG_TAG = "${TAG}DELETE_DIALOG_TAG"

        fun makeBundle(id: Long, title: String): Bundle {
            val bundle = Bundle()
            bundle.putLong(BUNDLE_KEY, id)
            bundle.putString(BUNDLE_KEY_TITLE, title)
            return bundle
        }
    }
}
            