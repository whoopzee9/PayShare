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
import ru.spbstu.feature.event.presentation.adapter.ParticipantUserAdapter
import ru.spbstu.feature.event.presentation.adapter.PurchaseAdapter
import java.time.LocalDate

class EventFragment : ToolbarFragment<EventViewModel>(
    R.layout.fragment_event,
    R.string.error_connection,
    ToolbarType.ROOM
) {
    private var purchaseItemAddingDialog: BottomSheetDialog? = null

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
        requireActivity().setStatusBarColor(R.color.white)
        requireView().setLightStatusBar()
        binding.frgEventLayoutToolbar.includeToolbarTvTitle.text = "Комната 228"
        binding.frgEventRvPurchases.adapter = purchaseAdapter
        binding.frgEventRvUsers.adapter = participantUserAdapter
        binding.frgEventLayoutToolbar.includeToolbarIbFirstButton.setDebounceClickListener {
            viewModel.selectAllPurchases()
        }
        binding.frgEventLayoutToolbar.includeToolbarIbSecondButton.setDebounceClickListener {
        }
        binding.frgEventFabAdd.setDebounceClickListener {
            showPurchaseItemAddingDialog()
        }
        setAddPurchaseCompleteListener()
    }

    private fun setAddPurchaseCompleteListener() {
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

    private fun showPurchaseItemAddingDialog() {
        if (purchaseItemAddingDialog == null) {
            purchaseItemAddingDialog =
                BottomSheetDialog(requireContext(), R.style.BottomSheetDialog_Theme)
            val dialogBinding =
                FragmentAddPurchaseDialogBinding.inflate(layoutInflater, binding.root, false)
            dialogBinding.frgAddPurchaseDialogEtDate.setDebounceClickListener {
                calendarFragment.show(parentFragmentManager, DATE_DIALOG_TAG)
            }
            dialogBinding.frgAddPurchaseDialogMbSave.setDebounceClickListener {
            }
            changeSavePurchaseButtonIsClickable(dialogBinding)
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
                val textTitle = dialogBinding.frgAddPurchaseDialogEtTitle.text.toString()
                val textPrice = dialogBinding.frgAddPurchaseDialogEtPrice.text.toString()
                val textDate = dialogBinding.frgAddPurchaseDialogEtDate.text.toString()
                val textShop = dialogBinding.frgAddPurchaseDialogEtShop.text.toString()
                viewModel.createNewPurchase(textTitle, textPrice, textDate, textShop)
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
