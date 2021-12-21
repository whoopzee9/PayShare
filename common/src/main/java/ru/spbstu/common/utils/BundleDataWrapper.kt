package ru.spbstu.common.utils

import android.os.Bundle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BundleDataWrapper {
    private val _bundleData: MutableStateFlow<Bundle> = MutableStateFlow(Bundle())
    val bundleData: StateFlow<Bundle> get() = _bundleData

    fun setBundle(bundle: Bundle) {
        _bundleData.value = bundle
    }
}