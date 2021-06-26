package com.example.komyuniti.ui.profile

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.komyuniti.R

class KomyunitiListAdapter(private val dataSet: MutableList<KomyunitiData>) :
    RecyclerView.Adapter<KomyunitiListAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val komyunitiName: TextView = view.findViewById(R.id.komyuniti_name)
        val komyunitiMembers: TextView = view.findViewById(R.id.komyuniti_members)

        init {
            // Define click listener for the ViewHolder's View.
            view.setOnClickListener { v:View ->
                val position: Int = adapterPosition
                Toast.makeText(v.context, "you clicked on the Komyuniti Item # ${position + 1}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.komyuniti_list_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.komyunitiName.text = dataSet[position].name
        viewHolder.komyunitiMembers.text = dataSet[position].members.toString() + " Members"

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}
