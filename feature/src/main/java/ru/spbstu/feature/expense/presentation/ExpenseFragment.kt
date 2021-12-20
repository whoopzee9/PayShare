package ru.spbstu.feature.expense.presentation

import android.view.ViewGroup
import android.widget.Toast
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.common.utils.ToolbarFragment
import ru.spbstu.feature.R
import ru.spbstu.feature.databinding.FragmentExpenseBinding
import ru.spbstu.feature.di.FeatureApi
import ru.spbstu.feature.di.FeatureComponent
import ru.spbstu.feature.domain.model.Expense
import ru.spbstu.feature.shared_adapters.ParticipantUserAdapter
import java.time.format.DateTimeFormatter

class ExpenseFragment : ToolbarFragment<ExpenseViewModel>(
    R.layout.fragment_expense,
    R.string.empty_toolbar,
    ToolbarType.BACK
) {

    override val binding by viewBinding(FragmentExpenseBinding::bind)

    private val participantUserAdapter by lazy {
        ParticipantUserAdapter(onItemClick = {
            Toast.makeText(requireContext(), "On user click", Toast.LENGTH_SHORT).show()
        }, onDeleteClick = {
            Toast.makeText(requireContext(), "On delete user click", Toast.LENGTH_SHORT).show()
        })
    }

    override fun getToolbarLayout(): ViewGroup = binding.frgExpenseLayoutToolbar.root

    override fun setupViews() {
        super.setupViews()
        binding.frgExpenseRvUsers.adapter = participantUserAdapter
    }

    override fun subscribe() {
        super.subscribe()

        viewModel.purchase.observe {
            setPurchaseInfo(it)
        }
        viewModel.users.observe {
            participantUserAdapter.bindData(it)
        }
    }

    private fun setPurchaseInfo(expense: Expense) {
        binding.frgExpenseLayoutPurchaseInfo.itemPurchaseInfoPrice.text = expense.price.toString()
        binding.frgExpenseLayoutPurchaseInfo.itemPurchaseInfoTvBuyerName.text =
            expense.buyer.getFullName()
        binding.frgExpenseLayoutPurchaseInfo.itemPurchaseInfoTvDate.text =
            expense.date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        binding.frgExpenseLayoutPurchaseInfo.itemPurchaseInfoTvDescription.text = expense.name
    }

    override fun inject() {
        FeatureUtils.getFeature<FeatureComponent>(this, FeatureApi::class.java)
            .expenseComponentFactory()
            .create(this)
            .inject(this)
    }
}
