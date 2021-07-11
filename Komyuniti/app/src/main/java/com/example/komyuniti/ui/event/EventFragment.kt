package com.example.komyuniti.ui.event

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.apollographql.apollo.ApolloClient
import com.example.komyuniti.MainViewModel
import com.example.komyuniti.R
import com.example.komyuniti.databinding.FragmentEventBinding
import com.example.komyuniti.ui.events.chat.ChatViewModel
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

class EventFragment : Fragment() {

    private lateinit var viewModel: EventViewModel
    private lateinit var binding: FragmentEventBinding
    private lateinit var activityViewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProvider(requireActivity()).get(EventViewModel::class.java)
        activityViewModel = ViewModelProvider(
            activity as ViewModelStoreOwner
        ).get(MainViewModel::class.java)
        binding = FragmentEventBinding.inflate(inflater, container, false)
        val apollo = activityViewModel.getApollo(requireContext())

        initNavigation()
        initSettings(apollo)
        loadData(apollo)

        return binding.root
    }

    private fun loadData(apollo: ApolloClient) {
        val eventId = viewModel.getEventId().value
        if (eventId != null) {
            // load data
            viewModel.fetchEvent(apollo, eventId)
            // set observer
            viewModel.getEvent().observe(viewLifecycleOwner, {
                if (it != null) {
                    binding.eventHeaderName.text = it.name
                    binding.eventSmallName.text = it.name
                    val dateStr = it.date?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                    binding.eventDate.text = dateStr
                    binding.eventLocationName.text = it.address

                    // show komyuniti
                    if (it.komyuniti != null) {
                        binding.eventKomyunitiName.text = it.komyuniti.name
                        binding.eventKomyunitiName.visibility = VISIBLE
                        binding.noKomyunitiStatus.visibility = GONE
                    } else {
                        binding.eventKomyunitiName.visibility = GONE
                        binding.noKomyunitiStatus.visibility = VISIBLE
                    }

                }
            })
        } else {
            Toast.makeText(activity, "Cannot load event", Toast.LENGTH_LONG).show()
        }
    }

    private fun initSettings(apollo: ApolloClient) {
        binding.btnEventSettings.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), it)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.settingsDeleteEvent -> {
                        // open dialog to make sure user wants to delete
                        val builder = AlertDialog.Builder(requireContext())
                        builder.setMessage("Are you sure to delete this event? This action cannot be reversed!")
                            .setPositiveButton("Delete") { dialog, id ->
                                // delete komyuniti
                                val eventId = viewModel.getEventId().value as String
                                lifecycleScope.launch {
                                    val result = viewModel.deleteEvent(apollo, eventId)
                                    if (result) {
                                        Toast.makeText(
                                            activity,
                                            "Event deleted!",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        // navigate back to profile
                                        Navigation.findNavController(it)
                                            .navigate(R.id.action_eventFragment_to_navigation_events)
                                    } else {
                                        Toast.makeText(
                                            activity,
                                            "Could not delete this event",
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
            popupMenu.inflate(R.menu.event_menu)

            // show icons of menu items
            try {
                val fieldPopUp = PopupMenu::class.java.getDeclaredField("mPopup")
                fieldPopUp.isAccessible = true
                val mPopup = fieldPopUp.get(popupMenu)
                mPopup.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                    .invoke(mPopup, true)
            } catch (e: Exception) {
                Log.e("EventFragment", "Error showing icon")
            } finally {
                popupMenu.show()
            }
        }
    }

    private fun initNavigation() {
        // route to event overview
        binding.eventGoBack.setOnClickListener {
            Navigation.findNavController(it)
                .navigate(R.id.action_eventFragment_to_navigation_events)
        }

        // route to chat
        binding.flbtnChat.setOnClickListener {
            val event = viewModel.getEvent().value
            if (event != null) {
                // set event in chatViewModel
                val chatViewModel =
                    ViewModelProvider(requireActivity()).get(ChatViewModel::class.java)
                chatViewModel.setEvent(event)

                // route to chat
                Navigation.findNavController(it)
                    .navigate(R.id.action_eventFragment_to_chatFragment)
            } else {
                Toast.makeText(activity, "Cannot open the chat currently", Toast.LENGTH_LONG).show()
            }
        }
    }
}