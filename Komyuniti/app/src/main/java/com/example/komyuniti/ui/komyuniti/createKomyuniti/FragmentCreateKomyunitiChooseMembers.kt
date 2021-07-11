package com.example.komyuniti.ui.komyuniti.createKomyuniti

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.apollographql.apollo.ApolloClient
import com.example.komyuniti.MainViewModel
import com.example.komyuniti.R
import com.example.komyuniti.databinding.FragmentCreateKomyunitiChooseMembersBinding
import com.example.komyuniti.ui.komyuniti.AddMemberAdapter
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class FragmentCreateKomyunitiChooseMembers : Fragment() {

    private lateinit var viewModel: CreateKomyunitiChooseMembersViewModel
    private lateinit var binding: FragmentCreateKomyunitiChooseMembersBinding
    private lateinit var activityViewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProvider(requireActivity()).get(CreateKomyunitiChooseMembersViewModel::class.java)
        activityViewModel = ViewModelProvider(
            activity as ViewModelStoreOwner
        ).get(MainViewModel::class.java)
        binding = FragmentCreateKomyunitiChooseMembersBinding.inflate(inflater, container, false)
        val apollo = activityViewModel.getApollo(requireContext())

        loadFriends(apollo)
        displayFriends()
        displayChosenMembers()
        initNavigation()

        return binding.root
    }

    private fun loadFriends(apollo: ApolloClient) {
        lifecycleScope.launch {
            val preferences = activity?.getSharedPreferences("Auth", Context.MODE_PRIVATE)!!
            val curUserId = preferences.getString("curUserId", null)
            if (curUserId == null) {
                Toast.makeText(activity, "Could not load friends", Toast.LENGTH_LONG).show()
                this.cancel()
            }

            viewModel.fetchFriends(apollo, curUserId as String)
        }
    }

    private fun displayFriends() {
        viewModel.getFriends().observe(viewLifecycleOwner, {
            if (it != null) {
                binding.chooseMemberRvStatus.visibility = View.GONE
                // set adapter
                val adapter = ChooseMemberAdapter(it, requireActivity(), viewLifecycleOwner)
                binding.chooseMemberRecyclerView.visibility = View.VISIBLE
                binding.chooseMemberRecyclerView.adapter = adapter
                adapter.setData(it)

                if (it.isEmpty()) {
                    binding.chooseMemberStatusNoFriends.visibility = View.VISIBLE
                    binding.chooseMemberStatusNoFriends.text = "You have no friends yet :-("
                } else {
                    binding.chooseMemberStatusNoFriends.visibility = View.GONE
                }
            } else {
                binding.chooseMemberRvStatus.visibility = View.VISIBLE
                binding.chosenMemberRecyclerView.visibility = View.GONE
            }
        })
    }

    private fun displayChosenMembers() {
        viewModel.getChosenMembers().observe(viewLifecycleOwner, {
            if (it != null && it.isNotEmpty()) {
                binding.chosenMemberRecyclerView.visibility = View.VISIBLE
                binding.chooseMemberDivider.visibility = View.VISIBLE
                // set adapter
                val adapter = ChosenMemberAdapter(it, requireActivity())
                binding.chosenMemberRecyclerView.adapter = adapter
                adapter.setData(it)

                // show floating Button
                binding.btnFloatGoToCreateKomyuniti.visibility = View.VISIBLE
            } else {
                binding.chosenMemberRecyclerView.visibility = View.GONE
                binding.chooseMemberDivider.visibility = View.GONE
                binding.btnFloatGoToCreateKomyuniti.visibility = View.GONE
            }
        })
    }

    private fun initNavigation() {
        // go back to profile
        binding.btnKomyunitiChooseMemberGoBack.setOnClickListener {
            // empty chosen chosenMembers
            viewModel.setChosenMembers(listOf())

            // route back to profile
            Navigation.findNavController(it)
                .navigate(R.id.action_fragmentCreateKomyunitiChooseMembers_to_navigation_profile)
        }

        // go to create komyuniti
        binding.btnFloatGoToCreateKomyuniti.setOnClickListener {
            // set chosen members in routed view model
            val chosenMembers = viewModel.getChosenMembers().value
            if (chosenMembers != null && chosenMembers.isNotEmpty()) {
                val createViewModel =
                    ViewModelProvider(requireActivity()).get(CreateKomyunitiViewModel::class.java)
                createViewModel.setMembers(chosenMembers)

                Navigation.findNavController(it)
                    .navigate(R.id.action_fragmentCreateKomyunitiChooseMembers_to_fragmentCreateKomyuniti)
            } else {
                Toast.makeText(activity, "Cannot create komyuniti from this", Toast.LENGTH_LONG).show()
            }

        }
    }
}