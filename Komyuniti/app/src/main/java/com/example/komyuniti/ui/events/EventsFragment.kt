package com.example.komyuniti.ui.events

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.apollographql.apollo.ApolloClient
import com.example.komyuniti.MainViewModel
import com.example.komyuniti.R
import com.example.komyuniti.databinding.FragmentEventsBinding
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch

class EventsFragment : Fragment() {

    private lateinit var eventsViewModel: EventsViewModel
    private lateinit var fragmentEventsBinding: FragmentEventsBinding
    private lateinit var activityViewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        eventsViewModel =
            ViewModelProvider(this).get(EventsViewModel::class.java)
        activityViewModel = ViewModelProvider(
            activity as ViewModelStoreOwner
        ).get(MainViewModel::class.java)
        fragmentEventsBinding = FragmentEventsBinding.inflate(inflater, container, false)
        val apollo = activityViewModel.getApollo(requireContext())

        val root: View = fragmentEventsBinding.root

        loadUpcomingEvents(apollo)
        navigation(fragmentEventsBinding)
        manageTabs()

        return root
    }

    private fun loadUpcomingEvents(apollo: ApolloClient) {
        // get userId from preferences
        val preferences: SharedPreferences =
            context?.getSharedPreferences("Auth", Context.MODE_PRIVATE)!!
        val curUserId = preferences.getString("curUserId", null)
        if (curUserId == null) {
            Toast.makeText(activity, "Could not load events", Toast.LENGTH_LONG).show()
            return
        }
        // fetch data from backend
        lifecycleScope.launch {
            eventsViewModel.fetchEvents(apollo, curUserId)

            // display upcoming at start
            displayUpcomingEvents()
        }
    }

    private fun navigation(binding: FragmentEventsBinding) {
        //navigate to new event via floating btn
        binding.flbtnAdd.setOnClickListener { view: View ->
            Navigation.findNavController(view)
                .navigate(R.id.action_navigation_events_to_newEvent)
        }
    }

    private fun displayUpcomingEvents() {
        // change adapter and observer
        val data = eventsViewModel.getUpcomingEvents().value
        if (data != null && data.isNotEmpty()) {
            // set ui
            fragmentEventsBinding.tvEventsEmpty.visibility = GONE
            fragmentEventsBinding.rvEvents.visibility = VISIBLE

            // set data
            val adapter = EventAdapter(
                data,
                requireActivity(),
                R.id.action_navigation_events_to_eventFragment
            )
            fragmentEventsBinding.rvEvents.adapter = adapter
            eventsViewModel.getUpcomingEvents().observe(viewLifecycleOwner, {
                if (it != null) {
                    // update upcoming events data
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
            // set ui
            fragmentEventsBinding.tvEventsEmpty.visibility = VISIBLE
            fragmentEventsBinding.tvEventsEmpty.text = "There are no upcoming events"
            fragmentEventsBinding.rvEvents.visibility = GONE
        }
    }

    private fun displayDoneEvents() {
        // change adapter and observer
        val data = eventsViewModel.getDoneEvents().value
        if (data != null && data.isNotEmpty()) {
            // set ui
            fragmentEventsBinding.tvEventsEmpty.visibility = GONE
            fragmentEventsBinding.rvEvents.visibility = VISIBLE

            // set data
            val adapter = EventAdapter(
                data,
                requireActivity(),
                R.id.action_navigation_events_to_eventFragment
            )
            fragmentEventsBinding.rvEvents.adapter = adapter
            eventsViewModel.getDoneEvents().observe(viewLifecycleOwner, {
                if (it != null) {
                    // update upcoming events data
                    adapter.setData(it)
                } else {
                    Toast.makeText(
                        activity,
                        "Could not load open events",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        } else {
            // set ui
            fragmentEventsBinding.tvEventsEmpty.visibility = VISIBLE
            fragmentEventsBinding.tvEventsEmpty.text = "There are no open events"
            fragmentEventsBinding.rvEvents.visibility = GONE
        }
    }

    private fun manageTabs() {
        val tabLayout = fragmentEventsBinding.tabsInEvents
        //val binding = fragmentEventsBinding
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                // upcoming events
                if (tab.position == 0) {
                    displayUpcomingEvents()
                } else if (tab.position == 1) {
                    displayDoneEvents()
                } else {
                    Toast.makeText(
                        activity,
                        "Something went wrong with Tab Position " + tab.position.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                Log.d("Profile Tabs", tab.position.toString())
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

    }

}

