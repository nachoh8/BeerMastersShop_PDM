package com.nachohseara.beermastersshop.base

import android.os.Handler

abstract class LoaderFragment : BaseFragment() {
    fun loadInfoComplete(ms: Long = 500L) {
        Handler().postDelayed({
            actionPostComplete()
        }, ms)
    }

    abstract fun actionPostComplete()
}