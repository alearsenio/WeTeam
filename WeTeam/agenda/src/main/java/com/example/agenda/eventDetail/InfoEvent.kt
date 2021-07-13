package com.example.agenda.eventDetail

class InfoEvent(var tipoInfo: String, var info1: String, var info2: String) {

    override fun toString(): String {
        return "$tipoInfo $info1 $info2"
    }
}