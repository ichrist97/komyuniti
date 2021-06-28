package com.example.komyuniti.ui.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.komyuniti.R


class FriendAdapter(private val dataSet: MutableList<FriendData>) :
    RecyclerView.Adapter<FriendAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.friend_name)
        val image: ImageView = view.findViewById(R.id.friend_image)

        init {
            // Define click listener for the ViewHolder's View.
            view.setOnClickListener { v:View ->
                val position: Int = adapterPosition
                Toast.makeText(v.context, "you clicked on the Friend Item # ${position + 1}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.friend_list_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.name.text = dataSet[position].name
/*        viewHolder.image.setImageResource(dataSet[position].image)
        viewHolder.image.setImageResource(dataSet[position].getImageUrl());*/
        viewHolder.image.setImageResource(R.drawable.profile)

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}
