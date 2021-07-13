package com.example.weteam.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.View
import androidx.annotation.ColorInt
import com.example.utils.OptionMenuActivity
import com.example.utils.isConnected
import com.example.utils.setSafeOnClickListener
import com.example.weteam.AuthentificationActivity
import com.example.weteam.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.agendaProgressbar
import java.io.File



class LoginActivity : OptionMenuActivity(), ILoginView {

    lateinit var loginUrl: String
    private lateinit var mLoginPresenter: ILoginPresenter

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(getSavedTheme())
        setContentView(R.layout.activity_main)

        //get the value of custom attrivute themeTextColor
        val typedValue = TypedValue()
        val theme = this.theme
        theme.resolveAttribute(com.example.utils.R.attr.themeTextColor, typedValue, true)
        @ColorInt val color = typedValue.data
        //set the value to the logo
        logoLogin.setColorFilter(color)
        //create users folder to store json file of users
        val file = File(filesDir,"users")
        file.mkdir()
        if(!file.exists())
        Log.i("users folder","error, cannot create user folder")

    }


    override fun onStart() {
        super.onStart()

        invalidateOptionsMenu()
        if (alreadyLogged())
            gotoAuthetication()

        mLoginPresenter = LoginPresenter(this)
        loginUrl = getString(R.string.loginUrl)

        //set the listener for login button to check if they are corrects
        loginButton.setSafeOnClickListener {
            if (isConnected(this)) {
                mLoginPresenter.verifyLogin(
                    email_field.text.toString(),
                    password_field.text.toString()
                )
                agendaProgressbar.setVisibility(View.VISIBLE)
            }else onLoginError("no internet connection")
        }
    }

    override fun onLoginSucess() {
        agendaProgressbar.setVisibility(View.GONE)
        gotoAuthetication()
    }

    override fun onLoginError(message: String) {
        agendaProgressbar.setVisibility(View.GONE)
        loginMessage.setText(message)
    }

    //go to authentification activity
    fun gotoAuthetication() {
        val intent = Intent(this, AuthentificationActivity::class.java)
        startActivity(intent)
        finish()
    }

    //check if the user is already logged with sharedPref
    fun alreadyLogged(): Boolean {
        val sharedPref: SharedPreferences = this.getSharedPreferences("login", Context.MODE_PRIVATE)
        val logged = sharedPref.getBoolean("logged", false)
        return logged
    }
}
