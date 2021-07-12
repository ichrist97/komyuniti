package com.example.komyuniti.ui.newEvent

import android.app.DatePickerDialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
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
import java.util.*


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
        showDatePicker()
        watchInput()

        navigation(binding)
        return binding.root
    }

    private fun watchInput() {
        // initialize with viewModel values
        binding.eventNameInput.setText(viewModel.name.value)
        binding.eventAddressInput.setText(viewModel.address.value)

        // event name
        binding.eventNameInput.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                // save new input in viewModel
                viewModel.name.postValue(s.toString())
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
            }
        })

        // event location
        binding.eventAddressInput.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                // save new input in viewModel
                viewModel.address.postValue(s.toString())
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
            }
        })
    }

    private fun showDatePicker() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        binding.eventDateInput.setOnClickListener {
            Log.d("DatePicker clicke", year.toString())
            val dpd = DatePickerDialog(
                requireContext(),
                DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->
                    //set in viewModel

                    viewModel.date.postValue("$mYear-$mMonth-$mDay")
                }, year, month, day
            )
            //show dialog
            dpd.show()
        }

        // show change in ui
        viewModel.date.observe(viewLifecycleOwner, {
            binding.eventDateInput.setText(it)
        })
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
            val members = viewModel.getMembers().value

            lifecycleScope.launch {
                val result: Event? = viewModel.createEvent(
                    apollo,
                    name,
                    date,
                    address = address,
                    komyunitiId = komyuniti?.id,
                    members = members
                )

                // on success route to created event
                if (result != null) {
                    // set eventId in shared view model
                    val eventViewModel =
                        ViewModelProvider(requireActivity()).get(EventViewModel::class.java)
                    eventViewModel.setEventId(result.id)

                    // empty current viewModel
                    viewModel.name.postValue("")
                    viewModel.date.postValue("")
                    viewModel.address.postValue("")
                    viewModel.setKomyuniti(null)
                    viewModel.setMembers(listOf())

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