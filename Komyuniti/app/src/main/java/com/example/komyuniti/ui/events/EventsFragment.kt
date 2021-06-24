package com.example.komyuniti.ui.events

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.komyuniti.databinding.FragmentEventsBinding

class EventsFragment : Fragment() {

    private lateinit var eventsViewModel: EventsViewModel
    private lateinit var fragmentEventsBinding: FragmentEventsBinding

    // This property is only valid between onCreateView and
    // onDestroyView.
    //private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        eventsViewModel =
            ViewModelProvider(this).get(EventsViewModel::class.java)

        fragmentEventsBinding = FragmentEventsBinding.inflate(inflater, container, false)


        val adapter = EventAdapter(eventsViewModel.eventList.value)
        fragmentEventsBinding.rvEvents.adapter = adapter

        val root: View = fragmentEventsBinding.root


//        val textView: TextView = _binding.tvEvents
//        eventsViewModel.text.observe(viewLifecycleOwner, Observer { text ->
//            textView.text = text
//        })
        return root
    }
}

/*override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
}*/
