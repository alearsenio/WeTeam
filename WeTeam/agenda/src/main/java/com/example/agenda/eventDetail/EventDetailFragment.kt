package com.example.agenda.eventDetail


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.agenda.R
import com.example.agenda.agenda.Day
import com.example.agenda.agenda.Event
import com.example.utils.setSafeOnClickListener
import kotlinx.android.synthetic.main.detail_item.view.*
import kotlinx.android.synthetic.main.fragment_event_detail.*


class EventDetailFragment : Fragment(), IEventDetailView {

    private lateinit var mEventDetailPresenter: EventDetailPresenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_detail, container, false)
    }

    override fun onStart() {
        super.onStart()

        detailProgressbar.setVisibility(View.VISIBLE)
        mEventDetailPresenter = EventDetailPresenter(this)

        val day = arguments!!.getSerializable("day") as Day
        val event = arguments!!.getSerializable("event") as Event

        Log.i("fragment event e day", "day : $day event: $event")

        setDescription(event)

        mEventDetailPresenter.getEventDetail()

        //set method when refreshing list
        swipeRefreshLayout.setOnRefreshListener { mEventDetailPresenter.getEventDetail() }

        //set closing fragment when pressing x button
        eventCloseButton.setSafeOnClickListener { activity!!.onBackPressed() }

        //set method when calendar button is clicked to add the event on calendar
        calendarButton.setOnClickListener { mEventDetailPresenter.addEventToCalendar() }

        readedCheckBox.setOnCheckedChangeListener { buttonView, isChecked -> mEventDetailPresenter.setReaded(isChecked) }
    }

    //insert all info of json class eventDetail into the xml in a list of info
    override fun setEventInfo(eventDetail: EventDetail) {

        if (activity != null) {
            deleteInfo()
            val detailItem: LinearLayout = activity!!.info_list as LinearLayout

            if (eventDetail.infoEvent != null) {
                eventDetail.infoEvent.forEach {
                    val detailView = layoutInflater.inflate(R.layout.detail_item, null)
                    val detail1: TextView = detailView.detail1 as TextView
                    val detail2: TextView = detailView.detail2 as TextView
                    val infoIcon: ImageView = detailView.infoIcon as ImageView
                    detail1.setText(it.info1)
                    detail2.setText(it.info2)
                    Glide.with(this)
                        .load(R.drawable.info)
                        .into(infoIcon)
                    detailItem.addView(detailView, 0)
                }
            }
            detailProgressbar.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false)
        }
    }


    //set the top descriptions in the xml
    fun setDescription(event: Event) {
        val descriptionIcon: ImageView = activity!!.descriptionIcon
        val description1: TextView = activity!!.description1
        val description2: TextView = activity!!.description2

        var id = 0

        description1.setText(event.tipoEvento)
        description2.setText(event.posizione)

        when (event.tipoEvento) {
            "compleanno" -> id = R.drawable.birthday
            "allenamento" -> id = R.drawable.workout
            else -> id = R.drawable.info
        }

        Glide.with(this)
            .load(id)
            .into(descriptionIcon)
    }

    //delete all info list item if you want to refresh
    fun deleteInfo() {
        val infoList: LinearLayout = activity!!.info_list as LinearLayout
        if (infoList.childCount > 2) {
            for (index in 0 until infoList.childCount - 2)
                infoList.removeViewAt(0)
        }

    }
}
