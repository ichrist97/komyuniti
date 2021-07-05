package com.example.komyuniti.ui.profile

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.coroutines.await
import com.example.komyuniti.MainActivity
import com.example.komyuniti.MainViewModel
import com.example.komyuniti.R
import com.example.komyuniti.databinding.FragmentProfileBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.*


import java.security.*
import com.budiyev.android.codescanner.*
import com.example.komyuniti.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import type.LoginInput


class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var activityViewModel: MainViewModel

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var komyunitis = mutableListOf<KomyunitiData>()
    private var friends = mutableListOf<FriendData>()

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

        //navigation
        (activity as MainActivity).setMainNavigationController()

        // init shared preferences
        preferences = activity?.getSharedPreferences("Auth", Context.MODE_PRIVATE)!!

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.profileHeaderTitle
        profileViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        //display all komyunitis connecting komyuniti Adapter
        binding.qrCode.visibility = VISIBLE
        binding.rvKomyunitiList.layoutManager = LinearLayoutManager(activity as MainActivity)
        binding.rvKomyunitiList.adapter = KomyunitiListAdapter(komyunitis)
        postToKomyuniti()
        postToFriends()

        //behaviour when tabs are clicked
        val tabLayout = binding.tabLayout
        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                //do stuff here
                if (tab.position == 0) {
                    binding.qrCode.visibility = VISIBLE
                    binding.rvKomyunitiList.adapter = KomyunitiListAdapter(komyunitis)
                    binding.tvKomyunitisTitle.text = "My Komyunitis"

                } else if (tab.position == 1) {
                    //display friends list after tab changed
                    binding.qrCode.visibility = GONE
                    binding.rvKomyunitiList.adapter = FriendAdapter(friends)
                    binding.tvKomyunitisTitle.text = "My Friends"
                } else {
                    Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show()
                }
                Log.d("Profile Tabs", tab.position.toString())
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        animationFloatingButton(binding.flbtnAdd)

        generateQRCode(binding)
        addFriend(binding)

        initLogout(binding)
        initSettings(binding)

        setCurrentUserName()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun addKomyuniti(komyuniti: KomyunitiData) {
        komyunitis.add(komyuniti)
    }

    private fun postToKomyuniti() {
        for (i in 1..8) {
            addKomyuniti(KomyunitiData())
        }
        Log.d("ProfileFragment", KomyunitiData().toString())
        Log.d("ProfileFragment", komyunitis.toString())
    }

    private fun addFriend(friend: FriendData) {
        friends.add(friend)
    }

    private fun postToFriends() {
        for (i in 1..10) {
            addFriend(FriendData())
        }
        Log.d("ProfileFragment", komyunitis.toString())
    }

    private fun animationFloatingButton(addBtn: FloatingActionButton) {
        //sets on click listener on floating button and displays expanded floating btns with animation
        var clicked = false
        binding.addFriendBtn.hide()
        binding.fltbCreateKomyuniti.hide()
        val showAnim = AnimationUtils.loadAnimation(activity, R.anim.scale_up);
        val hideAnim = AnimationUtils.loadAnimation(activity, R.anim.scale_down);
        val rotateOpenAnim = AnimationUtils.loadAnimation(activity, R.anim.rotate_open);
        val rotateCloseAnim = AnimationUtils.loadAnimation(activity, R.anim.rotate_close);

        addBtn.setOnClickListener { view: View ->
            clicked = if (!clicked) {
                binding.addFriendBtn.show()
                binding.fltbCreateKomyuniti.show()
                binding.addFriendBtn.startAnimation(showAnim)
                binding.fltbCreateKomyuniti.startAnimation(showAnim)
                addBtn.startAnimation(rotateOpenAnim)
                true
            } else {
                binding.addFriendBtn.startAnimation(hideAnim)
                binding.fltbCreateKomyuniti.startAnimation(hideAnim)
                binding.addFriendBtn.hide()
                binding.fltbCreateKomyuniti.hide()
                addBtn.startAnimation(rotateCloseAnim)
                false
            }
        }
    }

    private fun initLogout(binding: FragmentProfileBinding) {
        binding.profileLogoutBtn.setOnClickListener { view: View ->
            //TODO: connection to backend quit user session
            Navigation.findNavController(view)
                .navigate(R.id.action_navigation_profile_to_loginFragment)
        }
    }
    private fun initSettings(binding: FragmentProfileBinding) {
        binding.profileSettingsBtn.setOnClickListener { view: View ->
            Navigation.findNavController(view)
                .navigate(R.id.action_navigation_profile_to_settingsFragment)
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

    private fun setCurrentUserName() {
        lifecycleScope.launch {
            //authUser = loginViewModel.login(apollo, email, password)
            var user: User? = profileViewModel.getCurrentUserName(activityViewModel.getApollo(requireContext()))
            if (user == null) {
                Toast.makeText(activity, "No Username available", Toast.LENGTH_SHORT).show()
            } else {
                binding.profileName.text = user.name
            }
        }
    }
}