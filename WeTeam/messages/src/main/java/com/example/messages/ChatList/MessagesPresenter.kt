package com.example.messages.ChatList

import android.app.Activity
import android.content.Context
import com.example.messages.R
import com.example.utils.VolleyRequestClass
import com.example.utils.getUserId
import com.example.utils.saveJsonOffline
import com.google.gson.Gson
import org.json.JSONObject

class MessagesPresenter(val view: IMessageView) : IMessagesPresenter,
    VolleyRequestClass.VolleyRequester {

    private var loginUrl: String
    private var volleyClass = VolleyRequestClass(view as Context)
    private lateinit var filePath: String
    private var messagesActivity: Activity

    init {
        messagesActivity = view as Activity
        val id = getUserId(messagesActivity)
        filePath = id.toString() + "/mychatJson.txt"
        loginUrl = messagesActivity.getString(R.string.mychatUrl) + id
    }


    override fun getPersonalChat() {
        volleyClass.volleyRequest(loginUrl, this, filePath)
    }

    override fun parseJson(json: JSONObject?) {

        if (json != null) {
            if (json.has("chats")) {
                val gson = Gson()
                val mychats = gson.fromJson(json.toString(), Mychats::class.java)

                view.setChats(mychats)

                val jsonString = gson.toJson(mychats)

                if (messagesActivity != null)
                    saveJsonOffline(jsonString, filePath, messagesActivity)
            }
        }
    }
}