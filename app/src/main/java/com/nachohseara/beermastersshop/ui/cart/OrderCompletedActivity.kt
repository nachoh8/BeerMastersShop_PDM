package com.nachohseara.beermastersshop.ui.cart

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.base.BaseActivity
import com.nachohseara.beermastersshop.utils.LoadingDialog

class OrderCompletedActivity : BaseActivity() {
    companion object {
        const val FILE = "order_complete"
        const val FIELD = "cod"

        fun saveState(cod: Int, ctxt: Context) {
            val sp = ctxt.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            val editor = sp.edit()
            editor.putInt(FIELD, cod)
            editor.commit()
        }
        fun getState(ctxt: Context) : Int {
            val sp = ctxt.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            val cod = sp.getInt(FIELD, -1)
            sp.edit().clear().commit()

            return cod
        }
    }

    override val loadingDialog = LoadingDialog(this)

    override fun getActName(): Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_completed)

        val cod = getState(this)

        val btFinish: Button = findViewById(R.id.btGoHome)
        btFinish.setOnClickListener {
            finish()
        }

        val txtResult: TextView = findViewById(R.id.txtResult)
        val txtOperation: TextView = findViewById(R.id.txtOperation)

        if (cod == 1) {
            val txt = "${txtOperation.text}\n${getString(R.string.operation_ok)}"
            txtOperation.text = txt
            txtResult.text = getString(R.string.payment_ok)
        } else {
            val txt = "${txtOperation.text}\n${getString(R.string.operation_failure)}"
            txtOperation.text = txt
            txtResult.text = getString(R.string.error_data)
        }
    }
}
