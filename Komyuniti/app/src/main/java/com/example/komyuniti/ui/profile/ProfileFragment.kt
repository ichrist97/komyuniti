package com.example.komyuniti.ui.profile

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.komyuniti.MainActivity
import com.example.komyuniti.MainViewModel
import com.example.komyuniti.R
import com.example.komyuniti.databinding.FragmentProfileBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.security.*
import com.budiyev.android.codescanner.*
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var activityViewModel: MainViewModel

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var preferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activityViewModel = ViewModelProvider(
            activity as ViewModelStoreOwner
        ).get(MainViewModel::class.java)
        profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        // init shared preferences
        preferences = activity?.getSharedPreferences("Auth", Context.MODE_PRIVATE)!!

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.profileHeaderTitle
        profileViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        generateQRCode(binding)
        addFriend(binding)

        initLogout(binding)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initLogout(binding: FragmentProfileBinding) {
        binding.profileLogoutBtn.setOnClickListener { view: View ->
            //TODO: connection to backend quit user session
            Navigation.findNavController(view)
                .navigate(R.id.action_navigation_profile_to_loginFragment)
            (activity as MainActivity).setMainNavigationController()
        }
    }

    private fun addFriend(binding: FragmentProfileBinding) {
        binding.addFriendBtn.setOnClickListener { view: View ->
            // TODO route to actual scan fragment
            Navigation.findNavController(view)
                .navigate(R.id.action_navigation_profile_to_scanFragment)
        }

    }

    private fun generateQRCode(binding: FragmentProfileBinding) {
        // load content
        val keyPair = loadKeyPair()
        if (keyPair != null) {
            lifecycleScope.launch {
                val bitmap = profileViewModel.generateQRBitmap(
                    activityViewModel.getApollo(requireContext()),
                    keyPair
                )

                if (bitmap != null) {
                    // display qr code
                    binding.qrCode.setImageBitmap(bitmap)
                } else {
                    // error handling
                    Toast.makeText(activity, "QR Code could not be loaded.", Toast.LENGTH_LONG)
                        .show()
                }
            }
        } else {
            // error handling
            Toast.makeText(activity, "QR Code could not be loaded.", Toast.LENGTH_LONG).show()
        }

    }

    private fun loadKeyPair(): KeyPair? {
        val keyAlias = "UserKey"
        val keyStore: KeyStore = KeyStore.getInstance("AndroidKeyStore").apply {
            load(null)
        }
        val entry: KeyStore.Entry = keyStore.getEntry(keyAlias, null)
        if (entry !is KeyStore.PrivateKeyEntry) {
            Log.w("PROFILE", "Not an instance of a PrivateKeyEntry")
            return null
        }
        val privateKey: PrivateKey = entry.privateKey
        val publicKey: PublicKey = keyStore.getCertificate(keyAlias).publicKey
        return KeyPair(publicKey, privateKey)
    }
}