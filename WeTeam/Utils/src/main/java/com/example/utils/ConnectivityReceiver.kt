package com.example.utils


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager

class ConnectivityReceiver(): BroadcastReceiver() {


    override fun onReceive(context: Context?, intent: Intent?) {

        if(MyApplication.currentContext != null){
            MyApplication.showNetworkMessage(isConnectedOrConnecting(MyApplication.currentContext!!))
        }

    }

    companion object {

        fun isConnectedOrConnecting(context: Context): Boolean {
            val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connMgr.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnectedOrConnecting
        }
    }
}
