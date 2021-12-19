package ru.spbstu.feature.event.presentation.adapter

import android.view.ViewGroup
import ru.spbstu.common.base.BaseAdapter
import ru.spbstu.common.base.BaseViewHolder
import ru.spbstu.common.extenstions.setDebounceClickListener
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.feature.databinding.ItemPurchaseBinding
import ru.spbstu.feature.domain.model.Expense
import java.time.format.DateTimeFormatter

class PurchaseAdapter(val onItemClick: (Expense) -> Unit, val onLongItemClick: (Expense) -> Unit) :
    BaseAdapter<Expense, PurchaseAdapter.PurchaseViewHolder>() {

    inner class PurchaseViewHolder(parent: ViewGroup) :
        BaseViewHolder<Expense, ItemPurchaseBinding>(parent.viewBinding(ItemPurchaseBinding::inflate)) {

        private lateinit var item: Expense

        override fun bind(item: Expense) {
            this.item = item

            binding.itemPurchaseTvDescription.text = item.description
            binding.itemPurchaseTvDate.text =
                item.date.format(DateTimeFormatter.ofPattern("dd.MM.yy"))
            binding.itemPurchasePrice.text = item.price.toString()
            binding.itemPurchaseCbIsBought.isChecked = item.isBought

            binding.itemPurchaseLayout.setOnLongClickListener {
                onLongItemClick(item)
                true
            }
            binding.itemPurchaseLayout.setDebounceClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PurchaseViewHolder {
        return PurchaseViewHolder(parent)
    }
}
