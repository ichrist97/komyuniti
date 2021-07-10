
package com.example.komyuniti.ui.settings

import android.content.pm.PackageManager
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.komyuniti.R
import com.example.komyuniti.databinding.FragmentSettingsBinding
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.komyuniti.MainActivity

//import com.example.komyuniti.ui.feed.SettingsViewModel

class SettingsFragment : Fragment() {

    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var fragmentSettingsBinding: FragmentSettingsBinding

    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000;

        //Permission code
        private val PERMISSION_CODE = 1001;
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        settingsViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)

        fragmentSettingsBinding = FragmentSettingsBinding.inflate(inflater, container, false)

        val root: View = fragmentSettingsBinding.root

        initProfile(fragmentSettingsBinding)
        editProfilePic(fragmentSettingsBinding)


        return root
    }

    private fun editProfilePic(binding: FragmentSettingsBinding) {
        // handle the permissions response
        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your app
                    true
                } else {
                    Toast.makeText(
                        activity,
                        "You denied permission to access gallery",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }
        //BUTTON CLICK
        binding.fltbtProfilePicUpload.setOnClickListener {
            //check runtime permission
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // You can use the API that requires the permission.
                    pickImageFromGallery()
                }
                shouldShowRequestPermissionRationale("WRITE_EXTERNAL_STORAGE") -> {
                    // In an educational UI, explain to the user why your app requires this permission
                    Toast.makeText(
                        activity,
                        "Permission needed to access gallery",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    // You can directly ask for the permission.
                    // The registered ActivityResultCallback gets the result of this request.
                    requestPermissionLauncher.launch(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
/*                    ActivityCompat.requestPermissions(
                        activity as Activity,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        IMAGE_PICK_CODE
                    )*/
                }

            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                                //Granted therefore continue
                                pickImageFromGallery()
                } else {
                    // user denied, explain why action not available
                    Toast.makeText(
                        activity,
                        "Permission needed to access gallery",
                        Toast.LENGTH_SHORT
                    ).show()
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


    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    //handle result of picked image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            fragmentSettingsBinding.imageView.setImageURI(data?.data)
        }
    }

    private fun initProfile(binding: FragmentSettingsBinding) {
        binding.tvBack.setOnClickListener { view: View ->
            Navigation.findNavController(view)
                .navigate(R.id.action_settingsFragment_to_navigation_profile2)
        }
    }
}
