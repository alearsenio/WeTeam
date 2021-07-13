package com.example.utils


import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class VolleyRequestClass(val context: Context) {

    private var headers = HashMap<String, String>()

    init {
        headers["Content-Type"] = context.getString(R.string.content_type)
        headers["JsonStub-User-Key"] = context.getString(R.string.user_Key)
        headers["JsonStub-Project-Key"] = context.getString(R.string.project_key)
    }


    //make a volley request to obtain the json of activity
    fun volleyRequest(loginUrl : String,volleyRequester : VolleyRequester,filePath: String?) {

        val headersCopy = headers
        val queue = Volley.newRequestQueue(context)
        val jsonObjectRequest = object : JsonObjectRequest(
            Method.GET, loginUrl, null,
            Response.Listener { response ->
                Log.i("JsonLog", "Response: %s".format(response.toString()))
                volleyRequester.parseJson(response)
            },
            Response.ErrorListener { error ->
                Log.i("JsonLog", "Error retrieving json")
                if(filePath !=null) {
                    val json = getOfflineJson(filePath,context)
                    if (json != null)
                        volleyRequester.parseJson(json)
                    else volleyRequester.parseJson(null)
                }else volleyRequester.parseJson(null)
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                return headersCopy
            }
        }
        queue.add(jsonObjectRequest)
    }

    //make a volley request to obtain the json of activity
    fun volleyLogOutRequest(loginUrl: String, context: OptionMenuActivity) {

        val headersCopy = headers
        val queue = Volley.newRequestQueue(context)
        val jsonObjectRequest = object : JsonObjectRequest(
            Method.GET, loginUrl, null,
            Response.Listener { response ->
                Log.i("Logout", "200")
                context.logout("200")
            },
            Response.ErrorListener { error ->
                Log.i("Logout", "Error")
                context.logout("error")
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                return headersCopy
            }
        }
        queue.add(jsonObjectRequest)
    }

    interface VolleyRequester{
        fun parseJson(json: JSONObject?)
    }
}