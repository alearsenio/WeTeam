package com.example.agenda.agenda

class Payload(var days: ArrayList<Day>){

    override fun toString(): String {
        var string : String = ""
        days.forEach{
            string += it.toString() + " "
        }
        return string
    }
}