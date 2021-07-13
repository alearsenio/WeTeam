package com.example.agenda.eventDetail

interface IEventDetailPresenter {
    fun getEventDetail()
    fun addEventToCalendar()
    fun setReaded(isChecked: Boolean)
}