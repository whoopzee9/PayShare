package ru.spbstu.feature.event.presentation.adapter

import android.util.Log
import android.view.ViewGroup
import ru.spbstu.common.base.BaseAdapter
import ru.spbstu.common.base.BaseViewHolder
import ru.spbstu.common.extenstions.setDebounceClickListener
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.feature.databinding.ItemPurchaseBinding
import ru.spbstu.feature.domain.model.EventInfo
import ru.spbstu.feature.domain.model.Expense
import java.time.format.DateTimeFormatter

class PurchaseAdapter(
    var event: EventInfo,
    val onItemClick: (Expense, Boolean) -> Unit,
    val onLongItemClick: (Expense) -> Unit
) :
    BaseAdapter<Expense, PurchaseAdapter.PurchaseViewHolder>() {

    inner class PurchaseViewHolder(parent: ViewGroup) :
        BaseViewHolder<Expense, ItemPurchaseBinding>(parent.viewBinding(ItemPurchaseBinding::inflate)) {

        private lateinit var item: Expense

        override fun bind(item: Expense) {
            this.item = item

            binding.itemPurchaseTvDescription.text = item.name
            binding.itemPurchaseTvDate.text =
                item.date.format(DateTimeFormatter.ofPattern("dd.MM.yy"))
            binding.itemPurchasePrice.text = item.price.toString()
            Log.d("qwerty", "\nmyID = ${event.yourParticipantId}")
            Log.d("qwerty", "users = ${item.users}")
            Log.d("qwerty", "isJoined = ${item.users.containsKey(event.yourParticipantId)}")
            binding.itemPurchaseCbIsBought.isChecked = item.users.containsKey(event.yourParticipantId)

            binding.itemPurchaseLayout.setOnLongClickListener {
                onLongItemClick(item)
                true
            }
            binding.itemPurchaseLayout.setDebounceClickListener {
                onItemClick(item, !binding.itemPurchaseCbIsBought.isChecked)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PurchaseViewHolder {
        return PurchaseViewHolder(parent)
    }
}
