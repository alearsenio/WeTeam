package com.example.weteam.home

import android.app.Activity
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.utils.*
import com.example.weteam.R
import com.example.weteam.adapter.ListAdapter
import kotlinx.android.synthetic.main.activity_home.*

import org.json.JSONObject



class HomeActivity : OptionMenuActivity(), IHomeView{

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var mHomePresenter: IHomePresenter
    private val arrayOfActivity = arrayListOf<String>()
    private lateinit var currentTheme : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(getSavedTheme())
        setContentView(R.layout.activity_home)

        currentTheme = getSharedPreferences("theme",Activity.MODE_PRIVATE).getString("theme","dark")!!
        agendaProgressbar.setVisibility(View.VISIBLE)
        mHomePresenter = HomePresenter(this)
        viewManager = GridLayoutManager(this, 3, GridLayoutManager.HORIZONTAL, false)
        viewAdapter = ListAdapter(arrayOfActivity, this)
        recyclerView = findViewById<RecyclerView>(R.id.rec_view).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
        mHomePresenter.getHomeIcon()
    }

    override fun onResume() {
        super.onResume()
        //set custom application to implement offline toolbar
        MyApplication.setContextandVIew(this, findViewById(R.id.home_activity))
        val theme = getSharedPreferences("theme", Activity.MODE_PRIVATE).getString("theme","dark")
        if(theme != currentTheme)
            recreate()
    }



    //fill the home with all the activity of the user
    override fun fillHome(json: JSONObject) {

        if (json.has("activity")) {
            val jsonArray = json.getJSONArray("activity")
            for (index in 0 until jsonArray.length()) {
                arrayOfActivity.add(jsonArray.getString(index))
            }
            println(arrayOfActivity.toString())
        } else Log.i("JsonLog", "no json field activity found")

        agendaProgressbar.setVisibility(View.GONE)
        viewAdapter.notifyDataSetChanged()
    }


}
