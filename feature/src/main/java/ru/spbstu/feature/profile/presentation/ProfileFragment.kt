package ru.spbstu.feature.profile.presentation

import android.view.ViewGroup
import coil.load
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extenstions.setDebounceClickListener
import ru.spbstu.common.extenstions.setLightStatusBar
import ru.spbstu.common.extenstions.setStatusBarColor
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.common.utils.ToolbarFragment
import ru.spbstu.feature.R
import ru.spbstu.feature.databinding.FragmentProfileBinding
import ru.spbstu.feature.di.FeatureApi
import ru.spbstu.feature.di.FeatureComponent


class ProfileFragment: ToolbarFragment<ProfileViewModel>(
    R.layout.fragment_profile,
    R.string.profile,
    ToolbarType.PROFILE
) {

    override val binding by viewBinding(FragmentProfileBinding::bind)

    override fun getToolbarLayout(): ViewGroup = binding.frgProfileLayoutToolbar.root

    override fun setupViews() {
        super.setupViews()
        requireActivity().setStatusBarColor(R.color.background_primary)
        requireView().setLightStatusBar()
        binding.frgProfileIvPicture.clipToOutline = true
        binding.frgProfileLayoutToolbar.includeToolbarIbSecondButton.setDebounceClickListener {
            viewModel.logout()
        }
        viewModel.getUserInfo()
    }

    override fun inject() {
        FeatureUtils.getFeature<FeatureComponent>(this, FeatureApi::class.java)
            .profileComponentFactory()
            .create(this)
            .inject(this)
    }

    override fun subscribe() {
        super.subscribe()
        viewModel.user.observe {
            binding.frgProfileIvPicture.load(it.imageUrl) {
                placeholder(R.drawable.ic_avatar_default_56)
            }
            binding.frgProfileTvName.text = "${it.firstName} ${it.lastName}"
        }
    }
}