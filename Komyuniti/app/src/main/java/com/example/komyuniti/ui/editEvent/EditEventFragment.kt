package com.example.komyuniti.ui.editEvent

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
import com.example.komyuniti.databinding.FragmentEditEventBinding
import com.example.komyuniti.databinding.FragmentEventBinding
import com.example.komyuniti.ui.event.EventViewModel
import com.example.komyuniti.ui.newEvent.NewEventViewModel
import java.time.format.DateTimeFormatter

class EditEventFragment : Fragment() {

    private lateinit var editEventViewModel: EditEventViewModel
    private lateinit var binding: FragmentEditEventBinding
    private lateinit var activityViewModel: MainViewModel
    private lateinit var eventViewModel: EventViewModel
    private lateinit var newEventViewModel: NewEventViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        editEventViewModel = ViewModelProvider(requireActivity()).get(EditEventViewModel::class.java)
        eventViewModel = ViewModelProvider(requireActivity()).get(EventViewModel::class.java)
        newEventViewModel = ViewModelProvider(requireActivity()).get(NewEventViewModel::class.java)
        activityViewModel = ViewModelProvider(
            activity as ViewModelStoreOwner
        ).get(MainViewModel::class.java)
        binding = FragmentEditEventBinding.inflate(inflater, container, false)
        val apollo = activityViewModel.getApollo(requireContext())

        loadData(apollo)
        initNavigation()
        return binding.root
    }

    private fun initNavigation() {
        binding.editEventGoBack.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_editEventFragment_to_eventFragment)
        }
        binding.saveEditEvent.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_editEventFragment_to_eventFragment)
        }
    }

    private fun loadData(apollo: ApolloClient) {
        val eventId = eventViewModel.getEventId().value
        if (eventId != null) {
            // load data
            eventViewModel.fetchEvent(apollo, eventId)
            // set observer
            eventViewModel.getEvent().observe(viewLifecycleOwner, {
                if (it != null) {
                    binding.tiNameEvent.editText?.setText(it.name)
                    val dateStr = it.date?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                    binding.tiDateEvent.editText?.setText(dateStr)
                    binding.tiLocation.editText?.setText(it.address)

                    // show komyuniti in chips
                    if (it.komyuniti != null) {
                        binding.eventKomyunitiName.text = it.komyuniti.name
                        binding.eventKomyunitiName.visibility = View.VISIBLE
                        binding.noKomyunitiStatus.visibility = View.GONE
                    } else {
                        binding.eventKomyunitiName.visibility = View.GONE
                        binding.noKomyunitiStatus.visibility = View.VISIBLE
                    }
                    //show friend
                    newEventViewModel.getMembers().observe(viewLifecycleOwner, {
                        if (it != null) {
                            binding.newEventAddMembersText.text = "Change members"
                        } else {
                            binding.newEventAddMembersText.text = "Add members"
                        }
                    })
                    //add komyuniti
                    newEventViewModel.getKomyuniti().observe(viewLifecycleOwner, {
                        if (it != null) {
                            binding.newEventAddKomyunitiText.text = "Change komyuniti"
                        } else {
                            binding.newEventAddKomyunitiText.text = "Add komyuniti"
                        }
                    })

                }
            })
        } else {
            Toast.makeText(activity, "Cannot load event", Toast.LENGTH_LONG).show()
        }
    }
}