
package com.example.komyuniti.ui.settings

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.komyuniti.R
import com.example.komyuniti.databinding.FragmentFeedBinding
import com.example.komyuniti.databinding.FragmentRegisterBinding
import com.example.komyuniti.ui.feed.FeedViewModel

class SettingsFragment : Fragment() {

    private lateinit var feedViewModel: FeedViewModel
    private lateinit var fragmentFeedBinding : FragmentFeedBinding

    // This property is only valid between onCreateView and
    // onDestroyView.


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        feedViewModel =
            ViewModelProvider(this).get(FeedViewModel::class.java)

        fragmentFeedBinding = FragmentFeedBinding.inflate(inflater, container, false)

        // val adapter = FeedAdapter(feedViewModel.feedList.value)


        /*val textView: TextView = fragmentFeedBinding.textHome
        feedViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })*/

        val root: View = fragmentFeedBinding.root

        return root
    }
}
