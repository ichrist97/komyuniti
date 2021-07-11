package com.example.komyuniti.ui.profile

import android.os.Bundle
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.komyuniti.MainActivity
import com.example.komyuniti.MainViewModel
import com.example.komyuniti.R
import com.example.komyuniti.databinding.FragmentProfileBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.*
import java.security.*
import com.example.komyuniti.models.User
import com.example.komyuniti.ui.komyuniti.KomyunitiViewModel
import com.example.komyuniti.util.loadKeyPair
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.system.exitProcess


class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var activityViewModel: MainViewModel

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var komyunitis = mutableListOf<KomyunitiData>()

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

        //display all komyunitis connecting komyuniti Adapter
        binding.qrCode.visibility = VISIBLE
        binding.rvKomyunitiList.layoutManager = LinearLayoutManager(activity as MainActivity)

        // fetch data
        loadKomyunitis()
        loadFriends()

        initTabNavigation()

        animationFloatingButton(binding.flbtnAdd)

        generateQRCode(binding)
        startScan(binding)

        initLogout(binding)
        initSettings(binding)
        initCreateKomyuniti()

        setCurrentUserName()

        return root
    }

    private fun initTabNavigation() {
        //behaviour when tabs are clicked
        val tabLayout = binding.tabLayout
        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                // qr code
                if (tab.position == 0) {
                    binding.qrCode.visibility = VISIBLE
                    binding.tvScanHint.visibility = VISIBLE
                    binding.recyclerViewStatus.visibility = GONE
                    binding.rvKomyunitiList.visibility = GONE
                } else if (tab.position == 1) {
                    // display komyunitis
                    binding.qrCode.visibility = GONE
                    binding.recyclerViewStatus.visibility = GONE
                    binding.tvScanHint.visibility = GONE

                    val data = profileViewModel.getKomyunitis().value
                    // display komyunitis
                    if (data != null && data.isNotEmpty()) {
                        val adapter = KomyunitiAdapter(data, requireActivity())
                        binding.rvKomyunitiList.adapter = adapter
                        binding.recyclerViewStatus.visibility = GONE
                        binding.rvKomyunitiList.visibility = VISIBLE

                        // observe friends for updates
                        profileViewModel.getKomyunitis().observe(viewLifecycleOwner, {
                            if (it != null) {
                                // update komyunitis data
                                adapter.setData(it)
                            } else {
                                Toast.makeText(
                                    activity,
                                    "Could not load komyunitis",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        })
                    } else {
                        // show status for no friends
                        binding.recyclerViewStatus.visibility = VISIBLE
                        binding.rvKomyunitiList.visibility = GONE
                        binding.recyclerViewStatus.text =
                            "You are not part of any komyunitis yet :-("
                    }

                } else if (tab.position == 2) {
                    //display friends list after tab changed
                    binding.qrCode.visibility = GONE
                    binding.tvScanHint.visibility = GONE

                    val data = profileViewModel.getFriends().value
                    // display friends
                    if (data != null && data.isNotEmpty()) {
                        val adapter = FriendAdapter(data, requireActivity())
                        binding.rvKomyunitiList.adapter = adapter
                        binding.recyclerViewStatus.visibility = GONE
                        binding.rvKomyunitiList.visibility = VISIBLE

                        // observe friends for updates
                        profileViewModel.getFriends().observe(viewLifecycleOwner, {
                            if (it != null) {
                                // update friends data
                                adapter.setData(it)
                            } else {
                                Toast.makeText(
                                    activity,
                                    "Could not load friends",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        })
                    } else {
                        // show status for no friends
                        binding.recyclerViewStatus.visibility = VISIBLE
                        binding.rvKomyunitiList.visibility = GONE
                        binding.recyclerViewStatus.text = "You have no friends yet :-("
                    }
                } else {
                    Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show()
                }
                Log.d("Profile Tabs", tab.position.toString())
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun loadFriends() {
        val curUserId = preferences.getString("curUserId", null)
        if (curUserId == null) {
            Toast.makeText(activity, "Could not load friends", Toast.LENGTH_LONG).show()
            return
        }

        profileViewModel.getFriends(activityViewModel.getApollo(requireContext()), curUserId)
    }

    private fun loadKomyunitis() {
        val curUserId = preferences.getString("curUserId", null)
        if (curUserId == null) {
            Toast.makeText(activity, "Could not load komyunitis", Toast.LENGTH_LONG).show()
            return
        }

        profileViewModel.getKomyunitis(activityViewModel.getApollo(requireContext()), curUserId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun addKomyuniti(komyuniti: KomyunitiData) {
        komyunitis.add(komyuniti)
    }

    private fun animationFloatingButton(addBtn: FloatingActionButton) {
        //sets on click listener on floating button and displays expanded floating btns with animation
        var clicked = false
        binding.addFriendBtn.hide()
        binding.fltbCreateKomyuniti.hide()
        val showAnim = AnimationUtils.loadAnimation(activity, R.anim.scale_up);
        val hideAnim = AnimationUtils.loadAnimation(activity, R.anim.scale_down)
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
            // remove token and curUser from preferences
            val preferences: SharedPreferences =
                context?.getSharedPreferences("Auth", Context.MODE_PRIVATE)!!
            // use commit as apply doesnt work in this case because the app will be shutdown
            preferences.edit().remove("accessToken").commit()
            preferences.edit().remove("curUserId").commit()

            /*
            Exit the whole app as workaround to route back to login because introducing
            the login navigation graph in the main navigation leads to an endless loop
             */
            exitProcess(-1)
        }
    }

    private fun initSettings(binding: FragmentProfileBinding) {
        binding.profileSettingsBtn.setOnClickListener { view: View ->
            Navigation.findNavController(view)
                .navigate(R.id.action_navigation_profile_to_settingsFragment)
        }
    }

    private fun startScan(binding: FragmentProfileBinding) {
        binding.addFriendBtn.setOnClickListener { view: View ->
            Navigation.findNavController(view)
                .navigate(R.id.action_navigation_profile_to_scanFragment)
        }

    }

    private fun generateQRCode(binding: FragmentProfileBinding) {
        // load content
        val keyPair = getKeyPair()
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

    private fun getKeyPair(): KeyPair? {
        // get current user id
        val curUserId = preferences.getString("curUserId", null)
        if (curUserId == null) {
            Toast.makeText(activity, "Error while retrieving keypair", Toast.LENGTH_LONG).show()
            return null
        }
        return loadKeyPair(curUserId)
    }

    private fun setCurrentUserName() {
        lifecycleScope.launch {
            //authUser = loginViewModel.login(apollo, email, password)
            val user: User? =
                profileViewModel.getCurrentUserName(activityViewModel.getApollo(requireContext()))
            if (user == null) {
                Toast.makeText(activity, "No Username available", Toast.LENGTH_SHORT).show()
            } else {
                binding.profileName.text = user.name
            }
        }
    }

    private fun initCreateKomyuniti() {
        binding.fltbCreateKomyuniti.setOnClickListener {
            Navigation.findNavController(it)
                .navigate(R.id.action_navigation_profile_to_fragmentCreateKomyunitiChooseMembers)
        }
    }
}