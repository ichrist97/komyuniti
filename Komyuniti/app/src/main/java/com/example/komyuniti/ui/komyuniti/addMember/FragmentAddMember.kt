package com.example.komyuniti.ui.komyuniti.addMember

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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.apollographql.apollo.ApolloClient
import com.example.komyuniti.MainViewModel
import com.example.komyuniti.R
import com.example.komyuniti.databinding.FragmentAddKomyunitiMemberBinding
import com.example.komyuniti.databinding.FragmentEventsBinding
import com.example.komyuniti.ui.events.EventsViewModel
import com.example.komyuniti.ui.komyuniti.AddMemberAdapter
import com.example.komyuniti.ui.komyuniti.MemberAdapter
import com.example.komyuniti.ui.komyuniti.NewMemberAdapter
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class FragmentAddMember : Fragment() {

    private lateinit var viewModel: AddKomyunitiMemberViewModel
    private lateinit var binding: FragmentAddKomyunitiMemberBinding
    private lateinit var activityViewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProvider(requireActivity()).get(AddKomyunitiMemberViewModel::class.java)
        activityViewModel = ViewModelProvider(
            activity as ViewModelStoreOwner
        ).get(MainViewModel::class.java)
        binding = FragmentAddKomyunitiMemberBinding.inflate(inflater, container, false)
        val apollo = activityViewModel.getApollo(requireContext())

        loadFriends(apollo)
        displayFriends()
        displayNewMembers()
        finishAddMember(apollo)
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
                binding.addMemberRvStatus.visibility = GONE
                // set adapter
                val adapter = AddMemberAdapter(it, requireActivity(), viewLifecycleOwner)
                binding.newMemberRecyclerView.visibility = VISIBLE
                binding.newMemberRecyclerView.adapter = adapter
                adapter.setData(it)

                if (it.isEmpty()) {
                    binding.statusNoFriends.visibility = VISIBLE
                    binding.statusNoFriends.text = "No more friends to add"
                } else {
                    binding.statusNoFriends.visibility = GONE
                }
            } else {
                binding.addMemberRvStatus.visibility = VISIBLE
                binding.newMemberRecyclerView.visibility = GONE
            }
        })
    }

    private fun displayNewMembers() {
        viewModel.getNewMember().observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                binding.toAddRv.visibility = VISIBLE
                binding.addMemberDivider.visibility = VISIBLE
                // set adapter
                val adapter = NewMemberAdapter(it, requireActivity())
                binding.toAddRv.adapter = adapter
                adapter.setData(it)

                // show floating Button
                binding.btnFloatAddMember.visibility = VISIBLE
            } else {
                binding.toAddRv.visibility = GONE
                binding.addMemberDivider.visibility = GONE
                binding.btnFloatAddMember.visibility = GONE
            }
        })
    }

    private fun finishAddMember(apollo: ApolloClient) {
        binding.btnFloatAddMember.setOnClickListener { view: View ->
            val data = viewModel.getNewMember().value
            val komyuniti = viewModel.getKomyuniti().value
            if (data != null && data.isNotEmpty() && komyuniti != null) {
                viewModel.addMembers(apollo, komyuniti.id, data)
            } else {
                Toast.makeText(activity, "Could not add members", Toast.LENGTH_LONG).show()
            }

            // add result observer
            viewModel.getFinishedAdding().observe(viewLifecycleOwner, { result: Boolean ->
                if (result) {
                    Toast.makeText(activity, "Added new members to komyuniti", Toast.LENGTH_LONG)
                        .show()

                    // empty current data containers
                    viewModel.setNewMember(listOf())

                    // route back to komyuniti
                    Navigation.findNavController(view)
                        .navigate(R.id.action_fragmentAddMember_to_komyunitiFragment)
                }
            })
        }
    }

    private fun initNavigation() {
        // route back to komyuniti
        binding.btnKomyunitiAddMemberGoBack.setOnClickListener {
            Navigation.findNavController(it)
                .navigate(R.id.action_fragmentAddMember_to_komyunitiFragment)
        }
    }
}