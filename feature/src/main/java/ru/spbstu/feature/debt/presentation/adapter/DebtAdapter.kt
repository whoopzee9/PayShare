package ru.spbstu.feature.debt.presentation.adapter

import android.view.ViewGroup
import ru.spbstu.common.base.BaseAdapter
import ru.spbstu.common.base.BaseViewHolder
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.feature.databinding.ItemPurchaseInfoBinding
import ru.spbstu.feature.domain.model.Expense
import java.time.format.DateTimeFormatter

class DebtAdapter : BaseAdapter<Expense, DebtAdapter.DebtViewHolder>() {

    inner class DebtViewHolder(parent: ViewGroup) :
        BaseViewHolder<Expense, ItemPurchaseInfoBinding>(parent.viewBinding(ItemPurchaseInfoBinding::inflate)) {

        private lateinit var item: Expense

        init {
        }

        override fun bind(item: Expense) {
            this.item = item

            binding.itemPurchaseInfoTvBuyerName.text = "${item.buyer.lastName} ${item.buyer.firstName}"
            binding.itemPurchaseInfoTvDate.text = item.date.format(DateTimeFormatter.ofPattern("dd.MM.yy"))
            binding.itemPurchaseInfoTvDescription.text = item.name
            binding.itemPurchaseInfoPrice.isClickable = false
            val price = item.price / item.users.size
            binding.itemPurchaseInfoPrice.text = price.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DebtViewHolder =
        DebtViewHolder(parent)
}