package com.example.komyuniti.ui.newEvent.addKomyuniti

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.Navigation
import com.apollographql.apollo.ApolloClient
import com.example.komyuniti.MainViewModel
import com.example.komyuniti.R
import com.example.komyuniti.databinding.FragmentAddKomyunitiBinding
import com.example.komyuniti.databinding.FragmentNewEventBinding
import com.example.komyuniti.models.Komyuniti
import com.example.komyuniti.ui.newEvent.NewEventViewModel

class AddKomyunitiFragment : Fragment() {

    private lateinit var viewModel: AddKomyunitiViewModel
    private lateinit var binding: FragmentAddKomyunitiBinding
    private lateinit var activityViewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProvider(requireActivity()).get(AddKomyunitiViewModel::class.java)
        activityViewModel = ViewModelProvider(
            activity as ViewModelStoreOwner
        ).get(MainViewModel::class.java)
        binding = FragmentAddKomyunitiBinding.inflate(inflater, container, false)
        val apollo = activityViewModel.getApollo(requireContext())

        navigation()
        loadKomyunitis(apollo)

        return binding.root
    }

    private fun loadKomyunitis(apollo: ApolloClient) {
        val preferences = activity?.getSharedPreferences("Auth", Context.MODE_PRIVATE)!!
        val userId = preferences.getString("curUserId", null)

        if (userId != null) {
            viewModel.fetchKomyunitis(apollo, userId)
        } else {
            Toast.makeText(activity, "Could not load komyunitis", Toast.LENGTH_LONG).show()
        }

        // set observer
        viewModel.getKomyunities().observe(viewLifecycleOwner, {
            if (it != null) {
                binding.statusNoKomyunitisEvent.visibility = GONE
                binding.chooseKomyunitiEventRv.visibility = VISIBLE
                val adapter = KomyunitiItemAdapter(it, requireActivity(), viewLifecycleOwner)
                binding.chooseKomyunitiEventRv.adapter = adapter
                adapter.setData(it)
            } else {
                binding.statusNoKomyunitisEvent.text =
                    "You are not participating in any komyunities"
                binding.statusNoKomyunitisEvent.visibility = VISIBLE
                binding.chooseKomyunitiEventRv.visibility = GONE
            }
        })
    }

    private fun navigation() {
        binding.btnAddKomyunitiEventGoBack.setOnClickListener {
            // go back
            Navigation.findNavController(it)
                .navigate(R.id.action_addKomyunitiFragment_to_newEvent)
        }
        binding.btnAddKomyunitiEventFinish.setOnClickListener {
            // pass selected value to newEventViewModel
            val selectedKomyuniti = viewModel.getSelectedKomyuniti().value
            if(selectedKomyuniti != null) {
                val newEventViewModel = ViewModelProvider(requireActivity()).get(NewEventViewModel::class.java)
                newEventViewModel.setKomyuniti(selectedKomyuniti)

                // go back
                Navigation.findNavController(it)
                    .navigate(R.id.action_addKomyunitiFragment_to_newEvent)
            } else {
                Toast.makeText(activity, "Need to select a komyuniti", Toast.LENGTH_LONG).show()
            }

        }
    }

}