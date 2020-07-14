package com.nachohseara.beermastersshop.ui

import android.os.Bundle
import android.widget.Button
import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.base.BaseActivity
import com.nachohseara.beermastersshop.ui.account.MainActivity
import com.nachohseara.beermastersshop.utils.LoadingDialog

class NoConnectionActivity : BaseActivity() {

    override val loadingDialog = LoadingDialog(this)
    override fun getActName(): Int = NOCON_ACT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_connection)

        val btTry: Button = findViewById(R.id.btTryConnection)
        btTry.setOnClickListener {
            if (onlineOrMsg()) {
                openActivity(MainActivity::class.java, true)
            }
        }
    }
}
