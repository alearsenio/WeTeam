package com.example.messages.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.utils.getUserId
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_chat.*
import java.util.*
import androidx.appcompat.app.AppCompatActivity
import com.example.messages.R
import com.example.messages.adapter.ChatAdapter


class ChatFragment : Fragment(), IChatView {

    private lateinit var database: DatabaseReference
    private lateinit var mChatPresenter: IChatPresenter
    lateinit var actionBar: ActionBar
    lateinit var mrecylerview: RecyclerView
    private var myId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onStart() {
        super.onStart()

        chatProgressbar.setVisibility(View.VISIBLE)
        mChatPresenter = ChatPresenter(this)

        val chatId = arguments!!.getSerializable("chatId") as Int

        actionBar = (activity as AppCompatActivity).supportActionBar!!
        actionBar!!.setDisplayHomeAsUpEnabled(true)

        database = FirebaseDatabase.getInstance().reference.child("messages")
        mrecylerview = activity!!.findViewById(R.id.messageRecyclerView)

        mrecylerview.layoutManager = LinearLayoutManager(activity)

        //mEventDetailPresenter.getEventDetail()


        sendButton.setOnClickListener {
            val message = Message(
                myId.toString(),
                messageEditText.text.toString(),
                Date().time
            )
            database.push().setValue(message)
            messageEditText.setText("")
        }

        if (context != null) {
            myId = getUserId(context!!)
        }

        firebaseData()
        chatProgressbar.setVisibility(View.GONE)
    }

    //ovveride onDestroy to hide back button
    override fun onDestroy() {
        actionBar!!.setDisplayHomeAsUpEnabled(false)
        super.onDestroy()
    }

    //set adapter
    fun firebaseData() {

        val option = FirebaseRecyclerOptions.Builder<Message>()
            .setQuery(database, Message::class.java)
            .setLifecycleOwner(this)
            .build()

        //set firebase adapter to fill recycleView and listen for new message
        val firebaseRecyclerAdapter = ChatAdapter(option, context!!, myId, database)

        mrecylerview.adapter = firebaseRecyclerAdapter
        firebaseRecyclerAdapter.startListening()

        //scroll to last position when a message arrive and you can see last message
        firebaseRecyclerAdapter.registerAdapterDataObserver(
            object : RecyclerView.AdapterDataObserver() {
                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    super.onItemRangeInserted(positionStart, itemCount)
                    val friendlyMessageCount = firebaseRecyclerAdapter.getItemCount()
                    val lastVisiblePosition =
                        (mrecylerview.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                    // If the recycler view is initially being loaded or the
                    // user is at the bottom of the list, scroll to the bottom
                    // of the list to show the newly added message.
                    if (lastVisiblePosition == -1 || positionStart >= friendlyMessageCount - 1 && lastVisiblePosition == positionStart - 1) {
                        mrecylerview.scrollToPosition(positionStart)
                    }
                }
            })
    }
}
