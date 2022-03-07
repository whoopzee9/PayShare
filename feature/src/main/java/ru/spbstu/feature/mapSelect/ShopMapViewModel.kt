package ru.spbstu.feature.mapSelect

import android.annotation.SuppressLint
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import ru.spbstu.common.utils.BundleDataWrapper
import ru.spbstu.feature.domain.model.Shop

@SuppressLint("NewApi")
class ShopMapViewModel(private val dataWrapper: BundleDataWrapper) :
    ViewModel() {

    var shop = Shop()

    fun setCoordinates(latLng: LatLng) {
        shop = shop.copy(latitude = latLng.latitude, longitude = latLng.longitude)
    }

    fun setName(shopName: String) {
        shop = shop.copy(name = shopName)
        dataWrapper.setBundle(
            bundleOf(
                ShopMapFragment.DATA_KEY to shop
            )
        )
    }
}
