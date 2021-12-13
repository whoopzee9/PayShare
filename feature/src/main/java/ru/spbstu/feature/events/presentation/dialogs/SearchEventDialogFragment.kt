package ru.spbstu.feature.events.presentation.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.poovam.pinedittextfield.PinField
import ru.spbstu.common.extenstions.setDebounceClickListener
import ru.spbstu.common.extenstions.setTextLengthWatcher
import ru.spbstu.common.text.TextLengthWatcher
import ru.spbstu.feature.databinding.FragmentSearchEventDialogBinding

class SearchEventDialogFragment : DialogFragment() {

    private var _binding: FragmentSearchEventDialogBinding? = null
    private val binding get() = _binding!!

    private var onQRClick: (() -> Unit)? = null
    private var onOKClick: ((String) -> Unit)? = null

    private var searchText: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchEventDialogBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setDimAmount(DIM_VALUE)

        setupDialogViews()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    fun setOnQRClickListener(onClick: () -> Unit) {
        onQRClick = onClick
    }

    fun setOnOKClickListener(onClick: (String) -> Unit) {
        onOKClick = onClick
    }

    private fun setupDialogViews() {
        binding.frgSerchEventDialogMbCancel.setDebounceClickListener {
            dialog?.dismiss()
        }
        binding.frgSerchEventDialogMbEnterQr.setDebounceClickListener {
            onQRClick?.invoke()
        }
        binding.frgSerchEventDialogMbOk.setDebounceClickListener {
            onOKClick?.invoke(searchText)
        }
        binding.frgSerchEventDialogMbOk.isEnabled = false
        binding.frgSearchEventDialogPinField.onTextCompleteListener = object : PinField.OnTextCompleteListener {
            override fun onTextComplete(enteredText: String): Boolean {
                searchText = enteredText
                binding.frgSerchEventDialogMbOk.isEnabled = true
                return false
            }
        }
        binding.frgSearchEventDialogPinField.setTextLengthWatcher(runAfterTextEntered = {
            if (binding.frgSearchEventDialogPinField.text.toString().length < 5) {
                binding.frgSerchEventDialogMbOk.isEnabled = false
            }
        })
    }

    companion object {
        private const val DIM_VALUE = 0.7f

        fun newInstance(): SearchEventDialogFragment {
            return SearchEventDialogFragment()
        }
    }
}