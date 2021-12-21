package ru.spbstu.feature.qr_code_sharing.presentation

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extenstions.setDebounceClickListener
import ru.spbstu.common.extenstions.setLightStatusBar
import ru.spbstu.common.extenstions.setStatusBarColor
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.common.utils.ToolbarFragment
import ru.spbstu.feature.R
import ru.spbstu.feature.databinding.FragmentQrCodeSharingBinding
import ru.spbstu.feature.di.FeatureApi
import ru.spbstu.feature.di.FeatureComponent

class QrCodeSharingFragment : ToolbarFragment<QrCodeSharingViewModel>(
    R.layout.fragment_qr_code_sharing,
    R.string.share,
    ToolbarType.BACK
) {

    override val binding by viewBinding(FragmentQrCodeSharingBinding::bind)

    override fun getToolbarLayout(): ViewGroup {
        return binding.frgQrCodeSharingLayoutToolbar.root
    }

    override fun setupViews() {
        super.setupViews()
        setupQrCodeGenerator()
        binding.frgQrCodeSharingMbShare.setDebounceClickListener {
            Toast.makeText(requireContext(), "code: ${viewModel.code}", Toast.LENGTH_SHORT).show()
        }
        requireActivity().setStatusBarColor(R.color.background_primary)
        requireView().setLightStatusBar()
        setupQrCodeFromArguments()
    }

    private fun setupQrCodeFromArguments() {
        requireArguments().getString(BUNDLE_KEY)?.let { viewModel.setupRoomCode(it) }
    }

    private fun setupQrCodeGenerator() {
        lifecycleScope.launch {
            delay(300)
            val code = viewModel.code
            val writer = QRCodeWriter()
            try {
                val bitMatrix = writer.encode(code, BarcodeFormat.QR_CODE, SIDE_SIZE, SIDE_SIZE)
                val width = bitMatrix.width
                val height = bitMatrix.height
                val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
                for (x in 0 until width) {
                    for (y in 0 until height) {
                        bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                    }
                }
                binding.frgQrCodeSharingIvCode.setImageBitmap(bmp)
            } catch (e: Exception) {
            }
        }
    }

    override fun inject() {
        FeatureUtils.getFeature<FeatureComponent>(this, FeatureApi::class.java)
            .qrCodeSharingComponentFactory()
            .create(this)
            .inject(this)
    }

    companion object {
        private val TAG = QrCodeSharingFragment::class.java.simpleName
        val BUNDLE_KEY = "${TAG}_BUNDLE_KEY"
        private const val SIDE_SIZE = 1024
        fun makeBundle(code: String): Bundle {
            val bundle = Bundle()
            bundle.putString(BUNDLE_KEY, code)
            return bundle
        }
    }
}
