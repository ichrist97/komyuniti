package com.example.komyuniti.ui.friendProfile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.Navigation
import com.example.komyuniti.MainViewModel
import com.example.komyuniti.R
import com.example.komyuniti.databinding.FragmentFriendProfileBinding
import java.time.format.DateTimeFormatter

class FriendProfileFragment : Fragment() {

    private lateinit var viewModel: FriendProfileViewModel
    private lateinit var activityViewModel: MainViewModel
    private var binding: FragmentFriendProfileBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activityViewModel = ViewModelProvider(
            activity as ViewModelStoreOwner
        ).get(MainViewModel::class.java)
        viewModel =
            ViewModelProvider(requireActivity()).get(FriendProfileViewModel::class.java)
        binding = FragmentFriendProfileBinding.inflate(inflater, container, false)

        initUserInfo()
        initNavigation()

        return binding!!.root
    }

    private fun initUserInfo() {
        val friendId = viewModel.getFriendId().value
        if (friendId == null) {
            Toast.makeText(activity, "Cannot display this friend", Toast.LENGTH_LONG).show()
            return
        }

        viewModel.fetchFriend(activityViewModel.getApollo(requireContext()), friendId)
        viewModel.getUser().observe(viewLifecycleOwner, {
            if (it != null) {
                binding!!.friendProfileName.text = it.name

                // parse date to time
                val dateStr = it.createdAt?.format(
                    DateTimeFormatter.ofPattern("dd.MM.yyyy")
                )
                binding!!.tvMemberSince.text = dateStr
            } else {
                Toast.makeText(activity, "Cannot display this friend", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun initNavigation() {
        binding!!.btnFriendProfileGoBack.setOnClickListener {
            Navigation.findNavController(it)
                .navigate(R.id.action_friendProfileFragment_to_navigation_profile)
        }
    }
}