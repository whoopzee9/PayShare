package ru.spbstu.feature.expense.presentation.adapter

import android.view.ViewGroup
import androidx.core.view.isVisible
import coil.load
import coil.transform.CircleCropTransformation
import ru.spbstu.common.base.BaseAdapter
import ru.spbstu.common.base.BaseViewHolder
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.feature.R
import ru.spbstu.feature.databinding.ItemUserBinding
import ru.spbstu.feature.domain.model.User
import ru.spbstu.feature.domain.model.UserBuyed

class PayedUserAdapter() : BaseAdapter<User, PayedUserAdapter.PayedUserViewHolder>() {

    inner class PayedUserViewHolder(parent: ViewGroup) :
        BaseViewHolder<User, ItemUserBinding>(parent.viewBinding(ItemUserBinding::inflate)) {

        private lateinit var item: User

        override fun bind(item: User) {
            this.item = item
            binding.itemUserIbDelete.setImageResource(R.drawable.ic_paid_24)
            binding.itemUserIbDelete.isVisible = false//item.isBought
            binding.itemUserIv.load(item.imageUrl) {
                transformations(CircleCropTransformation())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PayedUserViewHolder {
        return PayedUserViewHolder(parent)
    }
}
