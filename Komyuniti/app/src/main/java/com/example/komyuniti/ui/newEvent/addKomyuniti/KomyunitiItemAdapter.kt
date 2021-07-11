package com.example.komyuniti.ui.newEvent.addKomyuniti

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.example.komyuniti.R
import com.example.komyuniti.models.Komyuniti

class KomyunitiItemAdapter(
    private var dataSet: List<Komyuniti>,
    private val activity: FragmentActivity,
    private val viewLifecycleOwner: LifecycleOwner
) :
    RecyclerView.Adapter<KomyunitiItemAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(
        view: View,
        data: List<Komyuniti>,
        activity: FragmentActivity,
        viewLifecycleOwner: LifecycleOwner
    ) : RecyclerView.ViewHolder(view) {
        val komyunitiName: TextView = view.findViewById(R.id.komyuniti_name)
        val cntMembers: TextView = view.findViewById(R.id.komyuniti_members)
        val image: ImageView = view.findViewById(R.id.komyuniti_image)
        val checked: ImageButton = view.findViewById(R.id.checkedKomyunitiStatus)

        init {
            // Define click listener for the ViewHolder's View.
            view.setOnClickListener {
                val position: Int = adapterPosition
                // set selected komyuniti in view model
                val viewModel =
                    ViewModelProvider(activity).get(AddKomyunitiViewModel::class.java)

                // add or remove
                val alreadySelected =
                    viewModel.getSelectedKomyuniti().value?.id == data[position].id
                viewModel.setSelectedKomyuniti(data[position]) // overwrite selected value

                viewModel.getSelectedKomyuniti().observe(viewLifecycleOwner, {
                    val nowSelected =
                        viewModel.getSelectedKomyuniti().value?.id == data[position].id
                    if (alreadySelected && nowSelected) {
                        checked.visibility = GONE
                    } else if (!alreadySelected && nowSelected) {
                        checked.visibility = VISIBLE
                    } else {
                        checked.visibility = GONE
                    }
                })
            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.komyuniti_list_item, viewGroup, false)

        return ViewHolder(view, dataSet, activity, viewLifecycleOwner)
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
