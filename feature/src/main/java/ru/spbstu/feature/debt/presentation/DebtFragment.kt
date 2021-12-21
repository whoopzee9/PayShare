package ru.spbstu.feature.debt.presentation

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extenstions.setLightStatusBar
import ru.spbstu.common.extenstions.setStatusBarColor
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.common.utils.ToolbarFragment
import ru.spbstu.feature.R
import ru.spbstu.feature.databinding.FragmentDebtBinding
import ru.spbstu.feature.debt.presentation.adapter.DebtAdapter
import ru.spbstu.feature.di.FeatureApi
import ru.spbstu.feature.di.FeatureComponent
import ru.spbstu.feature.event.presentation.EventFragment
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
        requireActivity().setStatusBarColor(R.color.background_primary)
        requireView().setLightStatusBar()
        initAdapter()
    }

    override fun inject() {
        FeatureUtils.getFeature<FeatureComponent>(this, FeatureApi::class.java)
            .debtComponentFactory()
            .create(this)
            .inject(this)
    }

    override fun setupFromArguments(args: Bundle) {
        super.setupFromArguments(args)
        val id = args.getLong(BUNDLE_KEY)
        viewModel.roomId = id
        viewModel.getData(id)
    }

    private fun initAdapter() {
        adapter = DebtAdapter(viewModel.debts.value.yourParticipantId, viewModel::onDebtChecked)
        binding.frgDebtRvDebts.adapter = adapter
    }

    override fun subscribe() {
        super.subscribe()
        viewModel.debts.observe {
            if (it.purchases.isEmpty()) {
                binding.frgDebtIvShare.visibility = View.VISIBLE
                binding.frgDebtTvNoDebts.visibility = View.VISIBLE
                binding.frgDebtRvDebts.visibility = View.GONE
                binding.frgDebtTvTotalDebt.visibility = View.GONE
            } else {
                binding.frgDebtIvShare.visibility = View.GONE
                binding.frgDebtTvNoDebts.visibility = View.GONE
                binding.frgDebtRvDebts.visibility = View.VISIBLE
                binding.frgDebtTvTotalDebt.visibility = View.VISIBLE
                var total = 0.0
                it.purchases.forEach { expense ->
                    if (expense.users.containsKey(it.yourParticipantId) &&
                        expense.users[it.yourParticipantId] == false &&
                        expense.buyer.id != viewModel.debts.value.yourParticipantId) {
                        total += expense.price / expense.users.size
                    }
                }
                binding.frgDebtTvTotalDebt.text = getString(R.string.total_debt_template, total)
            }
            adapter.yourId = it.yourParticipantId
            adapter.bindData(it.purchases)
        }
    }

    companion object {
        private val TAG = DebtFragment::class.java.simpleName
        val BUNDLE_KEY = "${TAG}_BUNDLE_KEY"

        fun makeBundle(id: Long): Bundle {
            val bundle = Bundle()
            bundle.putLong(BUNDLE_KEY, id)
            return bundle
        }
    }
}