package ru.spbstu.feature.event.presentation.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import ru.spbstu.common.extenstions.setDebounceClickListener
import ru.spbstu.feature.databinding.DialogPurchaseOptionsBinding

class PurchaseOptionsDialogFragment : DialogFragment() {

    private var _binding: DialogPurchaseOptionsBinding? = null
    private val binding get() = _binding!!

    private var onDeletePurchaseClick: (() -> Unit)? = null
    private var onEditPurchaseClick: (() -> Unit)? = null
    private var onInfoPurchaseClick: (() -> Unit)? = null
    private var onClosePurchaseClick: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogPurchaseOptionsBinding.inflate(inflater, container, false)

        dialog?.window?.setDimAmount(DIM_ALPHA)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.dialogPurchaseOptionsTbDeleteFile.setDebounceClickListener {
            onDeletePurchaseClick?.invoke()
        }
        binding.dialogPurchaseOptionsTbEditFile.setDebounceClickListener {
            onEditPurchaseClick?.invoke()
        }
        binding.dialogPurchaseOptionsTbIbInfo.setDebounceClickListener {
            onInfoPurchaseClick?.invoke()
        }
        binding.dialogPurchaseOptionsTbIbClosePurchase.setDebounceClickListener {
            onClosePurchaseClick?.invoke()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    fun setOnDeletePurchaseClick(onClick: () -> Unit) {
        onDeletePurchaseClick = onClick
    }

    fun setOnEditPurchaseClick(onClick: () -> Unit) {
        onEditPurchaseClick = onClick
    }

    fun setOnInfoPurchaseClick(onClick: () -> Unit) {
        onInfoPurchaseClick = onClick
    }

    fun setOnCLosePurchaseClick(onClick: () -> Unit) {
        onClosePurchaseClick = onClick
    }

    companion object {
        private const val DIM_ALPHA = 0.25F
        fun newInstance(): PurchaseOptionsDialogFragment {
            return PurchaseOptionsDialogFragment()
        }
    }
}
