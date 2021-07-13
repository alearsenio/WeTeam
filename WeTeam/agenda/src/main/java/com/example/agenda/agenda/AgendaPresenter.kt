package com.example.agenda.agenda

import android.app.Activity
import android.content.Context
import android.os.Environment
import com.example.agenda.R
import com.example.utils.VolleyRequestClass
import com.example.utils.getUserId
import com.example.utils.saveJsonOffline
import com.google.gson.Gson
import org.json.JSONObject

class AgendaPresenter(val view: IAgendaView): IAgendaPresenter, VolleyRequestClass.VolleyRequester {


    val filePath: String
    private var loginUrl: String
    private var volleyClass = VolleyRequestClass(view as Context)
    private var agendaActivity: Activity

    init {
        agendaActivity = view as Activity
        val id = getUserId(agendaActivity)
        filePath= id.toString() + "/agendaJson.txt"
        loginUrl = agendaActivity.getString(R.string.eventUrl) + id
    }


    //volley request to get json object
    override fun getEventList() {
        volleyClass.volleyRequest(loginUrl,this,filePath)
    }


    //from json creates a payload object with inside all days and events of json
    override fun parseJson(json: JSONObject?) {
        if (json != null) {
            if(json.has("payload")) {
                val json2 = json.getJSONObject("payload")
                val gson = Gson()
                val payload = gson.fromJson(json2.toString(), Payload::class.java)

                view.setExpandableList(payload)

                val jsonString = "{\"payload\":" + gson.toJson(payload) + "}"
                saveJsonOffline(jsonString, filePath, agendaActivity)
            }
        }
    }


}