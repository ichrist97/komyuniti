package com.example.komyuniti.ui.komyuniti

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.apollographql.apollo.ApolloClient
import com.example.komyuniti.MainViewModel
import com.example.komyuniti.R
import com.example.komyuniti.databinding.FragmentKomyunitiBinding
import com.example.komyuniti.models.Komyuniti
import com.example.komyuniti.ui.events.EventAdapter
import com.example.komyuniti.ui.komyuniti.addMember.AddKomyunitiMemberViewModel
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch

class KomyunitiFragment : Fragment() {

    private lateinit var viewModel: KomyunitiViewModel
    private lateinit var binding: FragmentKomyunitiBinding
    private lateinit var activityViewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProvider(requireActivity()).get(KomyunitiViewModel::class.java)
        activityViewModel = ViewModelProvider(
            activity as ViewModelStoreOwner
        ).get(MainViewModel::class.java)
        binding = FragmentKomyunitiBinding.inflate(inflater, container, false)
        val apollo = activityViewModel.getApollo(requireContext())

        loadMember(apollo)
        loadEvents(apollo)

        animationFloatingButton()
        initTabNavigation()
        initNavigation()
        setUIData()
        initSettings(apollo)

        return binding.root
    }

    private fun loadMember(apollo: ApolloClient) {
        val komyunitiId = viewModel.getKomyunitiId().value
        if (komyunitiId != null) {
            // load data
            lifecycleScope.launch {
                viewModel.fetchKomyuniti(apollo, komyunitiId)
                displayMember()
            }
        }
    }

    private fun loadEvents(apollo: ApolloClient) {
        val komyunitiId = viewModel.getKomyunitiId().value
        if (komyunitiId != null) {
            // load data
            lifecycleScope.launch {
                viewModel.fetchEvents(apollo, komyunitiId)
            }
        }
    }

    private fun displayMember() {
        val member = viewModel.getKomyuniti().value?.members
        if (member != null && member.isNotEmpty()) {
            val adapter = MemberAdapter(
                member,
                requireActivity(),
                R.id.action_komyunitiFragment_to_friendProfileFragment
            )
            binding.memberRecyclerView.adapter = adapter
            binding.memberRecyclerView.visibility = VISIBLE
            binding.eventRecyclerView.visibility = GONE
            binding.komyunitiRvStatus.visibility = GONE

            // init observer
            viewModel.getKomyuniti().observe(viewLifecycleOwner, {
                if (it?.members != null) {
                    adapter.setData(it.members)
                } else {
                    Toast.makeText(
                        activity,
                        "Could not load member",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        } else {
            binding.memberRecyclerView.visibility = GONE
            binding.eventRecyclerView.visibility = GONE
            binding.komyunitiRvStatus.visibility = VISIBLE
            binding.komyunitiRvStatus.text = "This komyuniti has no member"
        }
    }

    private fun displayUpcomingEvents() {
        val data = viewModel.getUpcomingEvents().value
        if (data != null && data.isNotEmpty()) {
            val adapter = EventAdapter(
                data,
                requireActivity(),
                R.id.action_komyunitiFragment_to_eventFragment
            )
            binding.eventRecyclerView.adapter = adapter
            binding.memberRecyclerView.visibility = GONE
            binding.eventRecyclerView.visibility = VISIBLE
            binding.komyunitiRvStatus.visibility = GONE

            // init observer
            viewModel.getUpcomingEvents().observe(viewLifecycleOwner, {
                if (it != null) {
                    adapter.setData(it)
                } else {
                    Toast.makeText(
                        activity,
                        "Could not load upcoming events",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        } else {
            binding.memberRecyclerView.visibility = GONE
            binding.eventRecyclerView.visibility = GONE
            binding.komyunitiRvStatus.visibility = VISIBLE
            binding.komyunitiRvStatus.text = "This komyuniti has no upcoming events"
        }
    }

    private fun displayDoneEvents() {
        val data = viewModel.getDoneEvents().value
        if (data != null && data.isNotEmpty()) {
            val adapter = EventAdapter(
                data,
                requireActivity(),
                R.id.action_komyunitiFragment_to_eventFragment
            )
            binding.eventRecyclerView.adapter = adapter
            binding.memberRecyclerView.visibility = GONE
            binding.eventRecyclerView.visibility = VISIBLE
            binding.komyunitiRvStatus.visibility = GONE

            // init observer
            viewModel.getDoneEvents().observe(viewLifecycleOwner, {
                if (it != null) {
                    adapter.setData(it)
                } else {
                    Toast.makeText(
                        activity,
                        "Could not load past events",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        } else {
            binding.memberRecyclerView.visibility = GONE
            binding.eventRecyclerView.visibility = GONE
            binding.komyunitiRvStatus.visibility = VISIBLE
            binding.komyunitiRvStatus.text = "This komyuniti has no past events"
        }
    }

    private fun initTabNavigation() {
        //behaviour when tabs are clicked
        val tabLayout = binding.komyunitiTabLayout
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                // member
                if (tab.position == 0) {
                    displayMember()
                } else if (tab.position == 1) {
                    // display upcoming events
                    displayUpcomingEvents()
                } else if (tab.position == 2) {
                    // display done events
                    displayDoneEvents()
                } else {
                    Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun animationFloatingButton() {
        //sets on click listener on floating button and displays expanded floating btns with animation
        var clicked = false
        binding.btnKomyunitiCreateEvent.hide()
        binding.btnkomyunitiAddMember.hide()
        val showAnim = AnimationUtils.loadAnimation(activity, R.anim.scale_up);
        val hideAnim = AnimationUtils.loadAnimation(activity, R.anim.scale_down)
        val rotateOpenAnim = AnimationUtils.loadAnimation(activity, R.anim.rotate_open);
        val rotateCloseAnim = AnimationUtils.loadAnimation(activity, R.anim.rotate_close);

        binding.btnKomyunitiFloat.setOnClickListener {
            clicked = if (!clicked) {
                binding.btnKomyunitiCreateEvent.show()
                binding.btnkomyunitiAddMember.show()
                binding.btnKomyunitiCreateEvent.startAnimation(showAnim)
                binding.btnkomyunitiAddMember.startAnimation(showAnim)
                binding.btnKomyunitiFloat.startAnimation(rotateOpenAnim)
                true
            } else {
                binding.btnKomyunitiCreateEvent.startAnimation(hideAnim)
                binding.btnkomyunitiAddMember.startAnimation(hideAnim)
                binding.btnKomyunitiCreateEvent.hide()
                binding.btnkomyunitiAddMember.hide()
                binding.btnKomyunitiFloat.startAnimation(rotateCloseAnim)
                false
            }
        }


    }

    private fun initNavigation() {
        // go back
        binding.btnKomyunitiGoBack.setOnClickListener {
            Navigation.findNavController(it)
                .navigate(R.id.action_komyunitiFragment_to_navigation_profile)
        }

        // add member
        binding.btnkomyunitiAddMember.setOnClickListener {
            // set komyunitiId in viewModel
            val addMemberViewModel =
                ViewModelProvider(requireActivity()).get(AddKomyunitiMemberViewModel::class.java)
            addMemberViewModel.setKomyuniti(viewModel.getKomyuniti().value as Komyuniti)

            // route to new view
            Navigation.findNavController(it)
                .navigate(R.id.action_komyunitiFragment_to_fragmentAddMember)
        }

        // create event from komyuniti
        binding.btnKomyunitiCreateEvent.setOnClickListener {
            // TODO set komyuniti in new event view
            // route to new event view
            Navigation.findNavController(it)
                .navigate(R.id.action_komyunitiFragment_to_newEvent)
        }
    }

    private fun setUIData() {
        // set komyuniti name
        viewModel.getKomyuniti().observe(viewLifecycleOwner, {
            binding.komyunitiHeaderName.text = it?.name

            // only show settings for admin of komyuniti
            binding.btnKomyunitiSettings.visibility = GONE  // hide by default
            val preferences = activity?.getSharedPreferences("Auth", Context.MODE_PRIVATE)!!
            val userId = preferences.getString("curUserId", null)
            if (userId != null && userId == it?.admin?.id) {
                // show for admin
                binding.btnKomyunitiSettings.visibility = VISIBLE
            }
        })
    }

    private fun initSettings(apollo: ApolloClient) {
        binding.btnKomyunitiSettings.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), it)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.settingsDeleteKomyuniti -> {
                        // open dialog to make sure user wants to delete
                        val builder = AlertDialog.Builder(requireContext())
                        builder.setMessage("Are you sure to delete this komyuniti? This action cannot be reversed!")
                            .setPositiveButton("Delete") { dialog, id ->
                                // delete komyuniti
                                val komyunitiId = viewModel.getKomyunitiId().value as String
                                lifecycleScope.launch {
                                    val result = viewModel.deleteKomyuniti(apollo, komyunitiId)
                                    if (result) {
                                        Toast.makeText(
                                            activity,
                                            "Komyuniti deleted!",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        // navigate back to profile
                                        Navigation.findNavController(it)
                                            .navigate(R.id.action_komyunitiFragment_to_navigation_profile)
                                    } else {
                                        Toast.makeText(
                                            activity,
                                            "Could not delete this komyuniti",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            }.setNegativeButton("Cancel", { dialog, id ->
                                // user cancels dialog
                            })
                        builder.create().show()

                        true
                    }
                    else -> false
                }
            }

            // display menu
            popupMenu.inflate(R.menu.komyuniti_menu)

            // show icons of menu items
            try {
                val fieldPopUp = PopupMenu::class.java.getDeclaredField("mPopup")
                fieldPopUp.isAccessible = true
                val mPopup = fieldPopUp.get(popupMenu)
                mPopup.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                    .invoke(mPopup, true)
            } catch (e: Exception) {
                Log.e("KomyunitiFragment", "Error showing icon")
            } finally {
                popupMenu.show()
            }
        }
    }

}