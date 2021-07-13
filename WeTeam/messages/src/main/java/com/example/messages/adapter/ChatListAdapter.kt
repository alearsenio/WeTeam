package com.example.messages.adapter

import android.content.Context
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView
import com.example.messages.ChatList.Chat
import com.example.messages.ChatList.IMessageView
import com.example.messages.R
import com.example.utils.setSafeOnClickListener

class ChatListAdapter(private val myDataset: ArrayList<Chat>, var context: Context) :
    RecyclerView.Adapter<ChatListAdapter.MyViewHolder>() {

    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): MyViewHolder {
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat, parent, false) as View

        return MyViewHolder(textView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val chatName = myDataset[position].name
        val chatId = myDataset[position].id

        val chatNameView =  holder.view.findViewById(R.id.chatNameTextView) as TextView
        val chatIconView =  holder.view.findViewById(R.id.chatIcon) as ImageView

        //search for theme color
        val typedValue = TypedValue()
        val theme = context.theme
        theme.resolveAttribute(com.example.utils.R.attr.themePrimary, typedValue, true)
        @ColorInt val color = typedValue.data
        //set the value to image
        chatIconView.setColorFilter(color)
        chatNameView.setText(chatName)

        holder.view.setSafeOnClickListener { (context as IMessageView).openChat(chatId) }
    }

    override fun getItemCount() = myDataset.size
}
