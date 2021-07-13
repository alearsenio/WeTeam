package com.example.agenda.eventDetail

class EventDetail(var eventId: Int, var infoEvent: ArrayList<InfoEvent>) {

    override fun toString(): String {
        var string = " $eventId"
        if(infoEvent != null)
            infoEvent.forEach { string += it.toString() + " " }
        return string
    }
}