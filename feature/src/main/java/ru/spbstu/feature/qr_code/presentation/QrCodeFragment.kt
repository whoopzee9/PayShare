package ru.spbstu.feature.qr_code.presentation

import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.widget.Toast
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extenstions.setLightStatusBar
import ru.spbstu.common.extenstions.setStatusBarColor
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.common.utils.PermissionUtils
import ru.spbstu.common.utils.ToolbarFragment
import ru.spbstu.feature.R
import ru.spbstu.feature.databinding.FragmentQrCodeBinding
import ru.spbstu.feature.di.FeatureApi
import ru.spbstu.feature.di.FeatureComponent
import timber.log.Timber


class QrCodeFragment: ToolbarFragment<QrCodeViewModel>(
    R.layout.fragment_qr_code,
    R.string.scanning,
    ToolbarType.BACK
) {

    override val binding by viewBinding(FragmentQrCodeBinding::bind)

    override fun getToolbarLayout(): ViewGroup = binding.frgQrCodeLayoutToolbar.root

    override fun setupViews() {
        super.setupViews()
        requireActivity().setStatusBarColor(R.color.background_primary)
        requireView().setLightStatusBar()
        initCamera()
    }

    override fun inject() {
        FeatureUtils.getFeature<FeatureComponent>(this, FeatureApi::class.java)
            .qrCodeComponentFactory()
            .create(this)
            .inject(this)
    }

    override fun subscribe() {
        super.subscribe()

    }

    private fun initCamera() {
        PermissionUtils.checkCameraPermissions(requireContext()) {
            val options = BarcodeScannerOptions.Builder().setBarcodeFormats(
                Barcode.FORMAT_QR_CODE,
                Barcode.FORMAT_CODE_128
            ).build()
            val scanner = BarcodeScanning.getClient(options)
            binding.frgQrCodeCameraView.setLifecycleOwner(this)
            var countFrames = 0
            binding.frgQrCodeCameraView.addFrameProcessor { frame ->
                if (binding.frgQrCodeCameraView.isOpened && ++countFrames % 10 == 0) {
                    val inputImage: InputImage = if (frame.dataClass === ByteArray::class.java) {
                        InputImage.fromByteArray(
                            frame.getData(),
                            frame.size.width,
                            frame.size.height,
                            frame.rotationToUser,
                            InputImage.IMAGE_FORMAT_NV21
                        )
                    } else {
                        InputImage.fromMediaImage(frame.getData(), frame.rotationToUser)
                    }

                    scanner.process(inputImage).addOnSuccessListener {
                        if (binding.frgQrCodeCameraView.isOpened) {
                            it.firstOrNull { barcode ->
                                openEventDetailsDialog(barcode.rawValue ?: "")
                                binding.frgQrCodeCameraView.close()
                                Handler(Looper.getMainLooper()).postDelayed({
                                    binding.frgQrCodeCameraView.open()
                                }, STATUS_DELAY)
                            }
                        }
                    }.addOnFailureListener {
                        Timber.i("SCANNER ${it.message}")
                    }
                }
            }
        }
    }

    private fun openEventDetailsDialog(barcode: String) {
        Toast.makeText(requireContext(), barcode, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val STATUS_DELAY: Long = 3000
    }
}