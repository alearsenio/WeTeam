package com.example.utils


import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import java.io.File



open class OptionMenuActivity : AppCompatActivity() {

    //set the option menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)

        return true
    }

    //handle the click on option menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var volleyClass = VolleyRequestClass(this)
        return when (item.itemId) {
            R.id.logOut -> {
                volleyClass.volleyLogOutRequest(getString(R.string.logoutUrl), this)
                true
            }
            R.id.changeTheme-> {
            saveTheme()
                true
            }
            android.R.id.home ->{
                onBackPressed()
                window.decorView.hideKeyboard()
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    fun logout(response: String) {
        //if response from server is ok
        if (response.equals("200")) {

            //delete all file of the user
            val dir = File(filesDir.path + "/users/" + getUserId(this).toString())
            dir.deleteRecursively()

            //delete is id from sharedPreferences
            val sharedPref: SharedPreferences =
                this.getSharedPreferences("login", Context.MODE_PRIVATE)
            sharedPref.edit().remove("logged").commit()

            //intent to loginActivity, destroying all prev activities
            val intent = Intent(this, Class.forName(getString(R.string.loginActivityPath)))
            startActivity(intent)
            finishAffinity()
        } else { //response is not 200
            val fragmentManager = supportFragmentManager
            val fragment = FireMissilesDialogFragment()
            fragment.show(fragmentManager, "Sorry, can't Logout right now")
        }
    }

    //fragment for dialog when you are offline and you can't logout
    class FireMissilesDialogFragment : DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return this?.let {
                // Where we track the selected items
                val builder =
                    AlertDialog.Builder(it.context)
                // Set the dialog title
                builder.setTitle("Can't logout right now")
                    // Set the action buttons
                    .setPositiveButton("remain here",
                        DialogInterface.OnClickListener { dialog, id ->

                        })
                    .setNegativeButton("close the app",
                        DialogInterface.OnClickListener { dialog, id ->
                            val d = context as Activity
                            d.finishAffinity()
                        })

                builder.create()
            } ?: throw IllegalStateException("Activity cannot be null")
        }
    }

    fun saveTheme() {
        var value: String
        val theme = getSharedPreferences("theme",Activity.MODE_PRIVATE).getString("theme","dark")
        val editor = getSharedPreferences("theme",Activity.MODE_PRIVATE).edit()
        when (theme) {
            "dark" -> value = "light"
            "light" -> value = "dark"
            else -> value = "dark"
        }
        editor.putString("theme", value)
        editor.commit()
        recreate()
    }

    fun getSavedTheme(): Int {

        val theme = getSharedPreferences("theme",Activity.MODE_PRIVATE).getString("theme","dark")
        when (theme) {
            "dark" -> {
                return R.style.Theme_App_BaseLight}
            "light" -> {darkStatusBar()
                return R.style.Theme_App_Base}
            else -> return R.style.Theme_App_Base
        }
    }

    fun darkStatusBar(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val typedValue = TypedValue()
                val theme = this.theme
                theme.resolveAttribute(com.example.utils.R.attr.themeTextColor, typedValue, true)
                @ColorInt val color = typedValue.data
                window.statusBarColor = color

                window.decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        }
    }

    fun View.hideKeyboard() = this.let {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
}