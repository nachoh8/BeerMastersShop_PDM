package com.nachohseara.beermastersshop.ui.home

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.base.BaseActivity
import com.nachohseara.beermastersshop.model.entity.User
import com.nachohseara.beermastersshop.utils.LoadingDialog


class HomeActivity : BaseActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    companion object {
        val setUser = setOf<Int>(R.id.nav_top_sales, R.id.nav_all, R.id.nav_orders, R.id.nav_profile)
        val setAdmin = setOf<Int>(R.id.nav_all, R.id.nav_payments, R.id.nav_profile)
    }

    private val controller = HomeController(this)
    override val loadingDialog = LoadingDialog(this)
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        controller.onCreate()
    }

    fun setUI(admin: Boolean) {
        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_home)

        val setMenu : Set<Int>
        if (admin) {
            val menu = navView.menu
            menu.findItem(R.id.nav_top_sales).isVisible = false
            menu.findItem(R.id.nav_orders).isVisible = false
            menu.setGroupVisible(R.id.menu_merchant, true)

            val inflater = navController.navInflater
            val graph = inflater.inflate(R.navigation.nav_admin)
            navController.graph = graph

            setMenu = setAdmin
        } else {
            setMenu = setUser
        }

        appBarConfiguration = AppBarConfiguration(setMenu.toSet(), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        displayHeaderNav()
    }

    fun setMenu(lock: Boolean) {
        val mode = if (lock) DrawerLayout.LOCK_MODE_LOCKED_CLOSED else DrawerLayout.LOCK_MODE_UNLOCKED
        drawerLayout.setDrawerLockMode(mode)
    }

    override fun getActName(): Int = HOME_ACT

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_home)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun displayHeaderNav() {
        val navView: NavigationView = findViewById(R.id.nav_view)
        val header = navView.getHeaderView(0)
        val txt: TextView = header.findViewById(R.id.txtDisplayName)
        txt.text = controller.getDisplayName()
        if (controller.isAdmin()) {
            val txtAdmin: TextView = header.findViewById(R.id.txtAdmin)
            txtAdmin.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        getSharedPreferences(User.file, Context.MODE_PRIVATE).registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == User.dataField ) displayHeaderNav()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
