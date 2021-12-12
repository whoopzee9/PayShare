package ru.spbstu.common.utils

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import ru.spbstu.common.R
import ru.spbstu.common.base.BaseFragment
import ru.spbstu.common.databinding.IncludeToolbarBinding
import ru.spbstu.common.extenstions.handleBackPressed
import ru.spbstu.common.extenstions.setDebounceClickListener


abstract class ToolbarFragment<T : BackViewModel> constructor(
    @LayoutRes contentLayoutId: Int,
    @StringRes private val titleResource: Int = 0,
    private val type: ToolbarType = ToolbarType.BACK
) : BaseFragment<T>(contentLayoutId) {

    private var _layoutToolbarBinding: IncludeToolbarBinding? = null
    private val layoutToolbarBinding get() = _layoutToolbarBinding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setToolbar()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroy() {
        _layoutToolbarBinding = null
        super.onDestroy()
    }

    protected fun setToolbar(firstClickListener: () -> Unit, secondClickListener: () -> Unit) {
        setToolbar(
            type = this.type,
            firstClickListener = firstClickListener,
            secondClickListener = secondClickListener
        )
    }

    protected abstract fun getToolbarLayout(): ViewGroup

    private fun setToolbar(
        type: ToolbarType = this.type,
        @StringRes titleResource: Int = this.titleResource,
        firstClickListener: (() -> Unit)? = { viewModel.back() },
        secondClickListener: (() -> Unit)? = { },
    ) {
        _layoutToolbarBinding = IncludeToolbarBinding.bind(getToolbarLayout())
        when (type) {
            ToolbarType.EMPTY -> {
                layoutToolbarBinding.includeToolbarIbFirstButton.visibility = View.GONE
                layoutToolbarBinding.includeToolbarIbSecondButton.visibility = View.GONE
            }
            ToolbarType.PURCHASES -> {
                layoutToolbarBinding.includeToolbarIbFirstButton.visibility = View.VISIBLE
                layoutToolbarBinding.includeToolbarIbFirstButton.setImageResource(type.firstIcon)
                layoutToolbarBinding.includeToolbarIbSecondButton.setImageResource(type.secondIcon)
                layoutToolbarBinding.root.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.toolbar_background_color_primary
                    )
                )
                layoutToolbarBinding.includeToolbarTvTitle.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.text_color_secondary
                    )
                )
            }
            ToolbarType.BACK -> {
                layoutToolbarBinding.includeToolbarIbFirstButton.visibility = View.VISIBLE
                layoutToolbarBinding.includeToolbarIbSecondButton.visibility = View.GONE
                layoutToolbarBinding.includeToolbarIbFirstButton.setImageResource(type.firstIcon)
                layoutToolbarBinding.root.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        android.R.color.transparent
                    )
                )
            }
            else -> {

            }
        }
        if (titleResource != 0) {
            layoutToolbarBinding.includeToolbarTvTitle.text = getString(titleResource)
        }
        firstClickListener?.let {
            layoutToolbarBinding.includeToolbarIbFirstButton.setDebounceClickListener {
                it()
            }
            if (type == ToolbarType.BACK) {
                handleBackPressed {
                    it()
                }
            }
        }
        secondClickListener?.let {
            layoutToolbarBinding.includeToolbarIbSecondButton.setDebounceClickListener {
                it()
            }
        }
    }

    enum class ToolbarType(@DrawableRes val firstIcon: Int, @DrawableRes val secondIcon: Int) {
        BACK(android.R.drawable.ic_delete, 0), EMPTY(0, 0), PURCHASES(
            R.drawable.ic_qr_code_24,
            R.drawable.ic_search_24
        )
    }
}
