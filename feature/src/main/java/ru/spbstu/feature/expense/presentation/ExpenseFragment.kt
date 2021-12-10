package ru.spbstu.feature.expense.presentation

import android.view.ViewGroup
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.common.utils.ToolbarFragment
import ru.spbstu.feature.R
import ru.spbstu.feature.databinding.FragmentExpenseBinding
import ru.spbstu.feature.di.FeatureApi
import ru.spbstu.feature.di.FeatureComponent

class ExpenseFragment: ToolbarFragment<ExpenseViewModel>(
    R.layout.fragment_expense,
    R.string.error_connection,
    ToolbarType.EMPTY
) {

    override val binding by viewBinding(FragmentExpenseBinding::bind)

    override fun getToolbarLayout(): ViewGroup = binding.frgExpenseLayoutToolbar.root

    override fun setupViews() {
        super.setupViews()
    }

    override fun inject() {
        FeatureUtils.getFeature<FeatureComponent>(this, FeatureApi::class.java)
            .expenseComponentFactory()
            .create(this)
            .inject(this)
    }

    override fun subscribe() {
        super.subscribe()

    }
}