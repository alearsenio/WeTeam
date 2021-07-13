package com.example.messages.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.messages.R
import com.example.messages.chat.Message
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import java.util.*

class ChatAdapter(option : FirebaseRecyclerOptions<Message>,var context: Context,var  myId: Int,
                  var database: DatabaseReference): FirebaseRecyclerAdapter<Message, ChatAdapter.MyViewHolder>(option) {

    private val MESSAGE_IN_VIEW_TYPE = 1
    private val MESSAGE_OUT_VIEW_TYPE = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        var itemView : View? = null
        if (viewType == MESSAGE_IN_VIEW_TYPE)
            itemView = LayoutInflater.from(context).inflate(R.layout.item_message_left,parent,false)
        else
            itemView = LayoutInflater.from(context).inflate(R.layout.item_message_right,parent,false)

        return MyViewHolder(itemView)
    }

    //see if message is sent or is arrived
    override fun getItemViewType(position: Int): Int {

        if(getItem(position).name == myId.toString())
            return MESSAGE_OUT_VIEW_TYPE
        else
            return MESSAGE_IN_VIEW_TYPE
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int, message: Message) {
        val placeid = getRef(position).key.toString()
        Log.i("message : ",message.toString())

        database.child(placeid).addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(context, "Error Occurred "+ p0.toException(), Toast.LENGTH_SHORT).show()

            }

            override fun onDataChange(p0: DataSnapshot) {
                // show_progress.visibility = if(itemCount == 0) View.VISIBLE else View.GONE


                if(message.timestamp != null) {
                    val date = Date(message.timestamp!!)
                    holder.messegerTextView.setText(date.toString().subSequence(0,16))
                }
                holder.messageTextView.setText(message.text)
                holder.messegerNameView.setText(message.name)


            }
        })
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var messageTextView: TextView = itemView!!.findViewById(R.id.messageText)
        internal var messegerTextView: TextView = itemView!!.findViewById(R.id.messageTime)
        internal var messegerNameView: TextView = itemView!!.findViewById(R.id.messageName)

    }
}

