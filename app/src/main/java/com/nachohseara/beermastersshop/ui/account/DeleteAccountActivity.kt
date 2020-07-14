package com.nachohseara.beermastersshop.ui.account

import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.base.BaseActivity
import com.nachohseara.beermastersshop.utils.LoadingDialog

class DeleteAccountActivity : BaseActivity() {

    private val controller = DeleteAccountController(this)
    override val loadingDialog = LoadingDialog(this)

    private lateinit var progBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_account)

        controller.onCreate()
    }

    fun loadViews() {
        progBar = findViewById(R.id.progBar)

        val btConfirm: Button = findViewById(R.id.btConfirm)
        btConfirm.setOnClickListener {
            controller.onConfirm()
        }

        val btCancel: Button = findViewById(R.id.btCancel)
        btCancel.setOnClickListener {
            controller.onCancel()
        }
    }

    fun showChoiceDialog(title: String, msg: String) {
        AlertDialog.Builder(this).setTitle(title).setMessage(msg)
            .setNegativeButton("No", null)
            .setPositiveButton("Yes") { _, _ -> controller.onPositiveBtDialog()
            }.show()
    }

    override fun onBackPressed() {
        controller.onCancel()
    }

    override fun getActName(): Int = DELACC_ACT
}
