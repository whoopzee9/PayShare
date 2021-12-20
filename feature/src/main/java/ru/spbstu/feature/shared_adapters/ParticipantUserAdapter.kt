package ru.spbstu.feature.shared_adapters

import android.view.ViewGroup
import androidx.core.view.isVisible
import coil.load
import coil.transform.CircleCropTransformation
import ru.spbstu.common.base.BaseAdapter
import ru.spbstu.common.base.BaseViewHolder
import ru.spbstu.common.extenstions.setDebounceClickListener
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.feature.databinding.ItemUserBinding
import ru.spbstu.feature.domain.model.User

class ParticipantUserAdapter(
    val onItemClick: (User) -> Unit,
    val onDeleteClick: (User) -> Unit
) :
    BaseAdapter<User, ParticipantUserAdapter.ParticipantUserViewHolder>() {
    private var isEditMode: Boolean = false

    inner class ParticipantUserViewHolder(parent: ViewGroup) :
        BaseViewHolder<User, ItemUserBinding>(parent.viewBinding(ItemUserBinding::inflate)) {

        private lateinit var item: User

        override fun bind(item: User) {
            this.item = item
            binding.itemUserIbDelete.isVisible = isEditMode
            binding.itemUserIv.load(item.photoLink) {
                transformations(CircleCropTransformation())
            }
            binding.itemUserIv.setDebounceClickListener {
                onItemClick(item)
            }
            binding.itemUserIbDelete.setDebounceClickListener {
                onDeleteClick(item)
            }
        }
    }

    fun changeIsEditMode(isEdit: Boolean) {
        this.isEditMode = isEditMode
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantUserViewHolder {
        return ParticipantUserViewHolder(parent)
    }
}
