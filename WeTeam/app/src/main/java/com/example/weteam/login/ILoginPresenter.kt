package com.example.weteam.login


interface ILoginPresenter {
    fun verifyLogin(email: String,password: String)
}