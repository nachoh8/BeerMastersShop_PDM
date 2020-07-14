package com.nachohseara.beermastersshop.ui.cart

import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.widget.Toolbar
import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.base.BaseActivity
import com.nachohseara.beermastersshop.model.entity.CartModel
import com.nachohseara.beermastersshop.utils.LoadingDialog

class CartActivity : BaseActivity() {
    override val loadingDialog = LoadingDialog(this)
    private lateinit var timer: CountDownTimer
    private val initTime = 420000L //in ms
    private var remaining = 0L
    var onWebView = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

        setTimer(initTime)
    }

    override fun onResume() {
        if (onWebView) { reanudeShoppingSession()}
        super.onResume()
    }

    override fun onDestroy() {
        cancelShoppingSession(false)
        super.onDestroy()
    }

    private fun setTimer(time: Long = 420000, interval: Long = 1000) { //in ms, default = 7min
        timer = object : CountDownTimer(time, interval) {
            override fun onFinish() {
                endShoppingSession(true)
            }
            override fun onTick(millisUntilFinished: Long) {
                remaining = millisUntilFinished
                //Log.d("CartActivity_Timer", "Segundos restantes: ${millisUntilFinished/1000}")
            }

        }
    }

    private fun startTimer(time: Long = 420000) {
        timer.start()
        var msg = ""
        val sec = time/1000
        msg = if (sec <= 60) {
            "You have less than 1 min to finish the purchase"
        } else {
            val min = sec/60
            "You have $min min to finish the purchase"
        }
        msgToast(msg)
    }

    private fun reanudeTimer() {
        setTimer(remaining)
        startTimer(remaining)
    }

    private fun cancelTimer() {
        timer.cancel()
    }

    fun startShoppingSession() {
        startTimer()
    }

    fun reanudeShoppingSession() {
        reanudeTimer()
    }

    fun cancelShoppingSession(delCart: Boolean = true) {
        if (delCart) {
            onWebView = false
            CartModel.clearSessionCart(this)
        }
        cancelTimer()
    }

    fun endShoppingSession(expired: Boolean) {
        if (expired) msgToast("Your session has expired, try it later")
        CartModel.clearSessionCart(this)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun getActName(): Int = CART_ACT
}
