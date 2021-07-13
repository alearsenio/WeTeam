package com.example.messages.ChatList

import com.example.messages.ChatList.Mychats

interface IMessageView {
    fun setChats(mychats: Mychats)
    fun openChat(chatId: Int)
}