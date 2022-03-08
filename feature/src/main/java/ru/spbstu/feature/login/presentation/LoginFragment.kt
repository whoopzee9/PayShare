package ru.spbstu.feature.login.presentation

import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKScope
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import ru.spbstu.common.base.BaseFragment
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.events.VkAuthEvent
import ru.spbstu.common.extenstions.setDebounceClickListener
import ru.spbstu.common.extenstions.setLightStatusBar
import ru.spbstu.common.extenstions.setStatusBarColor
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.common.model.EventState
import ru.spbstu.feature.R
import ru.spbstu.feature.databinding.FragmentLoginBinding
import ru.spbstu.feature.di.FeatureApi
import ru.spbstu.feature.di.FeatureComponent
import timber.log.Timber

class LoginFragment: BaseFragment<LoginViewModel>(
    R.layout.fragment_login
) {

    override val binding by viewBinding(FragmentLoginBinding::bind)

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode != 0) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    viewModel.authWithGoogle(account)
                } catch (e: ApiException) {
                    Timber.tag(TAG).e(e)
                }
            }
        }

    override fun setupViews() {
        super.setupViews()
        requireActivity().setStatusBarColor(R.color.background_primary)
        requireView().setLightStatusBar()
        binding.frgLoginMbGoogle.setDebounceClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_key))
                .requestEmail()
                .requestId()
                .build()
            val mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
            mGoogleSignInClient.signOut()

            val signInIntent = mGoogleSignInClient.signInIntent
            resultLauncher.launch(signInIntent)
        }
        binding.frgLoginMbVk.setDebounceClickListener {
            VK.login(requireActivity(), arrayListOf(VKScope.WALL, VKScope.PHOTOS))
        }
    }

    override fun onStart() {
        EventBus.getDefault().register(this)
        super.onStart()
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: VkAuthEvent) {
        viewModel.authWithVK(event.token)
    }

    override fun inject() {
        FeatureUtils.getFeature<FeatureComponent>(this, FeatureApi::class.java)
            .loginComponentFactory()
            .create(this)
            .inject(this)
    }

    override fun subscribe() {
        super.subscribe()
        viewModel.eventState.observe {
            when (it) {
                EventState.Initial -> {
                    binding.frgLoginPbProgress.visibility = View.GONE
                }
                EventState.Progress -> {
                    binding.frgLoginPbProgress.visibility = View.VISIBLE
                }
                EventState.Success -> {
                    binding.frgLoginPbProgress.visibility = View.GONE
                }
                is EventState.Failure -> {
                    binding.frgLoginPbProgress.visibility = View.GONE
                }
            }
        }
    }

    companion object {
        private val TAG = LoginFragment::class.simpleName
    }
}