package com.nachohseara.beermastersshop.base

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.ui.NoConnectionActivity
import com.nachohseara.beermastersshop.utils.LoadingDialog
import com.nachohseara.beermastersshop.utils.NetworkUtils

@SuppressLint("Registered")
abstract class BaseActivity : AppCompatActivity() {
    companion object {
        const val MAIN_ACT = 0
        const val HOME_ACT = 1
        const val CART_ACT = 2
        const val DELACC_ACT = 3
        const val  NOCON_ACT = 4
    }

    abstract val loadingDialog: LoadingDialog

    fun isOnline() : Boolean = NetworkUtils.isOnline(this)

    fun notOnlineAndGo(act: Class<*> = NoConnectionActivity::class.java, finish: Boolean = true) : Boolean {
        if (!isOnline()) {
            openActivity(act, finish)
            return true
        }
        return false
    }

    fun onlineOrMsg() : Boolean {
        if (isOnline()) {
            return true
        }
        msgSnackBar(getString(R.string.no_connection))
        return false
    }

    fun openActivity(act: Class<*>, finish: Boolean) {
        val intent = Intent(this, act)
        startActivity(intent)
        if (finish) finish()
    }

    fun msgSnackBar(msg: String) {
        val parent : View = findViewById(android.R.id.content)
        Snackbar.make(parent, msg, Snackbar.LENGTH_LONG).show()
    }

    fun msgToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    fun startLoading() {
        loadingDialog.startDialog()
    }

    fun endLoading() {
        loadingDialog.endDialog()
    }

    abstract fun getActName() : Int
}