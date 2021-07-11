package com.example.komyuniti.ui.komyuniti.createKomyuniti

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.Navigation
import com.apollographql.apollo.ApolloClient
import com.example.komyuniti.MainViewModel
import com.example.komyuniti.R
import com.example.komyuniti.databinding.FragmentCreateKomyunitiBinding

class FragmentCreateKomyuniti : Fragment() {

    private lateinit var viewModel: CreateKomyunitiViewModel
    private lateinit var binding: FragmentCreateKomyunitiBinding
    private lateinit var activityViewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProvider(requireActivity()).get(CreateKomyunitiViewModel::class.java)
        activityViewModel = ViewModelProvider(
            activity as ViewModelStoreOwner
        ).get(MainViewModel::class.java)
        binding = FragmentCreateKomyunitiBinding.inflate(inflater, container, false)
        val apollo = activityViewModel.getApollo(requireContext())

        initCreateKomyuniti(apollo)
        setUIData()
        initNavigation()

        return binding.root
    }

    private fun initCreateKomyuniti(apollo: ApolloClient) {
        binding.btnCreateKomyuniti.setOnClickListener {
            val name = binding.editKomyunitiName.text.toString()
            val members = viewModel.getMembers().value

            if (name.isNotEmpty()) {
                viewModel.createKomyuniti(apollo, name, members!!)
            } else {
                Toast.makeText(activity, "Give the komyuniti a name", Toast.LENGTH_LONG).show()
            }
        }

        // observe result of komyuniti creation
        viewModel.getCreatedKomyuniti().observe(viewLifecycleOwner, { result: Boolean ->
            if (result) {
                // empty chosenMembers in ChooseMemberViewModel
                val chooseViewModel = ViewModelProvider(requireActivity()).get(
                    CreateKomyunitiChooseMembersViewModel::class.java
                )
                chooseViewModel.setChosenMembers(listOf())

                // set back creation result
                viewModel.setCreatedKomyuniti(false)

                Toast.makeText(activity, "Komyuniti created!", Toast.LENGTH_LONG).show()
                // route back to profile
                Navigation.findNavController(binding.root)
                    .navigate(R.id.action_fragmentCreateKomyuniti_to_navigation_profile)
            }
        })
    }

    private fun setUIData() {
        // set dummy pic
        binding.komyunitiProfilePic.setImageResource(R.drawable.profile)

        // set chosen members
        val data = viewModel.getMembers().value
        val adapter = DisplayMemberAdapter(data!!, requireActivity())
        binding.createKomyunitiShowMembers.adapter = adapter
        viewModel.getMembers().observe(viewLifecycleOwner, {
            // update at change
            adapter.setData(it)
        })

        // show members count
        val cnt = data.size
        binding.showParticipantsCnt.text = "Participants: $cnt"
    }

    private fun initNavigation() {
        binding.btnCreateKomyunitiGoBack.setOnClickListener {
            // go back to choose participants
            Navigation.findNavController(it)
                .navigate(R.id.action_fragmentCreateKomyuniti_to_fragmentCreateKomyunitiChooseMembers)
        }
    }

}