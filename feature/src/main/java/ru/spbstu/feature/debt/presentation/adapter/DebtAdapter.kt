package ru.spbstu.feature.debt.presentation.adapter

import android.view.View
import android.view.ViewGroup
import ru.spbstu.common.base.BaseAdapter
import ru.spbstu.common.base.BaseViewHolder
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.feature.databinding.ItemPurchaseInfoBinding
import ru.spbstu.feature.domain.model.EventInfo
import ru.spbstu.feature.domain.model.Expense
import java.time.format.DateTimeFormatter

class DebtAdapter(var eventInfo: EventInfo, val onChecked: (Expense, Boolean) -> Unit) :
    BaseAdapter<Expense, DebtAdapter.DebtViewHolder>() {

    inner class DebtViewHolder(parent: ViewGroup) :
        BaseViewHolder<Expense, ItemPurchaseInfoBinding>(parent.viewBinding(ItemPurchaseInfoBinding::inflate)) {

        private lateinit var item: Expense

        init {

        }

        override fun bind(item: Expense) {
            this.item = item

            binding.itemPurchaseInfoTvBuyerName.text =
                "${item.buyer.lastName} ${item.buyer.firstName}"
            binding.itemPurchaseInfoTvDate.text =
                item.date.format(DateTimeFormatter.ofPattern("dd.MM.yy"))
            binding.itemPurchaseInfoTvDescription.text = item.name
            binding.itemPurchaseInfoPrice.isClickable = false
            val price = if (item.users.isEmpty()) 0 else item.price / item.users.size
            binding.itemPurchaseInfoPrice.text = price.toString()
            binding.itemPurchaseInfoCbPaid.visibility =
                if (item.users.containsKey(eventInfo.yourParticipantId)) View.VISIBLE else View.GONE
            binding.itemPurchaseInfoCbPaid.setOnCheckedChangeListener(null)
            binding.itemPurchaseInfoCbPaid.isChecked = item.users[eventInfo.yourParticipantId] ?: false
            binding.itemPurchaseInfoCbPaid.setOnCheckedChangeListener { compoundButton, b ->
                onChecked(item, b)
            }
            binding.itemPurchaseInfoTvBuyerName.text =
                eventInfo.participants.first { it.id == item.buyer.id }.getFullName()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DebtViewHolder =
        DebtViewHolder(parent)
}