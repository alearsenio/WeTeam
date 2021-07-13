package com.example.weteam.home

import org.json.JSONObject

interface IHomeView {
    fun fillHome(jsonObject: JSONObject)
}