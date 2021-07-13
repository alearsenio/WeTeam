package com.example.agenda.agenda

import java.io.Serializable
import java.util.*

class Event (var idEvent: Int,var dateFrom: String,var dateTo : String,var tipoEvento: String,var posizione: String,var ora: String,var isNew : Boolean): Serializable{


    override fun toString(): String {
        return "$idEvent $tipoEvento $dateFrom $dateTo $posizione $ora"
    }
}