package com.example.komyuniti.ui.komyuniti

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.komyuniti.R
import com.example.komyuniti.models.User
import com.example.komyuniti.ui.komyuniti.addMember.AddKomyunitiMemberViewModel

class NewMemberAdapter(
    private var dataSet: List<User>,
    private val activity: FragmentActivity
) :
    RecyclerView.Adapter<NewMemberAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(
        view: View,
        data: List<User>,
        activity: FragmentActivity,
    ) :
        RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.new_member_name)
        val image: ImageView = view.findViewById(R.id.new_member_image)

        init {
            // Define click listener for the ViewHolder's View.
            view.setOnClickListener { v: View ->
                val position: Int = adapterPosition
                // remove from new member
                val viewModel =
                    ViewModelProvider(activity).get(AddKomyunitiMemberViewModel::class.java)
                val filtered = viewModel.getNewMember().value?.filter {
                    it.id != data[position].id
                }
                viewModel.setNewMember(filtered!!)
            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.new_member_list_item, viewGroup, false)

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