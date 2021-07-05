package com.example.komyuniti.ui.newEvent

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.komyuniti.R
import com.example.komyuniti.databinding.FragmentNewEventBinding
//import com.example.komyuniti.ui.settings.NewEventViewModel

class NewEvent : Fragment() {

    companion object {
        fun newInstance() = NewEvent()
    }

    private lateinit var newEventViewModel: NewEventViewModel
    private lateinit var fragmentNewEventBinding : FragmentNewEventBinding

    // This property is only valid between onCreateView and
    // onDestroyView.


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        newEventViewModel =
            ViewModelProvider(this).get(NewEventViewModel::class.java)

        fragmentNewEventBinding = FragmentNewEventBinding.inflate(inflater, container, false)

        val root: View = fragmentNewEventBinding.root

        navigation(fragmentNewEventBinding);
        return root
    }

    private fun navigation(binding: FragmentNewEventBinding) {
        binding.tvBack.setOnClickListener { view: View ->
            Navigation.findNavController(view)
                .navigate(R.id.action_newEvent_to_navigation_events)
        }

    }

}