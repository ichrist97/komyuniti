
package com.example.komyuniti.ui.settings

import UpdatePasswordMutation
import UpdateUserDetailsMutation
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
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
import com.example.komyuniti.MainActivity
import com.example.komyuniti.MainViewModel
import com.example.komyuniti.R
import com.example.komyuniti.databinding.FragmentSettingsBinding
import com.example.komyuniti.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.system.exitProcess


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

        settingsViewModel = ViewModelProvider(requireActivity()).get(SettingsViewModel::class.java)
        activityViewModel = ViewModelProvider(activity as ViewModelStoreOwner).get(MainViewModel::class.java)


        fragmentSettingsBinding = FragmentSettingsBinding.inflate(inflater, container, false)

        val root: View = fragmentSettingsBinding.root

        initProfile(fragmentSettingsBinding)
        editProfilePic(fragmentSettingsBinding)
        initSave(fragmentSettingsBinding)
        displayInfo(fragmentSettingsBinding)
        initChangePassword(fragmentSettingsBinding)
        initLogout(fragmentSettingsBinding)
        return root
    }

    private fun initChangePassword(binding: FragmentSettingsBinding) {
        binding.tvChangePassword.setOnClickListener {
            updatePassword(binding)
        }
    }

    private fun initLogout(binding: FragmentSettingsBinding) {
        binding.settingsLogoutBtn.setOnClickListener { view: View ->
            // remove token and curUser from preferences
            val preferences: SharedPreferences =
                context?.getSharedPreferences("Auth", Context.MODE_PRIVATE)!!
            // use commit as apply doesnt work in this case because the app will be shutdown
            preferences.edit().remove("accessToken").commit()
            preferences.edit().remove("curUserId").commit()
            val mainAct = activity as MainActivity
            mainAct.makeToast("Komyuniti logout successful")

            /*
            Exit the whole app as workaround to route back to login because introducing
            the login navigation graph in the main navigation leads to an endless loop
             */
            exitProcess(-1)
        }
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
            val mainAct = activity as MainActivity
            if (res.data != null) {
                Log.d("Settings:", "Update Info" + res.data.toString())
                mainAct.makeToast("Succesfully updated username and email!")

            } else {
                mainAct.makeToast("Couldn't update details")
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
            val mainAct = activity as MainActivity
            if (res.data != null) {
                withContext(Dispatchers.Main) {
                    mainAct.makeToast("Successfully updated password")
                }
                binding.etCurrentPassword.editText?.setText("")
                binding.etNewPassword.editText?.setText("")
            } else {
                if(res.errors?.get(0)?.message == "Password is incorrect") {
                    mainAct.makeToast("Current password was incorrect")
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(activity, "Couldn't update password", Toast.LENGTH_LONG).show()
                    }
                }

            }
        }
    }

    private fun initSave(binding: FragmentSettingsBinding) {
        binding.saveSettings.setOnClickListener{
            lifecycleScope.launch {
                updateInfo(binding)
                val old_pw: String = binding.etCurrentPassword.editText?.text.toString()
                val new_pw: String = binding.etNewPassword.editText?.text.toString()
                if (old_pw != "" || new_pw != "") {
                    updatePassword(binding)
                }
            }
            //display profile pic preview if available
            if (this::profilePic.isInitialized) {
                lifecycleScope.launch {
                    settingsViewModel.setProfilePic(profilePic)
                }
            }
            hideKeyboard()
        }

    }

    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
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
    override fun onActivityResult(requestCode: Int, resultCode: Int, imageData: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            lifecycleScope.launch {
                fragmentSettingsBinding.imageView.setImageURI(imageData?.data)
                profilePic = imageData?.data!!
            }
        }
    }

    private fun initProfile(binding: FragmentSettingsBinding) {
        binding.settingsGoBack.setOnClickListener { view: View ->
            Navigation.findNavController(view)
                .navigate(R.id.action_settingsFragment_to_navigation_profile2)
        }
    }
}
