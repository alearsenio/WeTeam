package com.example.weteam.home

import android.app.Activity
import android.content.Context
import com.example.utils.VolleyRequestClass
import com.example.utils.getUserId
import com.example.utils.saveJsonOffline
import com.example.weteam.R
import org.json.JSONObject

class HomePresenter(val view: IHomeView) : IHomePresenter , VolleyRequestClass.VolleyRequester{

    private var loginUrl: String
    private var volleyClass = VolleyRequestClass(view as Context)
    private lateinit var filePath: String

    init {
        val homeActivity = view as Activity
        val id = getUserId(homeActivity)
        filePath= id.toString() + "/homeJson.txt"
        loginUrl = homeActivity.getString(R.string.activitiesUrl) + id
    }

    override fun getHomeIcon() {
        volleyClass.volleyRequest(loginUrl,this,filePath)
    }


    override fun parseJson(json: JSONObject?) {
        if(json!= null) {
            view.fillHome(json)
            saveJsonOffline(json.toString(), filePath, view as Activity)
        }
    }

}