package ru.spbstu.feature.events.presentation.adapter

import android.view.View
import android.view.ViewGroup
import ru.spbstu.common.base.BaseAdapter
import ru.spbstu.common.base.BaseViewHolder
import ru.spbstu.common.extenstions.setDebounceClickListener
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.feature.R
import ru.spbstu.feature.databinding.ItemEventsBinding
import ru.spbstu.feature.domain.model.Event
import java.time.format.DateTimeFormatter

class EventsAdapter(val onItemClick: (Event) -> Unit) : BaseAdapter<Event, EventsAdapter.EventsViewHolder>() {

    inner class EventsViewHolder(parent: ViewGroup) :
        BaseViewHolder<Event, ItemEventsBinding>(parent.viewBinding(ItemEventsBinding::inflate)) {

        private lateinit var item: Event
        private var prevFavourite = true

        init {
            binding.itemEventsCardView.setDebounceClickListener {
                onItemClick(item)
            }
        }

        override fun bind(item: Event) {
            var totalPrice = 0.0
            this.item = item
            item.expenses.forEach { totalPrice += it.price }
            binding.itemEventsTvTotalPrice.text =
                binding.root.context.getString(R.string.total_price_template, totalPrice)
            binding.itemEventsTvTotalAmount.text = item.expenses.size.toString()
            binding.itemEventsTvTime.text = item.date.format(DateTimeFormatter.ofPattern("HH:mm"))
            binding.itemEventsTvDate.text =
                item.date.format(DateTimeFormatter.ofPattern("dd.MM.yy"))
            binding.itemEventsTvEventTitle.text = item.name

            when (item.expenses.size) {
                0 -> {
                    binding.itemEventsTvPurchase1.visibility = View.GONE
                    binding.itemEventsTvPurchase2.visibility = View.GONE
                }
                1 -> {
                    binding.itemEventsTvPurchase1.visibility = View.VISIBLE
                    binding.itemEventsTvPurchase2.visibility = View.GONE
                    binding.itemEventsTvPurchase1.text = item.expenses[0].name
                }
                else -> {
                    binding.itemEventsTvPurchase1.visibility = View.VISIBLE
                    binding.itemEventsTvPurchase2.visibility = View.VISIBLE
                    binding.itemEventsTvPurchase1.text = item.expenses[0].name
                    binding.itemEventsTvPurchase2.text = item.expenses[1].name
                }
            }

            if (absoluteAdapterPosition == 0) {
                if (item.isFavourite) {
                    binding.itemEventsIvStar.visibility = View.VISIBLE
                    binding.itemEventsTvAll.visibility = View.GONE
                } else {
                    binding.itemEventsIvStar.visibility = View.GONE
                    binding.itemEventsTvAll.visibility = View.VISIBLE
                }
            } else if (item.isFavourite != prevFavourite) {
                binding.itemEventsIvStar.visibility = View.GONE
                binding.itemEventsTvAll.visibility = View.VISIBLE
            } else {
                binding.itemEventsIvStar.visibility = View.GONE
                binding.itemEventsTvAll.visibility = View.GONE
            }
            prevFavourite = item.isFavourite

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsViewHolder =
        EventsViewHolder(parent)
}