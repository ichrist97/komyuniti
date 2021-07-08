package com.example.komyuniti.ui.events

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.Navigation
import com.apollographql.apollo.ApolloClient
import com.example.komyuniti.MainViewModel
import com.example.komyuniti.R
import com.example.komyuniti.databinding.DoneEventViewFragmentBinding
import com.example.komyuniti.models.Event
import com.example.komyuniti.ui.events.chat.ChatViewModel

class DoneEventViewFragment : Fragment() {

    private lateinit var doneEventViewModel: DoneEventViewModel
    private lateinit var doneEventBinding: DoneEventViewFragmentBinding
    private lateinit var activityViewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activityViewModel = ViewModelProvider(
            activity as ViewModelStoreOwner
        ).get(MainViewModel::class.java)
        doneEventViewModel =
            ViewModelProvider(requireActivity()).get(DoneEventViewModel::class.java)
        doneEventBinding = DoneEventViewFragmentBinding.inflate(inflater, container, false)
        val root: View = doneEventBinding.root
        val apollo = activityViewModel.getApollo(requireContext())

        initNavigation()
        loadData(apollo)

        return root
    }

    private fun loadData(apollo: ApolloClient) {
        val eventId = doneEventViewModel.getEventId().value
        if (eventId != null) {
            // load data
            doneEventViewModel.fetchEvent(apollo, eventId)
            // set observer
            doneEventViewModel.getEvent().observe(viewLifecycleOwner, {
                if (it != null) {
                    doneEventBinding.tvDoneEventName.text = it.name
                    // TODO do more stuff with the data in the new fragment
                }
            })
        } else {
            Toast.makeText(activity, "Cannot load event", Toast.LENGTH_LONG).show()
        }
    }

    private fun initNavigation() {
        // route to event overview
        doneEventBinding.tvDoneEventBackBtn.setOnClickListener {
            Navigation.findNavController(it)
                .navigate(R.id.action_doneEventViewFragment_to_navigation_events)
        }

        // route to chat
        doneEventBinding.flbtnChat.setOnClickListener {
            val event = doneEventViewModel.getEvent().value
            if (event != null) {
                // set event in chatViewModel
                val chatViewModel =
                    ViewModelProvider(requireActivity()).get(ChatViewModel::class.java)
                chatViewModel.setEvent(event)

                // route to chat
                Navigation.findNavController(it)
                    .navigate(R.id.action_doneEventViewFragment_to_chatFragment)
            } else {
                Toast.makeText(activity, "Cannot open the chat currently", Toast.LENGTH_LONG).show()
            }
        }
    }

}