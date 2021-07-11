package com.example.komyuniti.ui.events.chat

import android.content.Context
import android.content.SharedPreferences
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
import com.example.komyuniti.MainViewModel
import com.example.komyuniti.R
import com.example.komyuniti.databinding.FragmentChatBinding
import com.example.komyuniti.models.ChatMessage
import com.example.komyuniti.models.User
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.lang.Exception

class ChatFragment : Fragment() {

    private lateinit var viewModel: ChatViewModel
    private lateinit var activityViewModel: MainViewModel
    private lateinit var binding: FragmentChatBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activityViewModel = ViewModelProvider(
            activity as ViewModelStoreOwner
        ).get(MainViewModel::class.java)
        viewModel = ViewModelProvider(requireActivity()).get(ChatViewModel::class.java)
        binding = FragmentChatBinding.inflate(inflater, container, false)
        val apollo = activityViewModel.getApollo(requireContext())

        // show event name
        binding.chatEventName.text = viewModel.getEvent().value?.name

        // show chat messages
        lifecycleScope.launch {
            val currentUser = viewModel.getCurrentUser(apollo)
            if (currentUser == null) {
                Toast.makeText(activity, "Chat cannot be displayed", Toast.LENGTH_SHORT).show()
                this.cancel()
            }

            // get current event
            val eventId = viewModel.getEvent().value?.id
            if (eventId == null) {
                Toast.makeText(activity, "Chat cannot be displayed", Toast.LENGTH_SHORT).show()
                this.cancel()
            }

            // load existing chat messages
            val msgs = viewModel.getChatMsgs(apollo, eventId as String)
            if (msgs == null) {
                Toast.makeText(activity, "Failed loading chat", Toast.LENGTH_SHORT).show()
                this.cancel()
            }
            viewModel.data.value = msgs

            // init chatAdapter
            val chatAdapter = ChatAdapter(viewModel.data.value, currentUser as User)
            binding.chatMsgRecycleView.adapter = chatAdapter

            // observe messages for updates
            viewModel.data.observe(viewLifecycleOwner, {
                // update chat messages
                chatAdapter.setData(it)
            })

            // start subscription to listen to chat
            viewModel.subscribeChat(apollo, eventId)
        }

        initNavigation()
        initSendMsg()

        val root: View = binding.root

        return root
    }

    private fun initSendMsg() {
        binding.btnSendMsg.setOnClickListener {
            // get message content
            val text = binding.editChatMsg.text.toString()

            // only continue of text not empty or null
            if (text != "") {
                // current event
                val eventId = viewModel.getEvent().value?.id

                val apollo = activityViewModel.getApollo(requireContext())

                lifecycleScope.launch {
                    val chatMsg = viewModel.sendChatMsg(apollo, eventId as String, text)
                    if (chatMsg == null) {
                        Toast.makeText(activity, "Failed sending message", Toast.LENGTH_SHORT)
                            .show()
                        this.cancel()
                    }

                    // append message to displayed messages
                    viewModel.data.value = viewModel.data.value?.plus(chatMsg as ChatMessage)
                }
            }
        }
    }

    private fun initNavigation() {
        // leave chat and go back to event
        binding.btnLeaveChat.setOnClickListener {
            Navigation.findNavController(it)
                .navigate(R.id.action_chatFragment_to_eventFragment)
        }
    }

}