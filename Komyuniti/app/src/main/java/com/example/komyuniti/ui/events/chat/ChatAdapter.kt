package com.example.komyuniti.ui.events.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.komyuniti.R
import com.example.komyuniti.models.ChatMessage
import com.example.komyuniti.models.User
import java.time.format.DateTimeFormatter

class ChatAdapter(private var chatMessages: List<ChatMessage>?, private val currentUser: User) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_RECEIVED = 0
        const val TYPE_SENT = 1
    }

    interface BaseViewHolder {
        fun bindViews(item: ChatMessage)
    }

    /**
     * Provide a reference to the type of views that you are using
     */
    inner class ReceivedMsgHolder(view: View) : RecyclerView.ViewHolder(view), BaseViewHolder {
        val user: TextView
        val text: TextView
        val date: TextView
        val time: TextView

        init {
            // Define click listener for the ViewHolder's View.
            user = view.findViewById(R.id.chatOtherUserName)
            text = view.findViewById(R.id.chatOtherUserText)
            date = view.findViewById(R.id.chatOtherUserDate)
            time = view.findViewById(R.id.chatOtherUserTime)
        }

        override fun bindViews(item: ChatMessage) {
            // bind values to views
            user.text = item.sender.name
            text.text = item.text
            val dateStr = item.createdAt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            date.text = dateStr
            val timeStr = item.time.format(
                DateTimeFormatter.ofPattern("hh:mm")
            )
            time.text = timeStr
        }
    }

    inner class SentMsgHolder(view: View) : RecyclerView.ViewHolder(view), BaseViewHolder {
        val text: TextView
        val date: TextView
        val time: TextView

        init {
            // Define click listener for the ViewHolder's View.
            text = view.findViewById(R.id.editChatMsg)
            date = view.findViewById(R.id.chatOwnDate)
            time = view.findViewById(R.id.chatOwnTime)
        }

        override fun bindViews(item: ChatMessage) {
            // bind values to views
            text.text = item.text
            val dateStr = item.createdAt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            date.text = dateStr
            time.text = item.time
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (chatMessages!![position].sender.id) {
            // sender of message is current user
            currentUser.id -> TYPE_SENT
            // sender is some other user
            else -> TYPE_RECEIVED
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // Create a new view, which defines the UI of the list item
        return when (viewType) {
            TYPE_RECEIVED -> ReceivedMsgHolder(
                LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.chat_message_item_other, viewGroup, false)
            )
            TYPE_SENT -> SentMsgHolder(
                LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.chat_message_item_own, viewGroup, false)
            )
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        if (chatMessages != null) {
            (viewHolder as BaseViewHolder).bindViews(chatMessages!![position])
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        if (chatMessages != null) {
            return chatMessages!!.size
        }
        return 0
    }

    fun setData(chatMessages: List<ChatMessage>) {
        this.chatMessages = chatMessages
        notifyDataSetChanged()
    }
}