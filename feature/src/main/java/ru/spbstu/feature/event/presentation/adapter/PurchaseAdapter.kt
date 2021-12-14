package ru.spbstu.feature.event.presentation.adapter

import android.os.Build
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import ru.spbstu.common.base.BaseAdapter
import ru.spbstu.common.base.BaseViewHolder
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.feature.databinding.ItemPurchaseBinding
import ru.spbstu.feature.domain.model.Expense
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)

class PurchaseAdapter(val onItemClick: (Expense) -> Unit) :
    BaseAdapter<Expense, PurchaseAdapter.PurchaseViewHolder>() {

    inner class PurchaseViewHolder(parent: ViewGroup) :
        BaseViewHolder<Expense, ItemPurchaseBinding>(parent.viewBinding(ItemPurchaseBinding::inflate)) {

        private lateinit var item: Expense
        private var prevFavourite = true

        override fun bind(item: Expense) {

            var totalPrice = 0.0
            this.item = item

            binding.itemPurchaseTvDescription.text = item.description
            binding.itemPurchaseTvDate.text =
                item.date.format(DateTimeFormatter.ofPattern("dd.MM.yy"))
            binding.itemPurchasePrice.text = item.price.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PurchaseViewHolder {
        return PurchaseViewHolder(parent)
    }
}
