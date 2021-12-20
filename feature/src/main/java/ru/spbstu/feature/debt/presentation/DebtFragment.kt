package ru.spbstu.feature.debt.presentation

import android.view.View
import android.view.ViewGroup
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.common.utils.ToolbarFragment
import ru.spbstu.feature.R
import ru.spbstu.feature.databinding.FragmentDebtBinding
import ru.spbstu.feature.debt.presentation.adapter.DebtAdapter
import ru.spbstu.feature.di.FeatureApi
import ru.spbstu.feature.di.FeatureComponent
import ru.spbstu.feature.events.presentation.adapter.EventsAdapter

class DebtFragment: ToolbarFragment<DebtViewModel>(
    R.layout.fragment_debt,
    R.string.debts,
    ToolbarType.BACK
) {

    override val binding by viewBinding(FragmentDebtBinding::bind)

    private lateinit var adapter: DebtAdapter

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

    private fun initAdapter() {
        adapter = DebtAdapter()
        binding.frgDebtRvDebts.adapter = adapter
    }

    override fun subscribe() {
        super.subscribe()
        viewModel.debts.observe {
            if (it.isEmpty()) {
                binding.frgDebtIvShare.visibility = View.VISIBLE
                binding.frgDebtTvNoDebts.visibility = View.VISIBLE
                binding.frgDebtRvDebts.visibility = View.GONE
                binding.frgDebtTvTotalDebt.visibility = View.GONE
            } else {
                binding.frgDebtIvShare.visibility = View.GONE
                binding.frgDebtTvNoDebts.visibility = View.GONE
                binding.frgDebtRvDebts.visibility = View.VISIBLE
                binding.frgDebtTvTotalDebt.visibility = View.VISIBLE
                binding.frgDebtTvTotalDebt
            }
            adapter.bindData(it)
        }
    }
}