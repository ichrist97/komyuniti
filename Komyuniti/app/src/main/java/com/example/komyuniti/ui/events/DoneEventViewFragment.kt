package com.example.komyuniti.ui.events

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.komyuniti.R
import com.example.komyuniti.databinding.DoneEventViewFragmentBinding

class DoneEventViewFragment : Fragment() {

    private lateinit var doneEventViewModel: DoneEventViewViewModel
    private lateinit var doneEventBinding: DoneEventViewFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        doneEventViewModel = ViewModelProvider(this).get(DoneEventViewViewModel::class.java)

        doneEventBinding = DoneEventViewFragmentBinding.inflate(inflater, container, false)

        val root: View = doneEventBinding.root

        initNavigation()

        return root
    }

    private fun initNavigation() {
        doneEventBinding.tvDoneEventBackBtn.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_doneEventViewFragment_to_navigation_events)
        }
    }

}