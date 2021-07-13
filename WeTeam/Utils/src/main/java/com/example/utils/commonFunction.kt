package com.example.utils

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import java.io.File
import org.jetbrains.anko.doAsync
import org.json.JSONObject
import java.io.DataInput
import android.R.attr.data
import android.os.Build
import android.util.TypedValue
import androidx.annotation.ColorInt




fun getUserId(context: Context): Int {
    val sharedPref: SharedPreferences =
        context.getSharedPreferences("login", Context.MODE_PRIVATE)
    val id = sharedPref.getInt("id", 0)
    return id
}

fun isExternalStorageReadable(): Boolean {

    return Environment.getExternalStorageState() in
            setOf(Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY)
}

fun isConnected(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
    val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
    return  isConnected

}
fun saveJsonOffline(jsonText: String, filePath: String, context: Context) {

    if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        context.doAsync {
            //check external memory is avaible
            if (isExternalStorageReadable()) {

                Log.i("Path di salvataggio", filePath)
                val file: File = File(context.filesDir.path +"/users/" + filePath)
                Log.i("Path di salvataggio", file.path)
                if (!file.exists()) {
                    file.createNewFile()
                    file.writeText(jsonText)
                } else {
                    if (jsonText != file.readText())
                        file.writeText(jsonText)
                }
            }
        }
    } else {
        Log.i("CheckPermission", "You don't have permission to save")
    }
}

fun getOfflineJson(filePath: String,context: Context): JSONObject? {

    Log.i("JsonLog", "offline json")
    var json : JSONObject? = null
    val file: File = File(context.filesDir.path +"/users/" + filePath)
    if (file.exists()) {
        json = JSONObject(file.readText())
    } else {
        Log.i("JsonLog", "no offline json")
    }
    return json
}

//change the style of an event if is readed or not
fun changeReaded(context: Context, view: View,isNew: Boolean){

    val typedValue = TypedValue()
    val theme = context.theme
    theme.resolveAttribute(R.attr.themeChildColor, typedValue, true)
    @ColorInt val childColor = typedValue.data
    theme.resolveAttribute(R.attr.themeTextColor, typedValue, true)
    @ColorInt val textColor = typedValue.data

    val text1Id = context.resources.getIdentifier("info1","id",context.packageName)
    val text2Id = context.resources.getIdentifier("info2","id",context.packageName)

    val textView: TextView = view!!.findViewById(text1Id)
    val textView2: TextView = view!!.findViewById(text2Id)

    if(isNew) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.setBackgroundColor(context.resources.getColor(R.color.colorYellow, null))
            textView.setTextColor(context.resources.getColor(R.color.colorGrey, null))
            textView2.setTextColor(context.resources.getColor(R.color.colorGrey,null))
        }
            else {
                view.setBackgroundColor(context.resources.getColor(R.color.colorYellow))
                textView.setTextColor(context.resources.getColor(R.color.colorGrey))
                textView2.setTextColor(context.resources.getColor(R.color.colorGrey))
            }
    }else {
        view.setBackgroundColor(childColor)
        textView.setTextColor(textColor)
        textView2.setTextColor(textColor)
    }
}

//change the style of a day if is readed or not
fun changeGroupReaded(context: Context, view: View,isNew: Boolean){

    val typedValue = TypedValue()
    val theme = context.theme
    theme.resolveAttribute(R.attr.themeGroupColor, typedValue, true)
    @ColorInt val groupColor = typedValue.data
    theme.resolveAttribute(R.attr.themeTextColor, typedValue, true)
    @ColorInt val textColor = typedValue.data

    val dayIconId = context.resources.getIdentifier("dayIcon","id",context.packageName)
    val dayIcon: TextView = view!!.findViewById(dayIconId)

    if(isNew) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            dayIcon.setBackground(context.resources.getDrawable(R.drawable.round,null))
            dayIcon.setTextColor(context.resources.getColor(R.color.colorGrey,null))
        }else{
            dayIcon.setBackground(context.resources.getDrawable(R.drawable.round))
            dayIcon.setTextColor(context.resources.getColor(R.color.colorGrey))
        }
    }else {
        dayIcon.setBackgroundColor(groupColor)
        dayIcon.setTextColor(textColor)
    }
}


