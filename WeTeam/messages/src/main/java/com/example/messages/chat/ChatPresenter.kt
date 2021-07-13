package com.example.messages.chat

import com.example.utils.VolleyRequestClass
import org.json.JSONObject

class ChatPresenter(val view: IChatView) : IChatPresenter, VolleyRequestClass.VolleyRequester {



    override fun parseJson(json: JSONObject?) {
    }

}