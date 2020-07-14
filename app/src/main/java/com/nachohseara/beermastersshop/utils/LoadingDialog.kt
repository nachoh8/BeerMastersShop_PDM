package com.nachohseara.beermastersshop.utils

import android.app.Activity
import android.app.AlertDialog
import com.nachohseara.beermastersshop.R

class LoadingDialog(private val activity: Activity) {
    private lateinit var dialog: AlertDialog
    private var loading = false

    fun startDialog() {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        builder.setView(inflater.inflate(R.layout.loading_dialog, null))
        builder.setCancelable(false)
        dialog = builder.create()
        dialog.show()
        loading = true
    }

    fun endDialog() {
        if (loading) {
            dialog.dismiss()
            loading = false
        }
    }
}