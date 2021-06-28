package com.example.komyuniti.ui.events

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.komyuniti.R


class EventAdapter(private val eventList: Array<EventData>?) :
    RecyclerView.Adapter<EventAdapter.ViewHolder>() {


    /**
     * Provide a reference to the type of views that you are using
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val notificationButton: Button
        val komyunitiName : TextView
        val eventName : TextView
        val numerOfPeopleInKomyuniti : TextView
        val date : TextView

        init {
            // Define click listener for the ViewHolder's View.
            notificationButton = view.findViewById(R.id.btn_notification)
            komyunitiName = view.findViewById(R.id.tv_event_item_title)
            eventName = view.findViewById(R.id.tv_event_name)
            numerOfPeopleInKomyuniti = view.findViewById(R.id.tv_event_item_number_of_people)
            date = view.findViewById(R.id.tv_event_item_date)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.event_list_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        if (eventList != null) {
            viewHolder.notificationButton.text = eventList[position].notificationNumber.toString()
            viewHolder.komyunitiName.text = eventList[position].komyunitiName
            viewHolder.eventName.text = eventList[position].eventName
            viewHolder.numerOfPeopleInKomyuniti.text = eventList[position].numberOfPeopleInKomyuniti.toString() + " People"
            viewHolder.date.text = eventList[position].date

        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        if (eventList != null) {
            return eventList.size
        }
        return 0
    }

}
