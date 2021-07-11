package com.example.komyuniti.ui.newEvent.addMembers

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
import com.example.komyuniti.databinding.FragmentNewEventAddMembersBinding
import com.example.komyuniti.ui.komyuniti.AddMemberAdapter
import com.example.komyuniti.ui.komyuniti.NewMemberAdapter
import com.example.komyuniti.ui.newEvent.NewEventViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class NewEventAddMembersFragment : Fragment() {

    private lateinit var viewModel: NewEventAddMembersViewModel
    private lateinit var binding: FragmentNewEventAddMembersBinding
    private lateinit var activityViewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProvider(requireActivity()).get(NewEventAddMembersViewModel::class.java)
        activityViewModel = ViewModelProvider(
            activity as ViewModelStoreOwner
        ).get(MainViewModel::class.java)
        binding = FragmentNewEventAddMembersBinding.inflate(inflater, container, false)
        val apollo = activityViewModel.getApollo(requireContext())

        loadFriends(apollo)
        displayFriends()
        displayNewMembers()
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
                binding.newEventChooseMembersRv.visibility = View.GONE
                // set adapter
                val adapter = ChooseMemberAdapter(it, requireActivity(), viewLifecycleOwner)
                binding.newEventChooseMembersRv.visibility = View.VISIBLE
                binding.newEventChooseMembersRv.adapter = adapter
                adapter.setData(it)

                if (it.isEmpty()) {
                    binding.newEventAddMemberRvStatus.visibility = View.VISIBLE
                    binding.newEventAddMemberRvStatus.text = "No more friends to add"
                } else {
                    binding.newEventAddMemberRvStatus.visibility = View.GONE
                }
            } else {
                binding.newEventAddMemberRvStatus.visibility = View.VISIBLE
                binding.newEventAddMemberRvStatus.text = "No more friends to add"
                binding.newEventChooseMembersRv.visibility = View.GONE
            }
        })
    }

    private fun displayNewMembers() {
        viewModel.getChosenMembers().observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                binding.newEventChosenMembersRv.visibility = View.VISIBLE
                binding.addMemberDivider.visibility = View.VISIBLE
                // set adapter
                val adapter = ChosenMemberAdapter(it, requireActivity())
                binding.newEventChosenMembersRv.adapter = adapter
                adapter.setData(it)
            } else {
                binding.newEventChosenMembersRv.visibility = View.GONE
                binding.addMemberDivider.visibility = View.GONE
            }
        })
    }

    private fun initNavigation() {
        binding.btnNewEventAddMembersGoBack.setOnClickListener {
            // go back
            Navigation.findNavController(it)
                .navigate(R.id.action_newEventAddMembersFragment_to_newEvent)
        }

        binding.btnNewEventAddMembersFinish.setOnClickListener {
            // pass selected value to newEventViewModel
            val members = viewModel.getChosenMembers().value
            if (members != null && members.isNotEmpty()) {
                val newEventViewModel =
                    ViewModelProvider(requireActivity()).get(NewEventViewModel::class.java)
                newEventViewModel.setMembers(members)

                // go back
                Navigation.findNavController(it)
                    .navigate(R.id.action_newEventAddMembersFragment_to_newEvent)
            } else {
                Toast.makeText(activity, "Need to select members", Toast.LENGTH_LONG).show()
            }
        }
    }
}