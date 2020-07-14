package com.nachohseara.beermastersshop.ui.home.payments

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.base.BaseActivity
import com.nachohseara.beermastersshop.model.entity.DateOrder
import java.util.*

class SearchPaymentsController(private val myView: SearchPaymentsFragment) {
    private lateinit var myAct: BaseActivity
    private var dateFrom = DateOrder.Empty()
    private var dateTo = DateOrder.Empty()

    fun onAttach() {
        myAct = myView.getBaseActivity()
    }

    fun onCreate() {
        if (!dateFrom.isSet()) {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            onPickDate(true, day, month, year)
            onPickDate(false, day, month, year)
        } else {
            myView.setDateFrom(dateFrom.transformStr())
            myView.setDateTo(dateTo.transformStr())
        }
    }

    fun onSearch() {
        if (myAct.onlineOrMsg() && checkFields()) {
            val args = Bundle()
            args.putString("from", DateOrder.toJSON(dateFrom))
            args.putString("to", DateOrder.toJSON(dateTo))
            myView.findNavController().navigate(R.id.action_searchPaymentsFragment_to_nav_payments, args)
        }
    }

    private fun checkFields() : Boolean {
        val strFrom = dateFrom.transformStr(true)
        val strTo = dateTo.transformStr(true)
        if (strFrom > strTo) {
            myView.msgSnackBar(myView.getString(R.string.error_dates))
            return false
        }
        return true
    }

    fun getDateFrom() : DateOrder = dateFrom
    fun getDateTo() : DateOrder = dateTo

    fun onPickDate(from: Boolean, day: Int, month: Int, year: Int) {
        val dateOrder = DateOrder(day, month, year)
        val res = dateOrder.transformStr()

        if (from) {
            dateFrom = dateOrder
            myView.setDateFrom(res)
        } else {
            dateTo = dateOrder
            myView.setDateTo(res)
        }
    }
}