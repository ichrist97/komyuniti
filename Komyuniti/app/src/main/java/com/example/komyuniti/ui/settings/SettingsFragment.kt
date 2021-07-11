
package com.example.komyuniti.ui.settings

import UpdatePasswordMutation
import UpdateUserDetailsMutation
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.api.toInput
import com.example.komyuniti.MainViewModel
import com.example.komyuniti.R
import com.example.komyuniti.databinding.FragmentSettingsBinding
import com.example.komyuniti.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


//import com.example.komyuniti.ui.feed.SettingsViewModel

class SettingsFragment : Fragment() {

    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var activityViewModel: MainViewModel

    private lateinit var fragmentSettingsBinding: FragmentSettingsBinding
    private lateinit var profilePic: Uri

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

        settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        activityViewModel = ViewModelProvider(activity as ViewModelStoreOwner).get(MainViewModel::class.java)


        fragmentSettingsBinding = FragmentSettingsBinding.inflate(inflater, container, false)

        val root: View = fragmentSettingsBinding.root

        initProfile(fragmentSettingsBinding)
        editProfilePic(fragmentSettingsBinding)
        initSave(fragmentSettingsBinding)
        displayInfo(fragmentSettingsBinding)

        return root
    }



    private fun displayInfo(binding: FragmentSettingsBinding) {
        lifecycleScope.launch {
            //authUser = loginViewModel.login(apollo, email, password)
            val user: User? =
                settingsViewModel.getCurrentUserDetails(activityViewModel.getApollo(requireContext()))
            if (user == null) {
                Toast.makeText(activity, "No user details available", Toast.LENGTH_SHORT).show()
            } else {
                binding.etName.editText?.setText(user.name)
                binding.etEmailAddress.editText?.setText(user.email)
            }
        }
    }
    private fun updateInfo(binding: FragmentSettingsBinding) {
        // get user input
        val email: Input<String> = binding.etEmailAddress.editText?.text.toString().toInput()
        val name: Input<String> = binding.etName.editText?.text.toString().toInput()
        //request to backend
        val apollo = activityViewModel.getApollo(activity as Context)
        var res: Response<UpdateUserDetailsMutation.Data>
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                res = settingsViewModel.updateCurrentUserDetails(apollo, email, name)
            }
            // on success
            if (res.data != null) {
                Toast.makeText(
                    activity,
                    "Succesfully updated username and email!",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(activity, "Couldn't update details", Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun updatePassword(binding: FragmentSettingsBinding) {
        // get user input
        val old_pw: String = binding.etCurrentPassword.editText?.text.toString()
        val new_pw: String = binding.etNewPassword.editText?.text.toString()
        //request to backend
        val apollo = activityViewModel.getApollo(activity as Context)
        var res: Response<UpdatePasswordMutation.Data>
        //launch coroutine to request password change in backend
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                res = settingsViewModel.updatePassword(apollo, old_pw, new_pw)
            }
            // on success
            if (res.data != null) {
                withContext(Dispatchers.Main) {
                    val toast = Toast.makeText(
                        requireContext(),
                        "Succesfully updated password", Toast.LENGTH_SHORT
                    )
                    toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 0)
                    toast.show()
                }
                binding.etCurrentPassword.editText?.setText("")
                binding.etNewPassword.editText?.setText("")
            } else {
                withContext(Dispatchers.Main) {
                    val toast =
                        Toast.makeText(activity, "Current password is incorrect", Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 0)
                    toast.show()
                }
            }
        }
    }

    private fun initSave(binding: FragmentSettingsBinding) {
        binding.saveSettings.setOnClickListener{
            updateInfo(binding)
            updatePassword(binding)
/*            if(profilePic!=null) {
                val uri:Uri = saveImageToInternalStorage(R.drawable.flower8)
                val profilePicImageView: ImageView = findViewById(R.id.galleryButton)

                // Display the internal storage saved image in image view
                image_view_saved.setImageURI(uri)
            }*/
            //upload image if set

        }

    }
    // Method to save an image to internal storage
/*    private fun saveImageToInternalStorage(drawableId:Int):Uri{
        // Get the image from drawable resource as drawable object
        val drawable = ContextCompat.getDrawable(requireContext(),drawableId)

        // Get the bitmap from drawable object
        val bitmap = (drawable as BitmapDrawable).bitmap

        // Get the context wrapper instance
        val wrapper = ContextWrapper(requireContext())

        // Initializing a new file
        // The bellow line return a directory in internal storage
        var file = wrapper.getDir("images", Context.MODE_PRIVATE)


        // Create a file to save the image
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            // Get the file output stream
            val stream: OutputStream = FileOutputStream(file)

            // Compress bitmap
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

            // Flush the stream
            stream.flush()

            // Close stream
            stream.close()
        } catch (e: IOException){ // Catch the exception
            e.printStackTrace()
        }

        // Return the saved image uri
        return Uri.parse(file.absolutePath)
    }*/


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
    override fun onActivityResult(requestCode: Int, resultCode: Int, imageData: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            fragmentSettingsBinding.imageView.setImageURI(imageData?.data)
            profilePic = imageData?.data!!
//            val input = UserImageUploadInput(imageData?.data)
        }
    }

    private fun initProfile(binding: FragmentSettingsBinding) {
        binding.settingsGoBack.setOnClickListener { view: View ->
            Navigation.findNavController(view)
                .navigate(R.id.action_settingsFragment_to_navigation_profile2)
        }
    }
}
