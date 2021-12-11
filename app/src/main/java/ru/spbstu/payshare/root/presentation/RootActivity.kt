package ru.spbstu.payshare.root.presentation

import android.content.Intent
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiManager
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import com.vk.api.sdk.exceptions.VKAuthException
import com.vk.api.sdk.utils.VKUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import ru.spbstu.common.base.BaseActivity
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.events.VkAuthEvent
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.payshare.databinding.ActivityRootBinding
import ru.spbstu.payshare.navigation.Navigator
import ru.spbstu.payshare.root.di.RootApi
import ru.spbstu.payshare.root.di.RootComponent
import timber.log.Timber
import javax.inject.Inject


class RootActivity : BaseActivity<RootViewModel>() {

    @Inject
    lateinit var navigator: Navigator

    override val binding: ActivityRootBinding by viewBinding(ActivityRootBinding::inflate)

    override fun setupViews() {
        super.setupViews()
        navigator.attachActivity(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        navigator.detachActivity()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val callback = object: VKAuthCallback {
            override fun onLogin(token: VKAccessToken) {
                Log.d("qwerty", "eventBus")
                lifecycleScope.launch {
                    delay(100)
                    EventBus.getDefault().post(VkAuthEvent(token))
                }
            }

            override fun onLoginFailed(authException: VKAuthException) {
                Timber.e(authException)
            }
        }
        if (data == null || !VK.onActivityResult(requestCode, resultCode, data, callback)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun inject() {
        FeatureUtils.getFeature<RootComponent>(this, RootApi::class.java)
            .mainActivityComponentFactory()
            .create(this)
            .inject(this)
    }
}
