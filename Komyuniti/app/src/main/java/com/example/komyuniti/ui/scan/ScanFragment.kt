package com.example.komyuniti.ui.scan

import android.Manifest
import android.app.Activity
import android.content.Context.VIBRATOR_SERVICE
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.budiyev.android.codescanner.*
import com.example.komyuniti.MainActivity
import com.example.komyuniti.R
import com.example.komyuniti.databinding.FragmentScanBinding


class ScanFragment : Fragment() {

    private var _binding: FragmentScanBinding? = null
    private lateinit var resultViewModel: ScanResultViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var codeScanner: CodeScanner
    private val cameraRequest = 1888

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        resultViewModel = ViewModelProvider(requireActivity()).get(ScanResultViewModel::class.java)
        _binding = FragmentScanBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val activity = requireActivity()

        // get camera permissions
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.CAMERA
            )
            == PackageManager.PERMISSION_DENIED
        ) {
            // if not granted then ask for it
            ActivityCompat.requestPermissions(
                activity as Activity,
                arrayOf(Manifest.permission.CAMERA),
                cameraRequest
            )
        } else {
            initCamera()
        }

        return root
    }

    private fun vibrate() {
        val vibrator = context?.getSystemService(VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    // This function is called when user accept or decline the permission.
    // Request Code is used to check which permission called this function.
    // This request code is provided when user is prompt for permission.
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            cameraRequest -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    initCamera()
                } else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                    Toast.makeText(
                        activity,
                        "Permission is needed to open the camera",
                        Toast.LENGTH_LONG
                    ).show()
                    // route back to profile
                    Navigation.findNavController(binding.root)
                        .navigate(R.id.action_scanFragment_to_navigation_profile)
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun initCamera() {
        // init code scanner
        val scannerView = binding.scannerView
        val activity = requireActivity()
        codeScanner = CodeScanner(activity, scannerView)

        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not

        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            activity.runOnUiThread {
                vibrate()
                // write scanning result to shared view model and navigate to result view
                resultViewModel.setQR(it.text)
                Navigation.findNavController(binding.root)
                    .navigate(R.id.action_scanFragment_to_scanResultFragment)
            }
        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorC
            activity.runOnUiThread {// allback.SUPPRESS
                Toast.makeText(
                    activity,
                    "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        if (this::codeScanner.isInitialized) {
            codeScanner.startPreview()
        }
    }

    override fun onPause() {
        super.onPause()
        if (this::codeScanner.isInitialized) {
            codeScanner.releaseResources()
        }
    }

}
