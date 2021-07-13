package com.example.agenda.agenda

import android.os.Bundle
import android.util.Log

import android.widget.ExpandableListView
import android.widget.SearchView
import com.example.agenda.adapter.ExpandableEventListAdapter
import com.example.utils.*
import com.google.gson.Gson
import android.view.View
import com.example.agenda.R
import com.example.agenda.eventDetail.EventDetailFragment
import kotlinx.android.synthetic.main.activity_agenda.*
import kotlinx.android.synthetic.main.fragment_event_detail.*

class AgendaActivity : OptionMenuActivity(), IAgendaView{

    private lateinit var filePath: String
    internal var expandableListView: ExpandableListView? = null
    internal var adapter: ExpandableEventListAdapter? = null
    private lateinit var mAgendaPresenter: IAgendaPresenter
    lateinit var savedPayload : Payload


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(getSavedTheme())
        setContentView(R.layout.activity_agenda)

        searchDay.setOnClickListener{
            searchDay.setIconified(false)
            searchDay.requestFocus()}

        agendaProgressbar.setVisibility(View.VISIBLE)
        mAgendaPresenter = AgendaPresenter(this)

        val id = getUserId(this)
        filePath= id.toString() + "/agendaJson.txt"

        searchDay.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                if(adapter != null)
                     adapter!!.filter.filter(newText)
                return false
            }
        })
       mAgendaPresenter.getEventList()
    }

    override fun onResume() {
        super.onResume()
        //set custom application to implement offline toolbar
        MyApplication.setContextandVIew(this, findViewById(R.id.agenda_Activity))
    }


    //set the expandable list
    override fun setExpandableList(payload: Payload) {
        savedPayload = payload

        expandableListView = findViewById(R.id.expandableListView)
        if (expandableListView != null) {
            adapter = ExpandableEventListAdapter(this, payload.days)
            expandableListView!!.setAdapter(adapter)
            expandableListView!!.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->
                val event = adapter!!.getChild(groupPosition,childPosition)
                if(event.isNew) {
                    event.isNew = false
                    saveJsonOffline(getJsonString(payload), filePath, this)
                }
                changeReaded(this,v,event.isNew)

                val day = adapter!!.getGroup(groupPosition)
                val isNew = day.checkNewEvents()
                val groupView = getGroupView(expandableListView!!,groupPosition)
                changeGroupReaded(this,groupView,isNew)
                launchDetailFragment(event,day)
                false
            }
        }
        //expande all days the are after or equals of today
        for(index in 0 until adapter!!.groupCount ) {
            if(adapter!!.getGroup(index).timestamp.toLong() >System.currentTimeMillis())
                expandableListView!!.expandGroup(index)
        }

        agendaProgressbar.setVisibility(View.GONE)

    }


    //get json string from payload class
    fun getJsonString(payload: Payload): String{
        val gson = Gson()
        val jsonString = "{\"payload\":" + gson.toJson(payload) + "}"
        return jsonString
    }

    //get the view of the group clicked
    fun getGroupView(listView: ExpandableListView, groupPosition: Int): View {
        val packedPosition = ExpandableListView.getPackedPositionForGroup(groupPosition)
        val flatPosition = listView.getFlatListPosition(packedPosition)
        val first = listView.firstVisiblePosition
        return listView.getChildAt(flatPosition - first)
    }

    //go to the detail fragment to see the detail of the event clicked
    fun launchDetailFragment(event: Event, day: Day){

        val bundle = Bundle()
        bundle.putSerializable("event",event)
        bundle.putSerializable("day",day)

        val eventDetailFragment = EventDetailFragment()
        eventDetailFragment.arguments = bundle

        supportFragmentManager
            .beginTransaction()
            .add(R.id.agenda_Activity, eventDetailFragment)
            .addToBackStack("ciao")
            .commit()
    }

    fun setEventReaded(){
        if(savedPayload != null)
         saveJsonOffline(getJsonString(savedPayload), filePath, this)
        else
            Log.i("savejsonEventReade","cannot save json")
    }

}

