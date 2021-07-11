package com.example.komyuniti.ui.events

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.komyuniti.R
import com.example.komyuniti.models.Event
import com.example.komyuniti.ui.event.EventViewModel
import java.time.format.DateTimeFormatter


class EventAdapter(
    private var eventList: List<Event>,
    private val activity: FragmentActivity,
    private val navigationDest: Int
) :
    RecyclerView.Adapter<EventAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     */
    class ViewHolder(
        view: View,
        data: List<Event>,
        activity: FragmentActivity,
        navigationDest: Int
    ) :
        RecyclerView.ViewHolder(view) {
        val notificationButton: Button
        val komyunitiName: TextView
        val eventName: TextView
        val cntPeople: TextView
        val date: TextView

        init {
            // Define click listener for the ViewHolder's View.
            notificationButton = view.findViewById(R.id.btn_notification)
            komyunitiName = view.findViewById(R.id.tv_event_item_title)
            eventName = view.findViewById(R.id.tv_event_name)
            cntPeople = view.findViewById(R.id.tv_event_item_number_of_people)
            date = view.findViewById(R.id.tv_event_item_date)

            // route to event details view
            itemView.setOnClickListener {
                // set eventId in event detail viewModel
                val position: Int = adapterPosition
                val viewModel = ViewModelProvider(activity).get(EventViewModel::class.java)
                viewModel.setEventId(data[position].id)

                // route to details view
                val navController = Navigation.findNavController(it)
                navController.navigate(navigationDest)
            }
        }

        fun bind(eventItem: Event) {
            // bind data
            // TODO actual notification num
            notificationButton.text = "1"
            komyunitiName.text = eventItem.komyuniti?.name
            eventName.text = eventItem.name
            // TODO actual count of people in event
            cntPeople.text = ""

            // parse date to time
            val dateStr = eventItem.date?.format(
                DateTimeFormatter.ofPattern("dd.MM.yyyy")
            )
            date.text = dateStr
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.event_list_item, viewGroup, false)

        return ViewHolder(view, eventList, activity, navigationDest)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.bind(eventList[position])
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return eventList.size
    }

    fun setData(data: List<Event>) {
        this.eventList = data
        notifyDataSetChanged()
    }

}
