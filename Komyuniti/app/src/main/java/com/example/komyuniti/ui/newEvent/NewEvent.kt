package com.example.komyuniti.ui.newEvent

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.apollographql.apollo.ApolloClient
import com.example.komyuniti.MainViewModel
import com.example.komyuniti.R
import com.example.komyuniti.databinding.FragmentNewEventBinding
import com.example.komyuniti.models.Event
import com.example.komyuniti.ui.event.EventViewModel
import kotlinx.coroutines.launch


class NewEvent : Fragment() {

    private lateinit var viewModel: NewEventViewModel
    private lateinit var binding: FragmentNewEventBinding
    private lateinit var activityViewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProvider(requireActivity()).get(NewEventViewModel::class.java)
        activityViewModel = ViewModelProvider(
            activity as ViewModelStoreOwner
        ).get(MainViewModel::class.java)
        binding = FragmentNewEventBinding.inflate(inflater, container, false)
        val apollo = activityViewModel.getApollo(requireContext())

        initCreateEvent(apollo)
        observeKomyuniti()
        observeMembers()

        navigation(binding)
        return binding.root
    }

    private fun observeKomyuniti() {
        viewModel.getKomyuniti().observe(viewLifecycleOwner, {
            if (it != null) {
                binding.newEventAddKomyunitiText.text = "Change komyuniti"
            } else {
                binding.newEventAddKomyunitiText.text = "Add komyuniti"
            }
        })
    }

    private fun observeMembers() {
        viewModel.getMembers().observe(viewLifecycleOwner, {
            if (it != null) {
                binding.newEventAddMembersText.text = "Change members"
            } else {
                binding.newEventAddMembersText.text = "Add members"
            }
        })
    }

    private fun initCreateEvent(apollo: ApolloClient) {
        binding.createEvent.setOnClickListener { view ->
            // gather info from user input
            val name = binding.eventNameInput.text.toString()
            if (name.isEmpty()) {
                Toast.makeText(activity, "Please pick a name", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val date = binding.eventDateInput.text.toString()
            if (date.isEmpty()) {
                Toast.makeText(activity, "Please pick a date", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // these two are optional
            val address = binding.eventAddressInput.text.toString()
            val komyuniti = viewModel.getKomyuniti().value

            lifecycleScope.launch {
                val result: Event? = viewModel.createEvent(
                    apollo,
                    name,
                    date,
                    address = address,
                    komyunitiId = komyuniti?.id
                )

                // on success route to created event
                if (result != null) {
                    // set eventId in shared view model
                    val eventViewModel =
                        ViewModelProvider(requireActivity()).get(EventViewModel::class.java)
                    eventViewModel.setEventId(result.id)
                    // route to event
                    Navigation.findNavController(view)
                        .navigate(R.id.action_newEvent_to_eventFragment)
                } else {
                    Toast.makeText(activity, "Could not create event", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun navigation(binding: FragmentNewEventBinding) {
        binding.newEventGoBack.setOnClickListener { view: View ->
            Navigation.findNavController(view)
                .navigate(R.id.action_newEvent_to_navigation_events)
        }

        // route to choose komyuniti
        binding.komyunitiInputCard.setOnClickListener {
            Navigation.findNavController(it)
                .navigate(R.id.action_newEvent_to_addKomyunitiFragment)
        }

        // route to choose members
        binding.membersInputCard.setOnClickListener {
            Navigation.findNavController(it)
                .navigate(R.id.action_newEvent_to_newEventAddMembersFragment)
        }

    }

}