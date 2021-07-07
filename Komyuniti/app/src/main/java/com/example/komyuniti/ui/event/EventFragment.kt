package com.example.komyuniti.ui.event

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.komyuniti.R
import com.example.komyuniti.databinding.FragmentEventBinding
import com.example.komyuniti.ui.events.EventAdapter
import com.example.komyuniti.ui.events.EventsViewModel

class EventFragment : Fragment() {

//    companion object {
//        fun newInstance() = EventFragment()
//    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    private lateinit var eventViewModel: EventViewModel
    private lateinit var eventBinding: FragmentEventBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        eventViewModel =
            ViewModelProvider(this).get(EventViewModel::class.java)

        eventBinding = FragmentEventBinding.inflate(inflater, container, false)


        val root: View = eventBinding.root

        return root
    }


}