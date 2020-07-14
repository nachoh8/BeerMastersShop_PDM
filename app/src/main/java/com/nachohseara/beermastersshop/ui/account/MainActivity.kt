package com.nachohseara.beermastersshop.ui.account

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.base.BaseActivity
import com.nachohseara.beermastersshop.model.db.FBModel
import com.nachohseara.beermastersshop.utils.LoadingDialog

class MainActivity : BaseActivity() {
    var onCreateAcc = false
    override val loadingDialog = LoadingDialog(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Handler().postDelayed({hideSplashScreen()}, 1000L)
    }

    override fun onBackPressed() {
        if (FBModel.islogin() && onCreateAcc) FBModel.logOut()
        super.onBackPressed()
    }

    private fun hideSplashScreen() {
        val nav_host_main: FragmentContainerView = findViewById(R.id.nav_host_main)
        nav_host_main.visibility = View.VISIBLE
        val splashLayout: ConstraintLayout = findViewById(R.id.splashLayout)
        splashLayout.visibility = View.GONE

    }

    override fun getActName(): Int = MAIN_ACT
}
