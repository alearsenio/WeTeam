package com.example.messages.chat

import com.google.firebase.database.IgnoreExtraProperties
import java.sql.Timestamp

@IgnoreExtraProperties
data class Message(var name: String? = "", var text: String? = "", var timestamp: Long? = 0){
    override fun toString(): String {
        return "{$name, $text, $timestamp}"
    }
}