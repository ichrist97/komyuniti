package com.example.komyuniti.ui.profile

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.komyuniti.R
import com.example.komyuniti.models.ChatMessage
import com.example.komyuniti.models.User
import com.example.komyuniti.ui.friendProfile.FriendProfileViewModel


class FriendAdapter(private var dataSet: List<User>, private val activity: FragmentActivity) :
    RecyclerView.Adapter<FriendAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View, data: List<User>, activity: FragmentActivity) :
        RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.friend_name)
        val image: ImageView = view.findViewById(R.id.friend_image)

        init {
            // Define click listener for the ViewHolder's View.
            view.setOnClickListener { v: View ->
                val position: Int = adapterPosition

                // set friendId in friendProfile viewModel
                val viewModel = ViewModelProvider(activity).get(FriendProfileViewModel::class.java)
                viewModel.setFriendId(data[position].id)

                //  route to friends profile
                Navigation.findNavController(v)
                    .navigate(R.id.action_navigation_profile_to_friendProfileFragment)
            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.friend_list_item, viewGroup, false)

        return ViewHolder(view, dataSet, activity)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.name.text = dataSet[position].name
        viewHolder.image.setImageResource(R.drawable.profile)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

    fun setData(data: List<User>) {
        this.dataSet = data
        notifyDataSetChanged()
    }
}
