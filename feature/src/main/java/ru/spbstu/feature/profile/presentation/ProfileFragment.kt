package ru.spbstu.feature.profile.presentation

import android.view.ViewGroup
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.common.utils.ToolbarFragment
import ru.spbstu.feature.R
import ru.spbstu.feature.databinding.FragmentProfileBinding
import ru.spbstu.feature.di.FeatureApi
import ru.spbstu.feature.di.FeatureComponent


class ProfileFragment: ToolbarFragment<ProfileViewModel>(
    R.layout.fragment_profile,
    R.string.error_connection,
    ToolbarType.EMPTY
) {

    override val binding by viewBinding(FragmentProfileBinding::bind)

    override fun getToolbarLayout(): ViewGroup = binding.frgProfileLayoutToolbar.root

    override fun setupViews() {
        super.setupViews()
    }

    override fun inject() {
        FeatureUtils.getFeature<FeatureComponent>(this, FeatureApi::class.java)
            .profileComponentFactory()
            .create(this)
            .inject(this)
    }

    override fun subscribe() {
        super.subscribe()

    }
}