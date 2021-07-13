package com.example.messages.ChatList

class Chat(var id: Int, var name: String){

    override fun toString(): String {
        var string = " $id $name"
        return string
    }
}