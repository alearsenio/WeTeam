package com.example.weteam.login

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log

import com.example.utils.VolleyRequestClass
import com.example.utils.isConnected
import com.example.weteam.R

import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.regex.Pattern

class LoginPresenter(val view: ILoginView) : ILoginPresenter, VolleyRequestClass.VolleyRequester {

    private var loginUrl: String
    private var volleyClass = VolleyRequestClass(view as Context)
    private lateinit var email: String
    private lateinit var password: String
    private var loginActivity: Activity

    init {
        loginActivity = view as Activity
        loginUrl = loginActivity.getString(R.string.loginUrl)
    }

    override fun verifyLogin(emailField: String, passwordField: String) {


            email = emailField
            password = passwordField
            volleyClass.volleyRequest(loginUrl, this, null)

    }

    override fun parseJson(json: JSONObject?) {
        if (json != null) {
            var checkName: String = ""
            var checkPassword: String = ""
            var found = false
            var jsonArray: JSONArray?
            var id = 0

            //check all the user and find one with same name of editText text
            if (json.has("users")) { //check if there is a "users" field
                jsonArray = json.getJSONArray("users")
                for (index in 0 until jsonArray.length()) {
                    if (jsonArray.getJSONObject(index).has("email")) {
                        checkName = jsonArray.getJSONObject(index).getString("email")
                    } else Log.i("JsonLog", "no json field email found")
                    if (checkName == email) {
                        if (jsonArray.getJSONObject(index).has("password"))
                            checkPassword = jsonArray.getJSONObject(index).getString("password")
                        id = jsonArray.getJSONObject(index).getInt("id")
                        Log.i("JsonLog", "no json field password found")
                        found = true
                        break
                    }
                }
            } else Log.i("JsonLog", "no json field users found")

            //if the email is valid and the user is found
            if (isEmailValid(email) && found) {
                if (password == checkPassword) {

                    val editor: SharedPreferences.Editor =
                        loginActivity.getSharedPreferences("login", Context.MODE_PRIVATE).edit()
                    editor.putInt("id", id)
                    editor.putBoolean("logged", true)
                    editor.commit()


                    val usersFolder = File(loginActivity.filesDir,"users")
                    if(usersFolder.exists()) {
                        val userFolder = File(usersFolder, id.toString())
                        userFolder.mkdir()
                    }else
                        Log.i("userFOder", "error")


                    view.onLoginSucess()

                } else view.onLoginError("incorrect email or password")
            }
        }
    }

    //check if the email is valid
    fun isEmailValid(email: String): Boolean {
        val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(email)
        if (matcher.matches()) {
            val delimeter = "@"
            val parts = email.split(delimeter)
            val domain = parts[1]
            if (domain == "juventus.com" || domain == "juventus.it")
                return true
        } else {
            view.onLoginError("invalid email")
            return false
        }
        return false
    }
}