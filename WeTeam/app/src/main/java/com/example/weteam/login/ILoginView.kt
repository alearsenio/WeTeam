package com.example.weteam.login

interface ILoginView {
    fun onLoginSucess()
    fun onLoginError(message: String)

}