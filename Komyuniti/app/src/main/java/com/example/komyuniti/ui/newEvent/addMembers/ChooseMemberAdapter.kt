package com.example.komyuniti.ui.newEvent.addMembers

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.komyuniti.R
import com.example.komyuniti.models.User
import com.example.komyuniti.ui.komyuniti.addMember.AddKomyunitiMemberViewModel

class ChooseMemberAdapter(
    private var dataSet: List<User>,
    private val activity: FragmentActivity,
    private val lifecycleOwner: LifecycleOwner
) :
    RecyclerView.Adapter<ChooseMemberAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(
        view: View,
        data: List<User>,
        activity: FragmentActivity,
        lifecycleOwner: LifecycleOwner
    ) :
        RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.new_member_name)
        val image: ImageView = view.findViewById(R.id.new_member_image)
        val btnAdded: ImageButton = view.findViewById(R.id.btnShowAdded)

        init {
            val viewModel =
                ViewModelProvider(activity).get(NewEventAddMembersViewModel::class.java)

            // Define click listener for the ViewHolder's View.
            view.setOnClickListener { v: View ->
                // add to new members live data in viewmodel
                // if not yet added
                val position: Int = adapterPosition
                if (!viewModel.getChosenMembers().value!!.contains(data[position])) {
                    viewModel.setChosenMembers(viewModel.getChosenMembers().value!!.plus(data[position]))

                    // show checkmark in ui
                    //btnAdded.visibility = VISIBLE
                } else {
                    // remove if already added
                    val filtered = viewModel.getChosenMembers().value?.filter {
                        it.id != data[position].id
                    }
                    viewModel.setChosenMembers(filtered!!)

                    // remove checkmark in ui
                    //btnAdded.visibility = GONE
                }
            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.add_member_list_item, viewGroup, false)

        return ViewHolder(view, dataSet, activity, lifecycleOwner)
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