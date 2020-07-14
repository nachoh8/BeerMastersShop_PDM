package com.nachohseara.beermastersshop.ui.home

import com.nachohseara.beermastersshop.model.entity.User

class HomeController(private val myView: HomeActivity) {

    fun onCreate() {
        myView.setUI(User(myView).isAdmin())
    }

    fun getDisplayName() : String = User(myView).getName()

    fun isAdmin() : Boolean = User(myView).isAdmin()
}