package com.example.agenda.agenda

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Day(var date: String,var timestamp: String,var events: ArrayList<Event>): Serializable{

    lateinit var dayString: String
    lateinit var month: String
    lateinit var year: String
    lateinit var dayInt: String
    var newEvents : Boolean = false


    //set data of day month and year with timestamp value
    fun setData(){
        val timestamp = timestamp.toLong()
        val date = Date(timestamp)

        dayString = SimpleDateFormat("EEEE", Locale.ITALIAN).format(date)
        month = SimpleDateFormat("yyyy",Locale.ITALIAN).format(date)
        year = SimpleDateFormat("MMMM",Locale.ITALIAN).format(date)
        dayInt = SimpleDateFormat("dd",Locale.ITALIAN).format(date).toInt().toString()
    }


    override fun toString(): String {
        var string = " $date $timestamp"
        if(events !=null)
            events.forEach { string += it.toString() + " " }
        return string
    }

    //check if it has new events
    fun checkNewEvents(): Boolean{
        newEvents = false
        events.forEach {
            if (it.isNew)
                newEvents = true
        }
        return newEvents
    }
}