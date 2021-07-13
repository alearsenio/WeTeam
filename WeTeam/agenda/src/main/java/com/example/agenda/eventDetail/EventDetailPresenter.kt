package com.example.agenda.eventDetail

import android.Manifest
import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.CalendarContract
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.agenda.R
import com.example.agenda.agenda.AgendaActivity
import com.example.agenda.agenda.Event
import com.example.utils.MyApplication
import com.example.utils.VolleyRequestClass
import com.example.utils.getUserId
import com.example.utils.saveJsonOffline
import com.google.gson.Gson
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONObject
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class EventDetailPresenter(val view: IEventDetailView) : IEventDetailPresenter,
    VolleyRequestClass.VolleyRequester {


    private var filePath: String
    private var loginUrl: String
    private var event: Event
    private var eventDetailFragment: Fragment
    private var volleyClass: VolleyRequestClass
    private lateinit var activity : AgendaActivity

    init {

        eventDetailFragment = view as Fragment
        volleyClass = VolleyRequestClass(eventDetailFragment.activity!!)
        eventDetailFragment.arguments
        event = eventDetailFragment.arguments!!.getSerializable("event") as Event
        activity = eventDetailFragment.activity as AgendaActivity
        val eventId = event.idEvent
        val id = getUserId(eventDetailFragment.activity!!)
        filePath = id.toString() + "/eventDetailJson" + eventId + ".txt"
        loginUrl = eventDetailFragment.getString(R.string.eventDetailUrl) + eventId
    }

    override fun getEventDetail() {
        volleyClass.volleyRequest(loginUrl, this, filePath)
    }

    //trasform the jsonobject into a class
    override fun parseJson(json: JSONObject?) {

        if (json != null) {
            if(json.has("eventDetail")) {
                val json2 = json.getJSONObject("eventDetail")
                val gson = Gson()
                val eventDetail = gson.fromJson(json2.toString(), EventDetail::class.java)

                view.setEventInfo(eventDetail)

                val jsonString = "{\"eventDetail\":" + gson.toJson(eventDetail) + "}"

                if (eventDetailFragment.context != null)
                    saveJsonOffline(jsonString, filePath, eventDetailFragment.context!!)
            }
        }
    }

    //add the event to the calendar
    override fun addEventToCalendar() {

        Log.i("calendar", "inizio metodo")
        val calID: Long? = getCalendarId(eventDetailFragment.activity!!)

        if (ContextCompat.checkSelfPermission(
                eventDetailFragment.context!!,
                Manifest.permission.WRITE_CALENDAR
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            //if there is a valid calendar id
            if (calID != null) {

                //do the task with another thread
                doAsync {
                    var endMillis: Long? = null
                    val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                    val dateFrom: Date = dateFormat.parse(event.dateFrom)
                    var startMillis = dateFrom.time

                    if (event.dateTo != null) {

                        val dateTo: Date = dateFormat.parse(event.dateTo)
                        endMillis = dateTo.time
                    } else endMillis = startMillis + 60000 * 60


                    Log.i("format date:", "$startMillis $endMillis")

                    val values = ContentValues().apply {
                        put(CalendarContract.Events.DTSTART, startMillis)
                        put(CalendarContract.Events.DTEND, endMillis)
                        put(
                            CalendarContract.Events.TITLE,
                            "${event.tipoEvento}, ${event.posizione}"
                        )
                        put(
                            CalendarContract.Events.DESCRIPTION,
                            "${event.tipoEvento}, ${event.posizione}"
                        )
                        put(CalendarContract.Events.CALENDAR_ID, calID)
                        put(CalendarContract.Events.EVENT_TIMEZONE, "Europe/Rome")
                    }
                    if (event.tipoEvento == "compleanno")
                        values.put(CalendarContract.Events.ALL_DAY, true)

                    var uri: Uri? = null
                    if (ContextCompat.checkSelfPermission(
                            eventDetailFragment.context!!,
                            Manifest.permission.WRITE_CALENDAR
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        uri = eventDetailFragment.context!!.contentResolver.insert(
                            CalendarContract.Events.CONTENT_URI,
                            values
                        )
                        uiThread {
                            Toast.makeText(
                                eventDetailFragment.activity!!.applicationContext,
                                " Event added on calendar",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Log.i("calendar", "no permission for calendar")
                        uiThread {
                            Toast.makeText(
                                eventDetailFragment.activity!!.applicationContext,
                                " Can't add event oncalendar",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    //if i insert an event an is not a birthday
                    if (uri != null && event.tipoEvento != "compleanno") {
                        val eventID: Long = uri.lastPathSegment!!.toLong()
                        values.clear()
                        values.apply {
                            put(CalendarContract.Reminders.EVENT_ID, eventID)
                            put(
                                CalendarContract.Reminders.METHOD,
                                CalendarContract.Reminders.METHOD_ALERT
                            )
                            put(CalendarContract.Reminders.MINUTES, 60)
                        }
                        eventDetailFragment.context!!.contentResolver.insert(
                            CalendarContract.Reminders.CONTENT_URI,
                            values
                        )
                    }
                }
            }else { //no calendar setted
                Toast.makeText(
                    eventDetailFragment.activity!!.applicationContext,
                    activity.getString(R.string.noCalendarFound),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else { // user have to set calendar permission on setting
            Toast.makeText(
                eventDetailFragment.activity!!.applicationContext,
                activity.getString(R.string.NoCalendarPermission),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    //get the calendar id of the user
    private fun getCalendarId(context: Context): Long? {
        val projection = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME
        )

        if (ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.READ_CALENDAR
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            var calCursor = context.contentResolver.query(
                CalendarContract.Calendars.CONTENT_URI,
                projection,
                CalendarContract.Calendars.VISIBLE + " = 1 AND " + CalendarContract.Calendars.IS_PRIMARY + "=1",
                null,
                CalendarContract.Calendars._ID + " ASC"
            )

            if (calCursor != null && calCursor.count <= 0) {
                calCursor = context.contentResolver.query(
                    CalendarContract.Calendars.CONTENT_URI,
                    projection,
                    CalendarContract.Calendars.VISIBLE + " = 1",
                    null,
                    CalendarContract.Calendars._ID + " ASC"
                )
            }

            if (calCursor != null) {
                if (calCursor.moveToFirst()) {
                    val calName: String
                    val calID: String
                    val nameCol = calCursor.getColumnIndex(projection[1])
                    val idCol = calCursor.getColumnIndex(projection[0])

                    calName = calCursor.getString(nameCol)
                    calID = calCursor.getString(idCol)

                    Log.d("ciaoooo", "Calendar name = $calName Calendar ID = $calID")

                    calCursor.close()
                    return calID.toLong()
                }
            }
        }
        return null
    }

    override fun setReaded(isChecked: Boolean) {
        event.isNew = isChecked
        activity.setEventReaded()
        if(activity.adapter!= null)
            activity.adapter!!.notifyDataSetChanged()
    }
}