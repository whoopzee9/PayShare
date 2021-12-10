package ru.spbstu.feature.debt.presentation

import android.view.ViewGroup
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.common.utils.ToolbarFragment
import ru.spbstu.feature.R
import ru.spbstu.feature.databinding.FragmentDebtBinding
import ru.spbstu.feature.di.FeatureApi
import ru.spbstu.feature.di.FeatureComponent

class DebtFragment: ToolbarFragment<DebtViewModel>(
    R.layout.fragment_debt,
    R.string.error_connection,
    ToolbarType.EMPTY
) {

    override val binding by viewBinding(FragmentDebtBinding::bind)

    override fun getToolbarLayout(): ViewGroup = binding.frgDebtLayoutToolbar.root

    override fun setupViews() {
        super.setupViews()
    }

    override fun inject() {
        FeatureUtils.getFeature<FeatureComponent>(this, FeatureApi::class.java)
            .debtComponentFactory()
            .create(this)
            .inject(this)
    }

    override fun subscribe() {
        super.subscribe()

    }
}