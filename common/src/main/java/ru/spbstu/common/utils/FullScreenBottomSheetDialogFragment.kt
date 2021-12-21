package ru.spbstu.common.utils

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.spbstu.common.R

open class FullScreenBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private val behavior: BottomSheetBehavior<*> by lazy {
        (dialog as BottomSheetDialog).behavior
    }
    private lateinit var parentBehavior: BottomSheetBehavior<*>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (view.parent as View).alpha = ALPHA_HIDDEN
        super.onViewCreated(view, savedInstanceState)
        requireDialog().setCanceledOnTouchOutside(false)
        requireDialog().window?.setDimAmount(0.2F)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().window.decorView.post {
            behavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    override fun getTheme(): Int {
        return R.style.BottomSheetDialog_Black
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener { dialogInterface ->
            behavior.state = BottomSheetBehavior.STATE_HIDDEN
            val d = dialogInterface as BottomSheetDialog
            (d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?)?.let {
                parentBehavior = BottomSheetBehavior.from(it)
                parentBehavior.setPeekHeight(
                    requireActivity().window.decorView.measuredHeight,
                    true
                )
                setupFullHeight(it)
                it.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        android.R.color.transparent
                    )
                )
            }
        }
        return dialog
    }

    protected fun hide() {
        requireDialog().dismiss()
    }

    protected open fun onDialogShown() {
        (requireView().parent as View).alpha = ALPHA_SHOWN
        parentBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    hide()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })
    }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams as ViewGroup.LayoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
        bottomSheet.setPadding(0, resources.getDimension(R.dimen.dp_12).toInt(), 0, 0)
        bottomSheet.post {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            onDialogShown()
        }
    }

    private companion object {
        const val ALPHA_HIDDEN = 0f
        const val ALPHA_SHOWN = 1f
    }
}
