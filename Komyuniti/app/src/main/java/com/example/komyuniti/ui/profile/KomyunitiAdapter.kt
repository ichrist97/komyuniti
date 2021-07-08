package com.example.komyuniti.ui.profile

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.example.komyuniti.R
import com.example.komyuniti.models.Komyuniti
import com.example.komyuniti.models.User

class KomyunitiAdapter(
    private var dataSet: List<Komyuniti>,
    private val activity: FragmentActivity
) :
    RecyclerView.Adapter<KomyunitiAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View, activity: FragmentActivity) : RecyclerView.ViewHolder(view) {
        val komyunitiName: TextView = view.findViewById(R.id.komyuniti_name)
        val cntMembers: TextView = view.findViewById(R.id.komyuniti_members)
        val image: ImageView = view.findViewById(R.id.komyuniti_image)

        init {
            // Define click listener for the ViewHolder's View.
            view.setOnClickListener { v: View ->
                val position: Int = adapterPosition
                // TODO route to komyuniti detail view and set value in shared view model
                Toast.makeText(
                    v.context,
                    "you clicked on the Komyuniti Item # ${position + 1}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.komyuniti_list_item, viewGroup, false)

        return ViewHolder(view, activity)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.komyunitiName.text = dataSet[position].name
        viewHolder.cntMembers.text = "${dataSet[position].members?.size} members"
        viewHolder.image.setImageResource(R.drawable.profile)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

    fun setData(data: List<Komyuniti>) {
        this.dataSet = data
        notifyDataSetChanged()
    }

}
