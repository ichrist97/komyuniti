package com.example.komyuniti.ui.events

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.komyuniti.R
import com.example.komyuniti.databinding.FragmentEventsBinding
import com.google.android.material.tabs.TabLayout

class EventsFragment : Fragment() {

    private lateinit var eventsViewModel: EventsViewModel
    private lateinit var fragmentEventsBinding: FragmentEventsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        eventsViewModel =
            ViewModelProvider(this).get(EventsViewModel::class.java)

        fragmentEventsBinding = FragmentEventsBinding.inflate(inflater, container, false)

        initEventView()

        val root: View = fragmentEventsBinding.root

        navigation(fragmentEventsBinding)
        manageTabs()

        return root
    }

    private fun initEventView() {
        val upcomingAdapter = EventAdapter(eventsViewModel.eventList.value)
        val openAdapter = EventAdapter(eventsViewModel.openEvents.value)
        //initial adapter
        fragmentEventsBinding.rvEvents.adapter = upcomingAdapter
    }

    private fun navigation(binding: FragmentEventsBinding) {
        //navigate to new event via floating btn
        binding.flbtnAdd.setOnClickListener { view: View ->
            Navigation.findNavController(view)
                .navigate(R.id.action_navigation_events_to_newEvent)
        }
    }

    private fun manageTabs() {
        val tabLayout = fragmentEventsBinding.tabsInEvents
        //val binding = fragmentEventsBinding
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == 0) {
                    //access all upcoming events
                    fragmentEventsBinding.rvEvents.adapter =
                        EventAdapter(eventsViewModel.eventList.value)

                } else if (tab.position == 1) {
                    //access all open events
                    fragmentEventsBinding.rvEvents.adapter =
                        EventAdapter(eventsViewModel.openEvents.value)
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

