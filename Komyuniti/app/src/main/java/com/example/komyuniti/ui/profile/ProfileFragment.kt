package com.example.komyuniti.ui.profile

import android.os.Bundle
import android.os.Debug
import android.util.Log
import android.util.Log.DEBUG
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.komyuniti.MainActivity
import com.example.komyuniti.R
import com.example.komyuniti.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel
    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var komyunitis = mutableListOf<KomyunitiData>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.profileHeaderTitle
        profileViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        //display all komyunitis connecting komyuniti Adapter
        binding.rvKomyunitiList.layoutManager = LinearLayoutManager(activity as MainActivity)
        binding.rvKomyunitiList.adapter = KomyunitiListAdapter(komyunitis)

        //val adapter = KomyunitiListAdapter(ProfileViewModel.komyunitiList.value)
        //binding.komyunitiList.adapter = adapter
        postToKomyuniti()
        return root

        logout(binding)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun logout(binding: FragmentProfileBinding) {
        binding.profileLogoutBtn.setOnClickListener{ view : View ->
            //TODO: connection to backend quit user session
            Navigation.findNavController(view).navigate(R.id.action_navigation_profile_to_loginFragment)
            (activity as MainActivity).setMainNavigationController()

        }
    }

    private fun addKomyuniti(komyuniti: KomyunitiData) {
        komyunitis.add(komyuniti)
    }
    private fun postToKomyuniti() {
            addKomyuniti(KomyunitiData())
            Log.d("ProfileFragment", KomyunitiData().toString())
            Log.d("ProfileFragment", komyunitis.toString())
    }

}