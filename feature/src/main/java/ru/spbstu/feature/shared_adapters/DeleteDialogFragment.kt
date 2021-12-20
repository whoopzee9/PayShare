package ru.spbstu.feature.shared_adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import ru.spbstu.common.extenstions.setDebounceClickListener
import ru.spbstu.feature.R
import ru.spbstu.feature.databinding.FragmentDeleteDialogBinding

class DeleteDialogFragment : DialogFragment() {
    private var _binding: FragmentDeleteDialogBinding? = null
    private val binding get() = _binding!!
    private var onOkClickListener: (() -> Unit)? = null
    private var onCancelClickListener: (() -> Unit)? = null
    private var dialogWarningText: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeleteDialogBinding.inflate(inflater, container, false)

        dialog?.window?.setDimAmount(DIM_ALPHA)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(400,100)

        setupDialogView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.frgDeleteDialogMbCancel.setDebounceClickListener {
            onCancelClickListener?.invoke()
            dialog?.dismiss()
        }
        binding.frgDeleteDialogMbOk.setDebounceClickListener {
            onOkClickListener?.invoke()
            dialog?.dismiss()
        }
    }

    fun setOnOkClickListener(onClick: () -> Unit) {
        onOkClickListener = onClick
    }

    fun setOnCancelClickListener(onClick: () -> Unit) {
        onCancelClickListener = onClick
    }

    fun setDialogWarningText(dialogWarningText: String) {
        this.dialogWarningText = dialogWarningText
    }

    private fun setupDialogView() {
        binding.frgDeleteDialogText.text =
            if (!dialogWarningText.isNullOrEmpty()) dialogWarningText
            else getString(R.string.warning_delete)
    }

    companion object {
        private const val DIM_ALPHA = 0.25F
        fun newInstance(dialogWarningText: String): DeleteDialogFragment {
            val fragment = DeleteDialogFragment()
            fragment.setDialogWarningText(dialogWarningText)
            return fragment
        }
    }
}
