package ru.spbstu.feature.qr_code.presentation

import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extenstions.setDebounceClickListener
import ru.spbstu.common.extenstions.setLightStatusBar
import ru.spbstu.common.extenstions.setStatusBarColor
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.common.utils.PermissionUtils
import ru.spbstu.common.utils.ToolbarFragment
import ru.spbstu.feature.R
import ru.spbstu.feature.databinding.FragmentQrCodeBinding
import ru.spbstu.feature.databinding.FragmentQrCodeSuccessDialogBinding
import ru.spbstu.feature.di.FeatureApi
import ru.spbstu.feature.di.FeatureComponent
import timber.log.Timber
import java.time.format.DateTimeFormatter

class QrCodeFragment : ToolbarFragment<QrCodeViewModel>(
    R.layout.fragment_qr_code,
    R.string.scanning,
    ToolbarType.BACK
) {

    private var eventDetailsDialog: BottomSheetDialog? = null
    private lateinit var eventDetailsDialogBinding: FragmentQrCodeSuccessDialogBinding

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
                                true
//                                Handler(Looper.getMainLooper()).postDelayed({
//                                    binding.frgQrCodeCameraView.open()
//                                }, STATUS_DELAY)
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
        viewModel.getEventInfo(barcode) { event ->
            if (eventDetailsDialog == null) {
                eventDetailsDialog =
                    BottomSheetDialog(requireContext(), R.style.BottomSheetDialog_Theme)
                eventDetailsDialogBinding =
                    FragmentQrCodeSuccessDialogBinding.inflate(layoutInflater, binding.root, false)

                eventDetailsDialog?.setContentView(eventDetailsDialogBinding.root)
            }

            var totalPrice = 0.0
            event.expenses.forEach { totalPrice += it.price }
            eventDetailsDialogBinding.frgTimePickerDialogTvTotalPrice.text =
                binding.root.context.getString(R.string.total_price_template, totalPrice)
            eventDetailsDialogBinding.frgTimePickerDialogTvTotalAmount.text =
                event.expenses.size.toString()
            eventDetailsDialogBinding.frgTimePickerDialogTvTime.text =
                event.date.format(DateTimeFormatter.ofPattern("HH:mm"))
            eventDetailsDialogBinding.frgTimePickerDialogTvDate.text =
                event.date.format(DateTimeFormatter.ofPattern("dd.MM.yy"))
            eventDetailsDialogBinding.frgTimePickerDialogTvEventTitle.text = event.name

            when (event.expenses.size) {
                0 -> {
                    eventDetailsDialogBinding.frgTimePickerDialogTvPurchase1.visibility = View.GONE
                    eventDetailsDialogBinding.frgTimePickerDialogTvPurchase2.visibility = View.GONE
                }
                1 -> {
                    eventDetailsDialogBinding.frgTimePickerDialogTvPurchase1.visibility =
                        View.VISIBLE
                    eventDetailsDialogBinding.frgTimePickerDialogTvPurchase2.visibility = View.GONE
                    eventDetailsDialogBinding.frgTimePickerDialogTvPurchase1.text =
                        event.expenses[0].name
                }
                else -> {
                    eventDetailsDialogBinding.frgTimePickerDialogTvPurchase1.visibility =
                        View.VISIBLE
                    eventDetailsDialogBinding.frgTimePickerDialogTvPurchase2.visibility =
                        View.VISIBLE
                    eventDetailsDialogBinding.frgTimePickerDialogTvPurchase1.text =
                        event.expenses[0].name
                    eventDetailsDialogBinding.frgTimePickerDialogTvPurchase2.text =
                        event.expenses[1].name
                }
            }

            eventDetailsDialogBinding.frgTimePickerDialogMbJoin.setDebounceClickListener {
                viewModel.openEventFragment(event.id)
                eventDetailsDialog?.dismiss()
            }

            eventDetailsDialog?.setOnDismissListener {
                binding.frgQrCodeCameraView.open()
            }

            eventDetailsDialog?.behavior?.state = BottomSheetBehavior.STATE_EXPANDED
            eventDetailsDialog?.show()
        }
    }

    companion object {
        private const val STATUS_DELAY: Long = 3000
    }
}
