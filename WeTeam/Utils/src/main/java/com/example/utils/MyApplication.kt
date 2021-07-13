package com.example.utils

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.IntentFilter

import android.net.ConnectivityManager
import android.view.View
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar


class MyApplication: Application() {


    companion object {

        var instance: MyApplication? = null

        var currentContext: Context? = null

        var currentView: View? = null

        var currentSnackbar: Snackbar? = null

        fun setContextandVIew(context: Context, view: View){
            currentContext = context
            currentView = view
            if(currentContext != null)
                showNetworkMessage(ConnectivityReceiver.isConnectedOrConnecting(context))
        }

        fun showNetworkMessage(isConnected: Boolean) {
            if (!isConnected) {
                currentSnackbar = Snackbar.make(
                    currentView!!,
                    "You are offline",
                    Snackbar.LENGTH_LONG
                ) //Assume "rootLayout" as the root layout of every activity.
                currentSnackbar?.duration = BaseTransientBottomBar.LENGTH_INDEFINITE
                currentSnackbar?.show()
            } else {
                currentSnackbar?.dismiss()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        registerReceiver(ConnectivityReceiver(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }


}