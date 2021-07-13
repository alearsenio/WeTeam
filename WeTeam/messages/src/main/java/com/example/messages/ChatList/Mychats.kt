package com.example.messages.ChatList

class Mychats(var chats : ArrayList<Chat>){

    override fun toString(): String {
        var string : String = ""
        chats.forEach{
            string += it.toString() + " "
        }
        return string
    }
}