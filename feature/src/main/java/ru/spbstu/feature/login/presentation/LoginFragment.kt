package ru.spbstu.feature.login.presentation

import android.view.ViewGroup
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.common.utils.ToolbarFragment
import ru.spbstu.feature.R
import ru.spbstu.feature.databinding.FragmentLoginBinding
import ru.spbstu.feature.di.FeatureApi
import ru.spbstu.feature.di.FeatureComponent

class LoginFragment: ToolbarFragment<LoginViewModel>(
    R.layout.fragment_login,
    R.string.error_connection,
    ToolbarType.EMPTY
) {

    override val binding by viewBinding(FragmentLoginBinding::bind)

    override fun getToolbarLayout(): ViewGroup = binding.frgLoginLayoutToolbar.root

    override fun setupViews() {
        super.setupViews()
    }

    override fun inject() {
        FeatureUtils.getFeature<FeatureComponent>(this, FeatureApi::class.java)
            .loginComponentFactory()
            .create(this)
            .inject(this)
    }

    override fun subscribe() {
        super.subscribe()

    }
}