package com.example.messages.ChatList


import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.messages.*
import com.example.messages.adapter.ChatListAdapter
import com.example.messages.chat.ChatFragment
import com.example.utils.MyApplication
import com.example.utils.OptionMenuActivity


class MessagesActivity : OptionMenuActivity(), IMessageView {



    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var mMessagesPresenter: IMessagesPresenter
    private val arrayOfChat = arrayListOf<Chat>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(getSavedTheme())
        setContentView(R.layout.activity_messages)

        mMessagesPresenter = MessagesPresenter(this)

        viewManager = LinearLayoutManager(this)
        viewAdapter = ChatListAdapter(arrayOfChat, this)
        recyclerView = findViewById<RecyclerView>(R.id.chatlistRecycleView).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        mMessagesPresenter.getPersonalChat()
    }

    override fun onResume() {
        super.onResume()
        //set custom application to implement offline toolbar
        MyApplication.setContextandVIew(this, findViewById(R.id.messages_Activity))
    }


    override fun setChats(mychats: Mychats) {
        if (mychats != null) {

            Log.i("chats:", mychats.toString())
            if (mychats.chats != null) {
                mychats.chats.forEach {
                    arrayOfChat.add(it)
                }
                viewAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun openChat(chatId: Int) {
        launchChatFragment(chatId)
    }

    //go to the detail fragment to see the detail of the event clicked
    fun launchChatFragment(chatId: Int){

        val bundle = Bundle()
        bundle.putSerializable("chatId",chatId)

        val eventDetailFragment = ChatFragment()
        eventDetailFragment.arguments = bundle

        supportFragmentManager
            .beginTransaction()
            .add(R.id.messages_Activity, eventDetailFragment)
            .addToBackStack("")
            .commit()
    }
}
